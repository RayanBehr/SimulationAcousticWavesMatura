package model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

public class Simulation extends Thread {
	private double progress;
	private String current_task;

	//
	private int matrix_size;

	private double actuation_frequency;
	private double time_interval;

	private boolean[][] solid;

	private double[][][] position; // t, x, y
	private double[][][] velocity;

	private boolean should_start_stop_or_restart;

	final double amplitude = 100;
	final double damping = 0.9;

	private int iteration_index = 0;
	
	private boolean parallel =  false;

	/*
	 * @param matrix_size The size of the tile field
	 * 
	 * @param time_steps The number of time steps
	 */
	public Simulation(int matrix_size, int time_steps) {
		current_task = "Waiting";
		progress = 0;
		this.matrix_size = matrix_size;
		solid = new boolean[matrix_size][matrix_size];

		position = new double[time_steps][matrix_size][matrix_size]; // t, x, y,
		velocity = new double[time_steps][matrix_size][matrix_size]; // t, x, y, 4 faces

	}
	
	public void setParallelism(boolean parallel) {
		this.parallel = parallel;
	}

	/*
	 * Tells the simulation to break the current doings and go on to the next task
	 */
	public void startStopOrRestartEmulation() { // and also Restart
		should_start_stop_or_restart = true;
	}

	/*
	 * @return The string which is changed in different parts of the program.
	 */
	public String getCurrentTask() {
		return current_task;
	}

	/*
	 * @return The current progress in the task mentioned in getCurrentTask()
	 */
	public int getProgressPercent() {
		return (int) (progress * 100);
	}

	/*
	 * @return The iteration index in which the simulation is currently finalizing
	 */
	public int getIterationIndex() {
		return iteration_index;
	}

	/*
	 * @param actuation_frequency Sets the actuation frequency used
	 */
	public void setActuationFrequency(double actuation_frequency) {
		this.actuation_frequency = actuation_frequency;
	}

	/*
	 * @param time_interval Sets the time interval used
	 */
	public void setTimeInterval(double time_interval) {
		this.time_interval = time_interval;
	}

	/*
	 * @param positions Microphone index and then x and y [microphone][x,y]
	 * 
	 * @return The entire position data for the whole simulation at the positions
	 * regardless of completion
	 */
	public double[][] getPressureSamples(int[][] positions) {
		double[][] samples = new double[positions.length][position.length];
		for (int m = 0; m < samples.length; m++)
			for (int t = 0; t < samples[0].length; t++)
				samples[m][t] = position[t][positions[m][0]][positions[m][1]];
		return samples;
	}

	public double[][] getChangeSamples(int[][] positions) {
		double[][] samples = new double[positions.length][position.length];
		for (int m = 0; m < samples.length; m++)
			for (int t = 0; t < samples[0].length; t++)
				samples[m][t] = velocity[t][positions[m][0]][positions[m][1]];
		return samples;
	}

	/*
	 * Is never stopped and keeps the simulation alive.
	 */
	public void run() {
		boolean shouldTerminate = false; // should never terminate
		while (!shouldTerminate) {
			this.current_task = "Waiting";
			if (should_start_stop_or_restart) {
				should_start_stop_or_restart = false;
				emulate();
			}
		}
	}
	// Control End

	/*
	 * Makes the iterations happen
	 */
	private void emulate() {	
		this.current_task = "Iterating over time steps";
		progress = 0;
		for (int t = 0; t < position.length - 1; t++) {
			iteration_index = t;
			iterate(t);
			progress = t / position.length;
			if (should_start_stop_or_restart) {
				should_start_stop_or_restart = false;
				break;
			}
			progress = (t + 1) / (double) position.length;
		}		
	}

	private void iterate(int t) {
		setActuator(t);

		if(parallel)
		{
			parallel(t);
		}
		else 
		{
			serial(t);
		}
		
		iteration_index = t;
	}
	
	private void parallel(int t) {
		IntStream.range(0, matrix_size).parallel().forEach(y -> {
			//very pragmatic...
			for (int x = 0; x < matrix_size; x++) {
				iterateVelocity(t, x, y);
				iteratePosition(t, x, y);
				setBoundaries(t, x, y);
			}
		});
	}
	
	private void serial(int t) {
		for (int y = 0; y < matrix_size; y++) {
			for (int x = 0; x < matrix_size; x++) {
				iterateVelocity(t, x, y);
				iteratePosition(t, x, y);
				setBoundaries(t, x, y);
			}
		}
	}
	
	private void setBoundaries(int t, int x, int y) {
		if (x == 0 || x == matrix_size - 1 || y == 0 || y == matrix_size - 1) {
			if (solid[x][y]) {
				velocity[t + 1][x][y] = 0;
			}

			else {
				double acceleration = 0;
				
				double c = 0;

				double pM = position[t][x][y];
				double pL = 0;
				double pR = 0;
				double pB = 0;
				double pT = 0;

				// Left
				if (boundaryCheck(x - 1, y)) {
					pL = position[t][x - 1][y];
					c++;
				}

				// Right
				if (boundaryCheck(x + 1, y)) {
					pR = position[t][x + 1][y];
					c++;
				}

				// Below
				if (boundaryCheck(x, y + 1)) {
					pB = position[t][x][y + 1];
					c++;
				}

				// Top
				if (boundaryCheck(x, y - 1)) {
					pT = position[t][x][y - 1];
					c++;
				}

				acceleration = (c * pM - pL - pR - pB - pT) / c;
				
				velocity[t + 1][x][y] = velocity[t + 1][x][y] - acceleration;
			}
			
			position[t + 1][x][y] = position[t + 1][x][y] + 0.5 * velocity[t + 1][x][y] * damping;
		}
	}
	
	private void iteratePosition(int t, int x, int y) {	

		if (x != 0 || x != matrix_size - 1 || y != 0 || y != matrix_size - 1)
			position[t + 1][x][y] = position[t][x][y] - 0.5 * velocity[t + 1][x][y] * damping;

	}

	private void iterateVelocity(int t, int x, int y) {
		if (x != 0 || x != matrix_size - 1 || y != 0 || y != matrix_size - 1)
			if (solid[x][y]) {
				velocity[t + 1][x][y] = 0;
			}
	
			else {
				double acceleration = 0;
				
				double c = 0;
	
				double pM = position[t][x][y];
				double pL = 0;
				double pR = 0;
				double pB = 0;
				double pT = 0;
	
				// Left
				if (boundaryCheck(x - 1, y)) {
					pL = position[t][x - 1][y];
					c++;
				}
	
				// Right
				if (boundaryCheck(x + 1, y)) {
					pR = position[t][x + 1][y];
					c++;
				}
	
				// Below
				if (boundaryCheck(x, y + 1)) {
					pB = position[t][x][y + 1];
					c++;
				}
	
				// Top
				if (boundaryCheck(x, y - 1)) {
					pT = position[t][x][y - 1];
					c++;
				}
	
				acceleration = (c * pM - pL - pR - pB - pT) / c;
				
				velocity[t + 1][x][y] = velocity[t][x][y] + acceleration;
			}

	}

	// Checks if the address combination is in the bounds
	public boolean boundaryCheck(int x, int y) {
		if (x >= 0 && y >= 0) {
			if (x < matrix_size && y < matrix_size) {
				return true;
			}
		}
		return false;
	}
	
	
	private void setActuator(int t) {
		double omega = actuation_frequency * 2d * Math.PI;
		double p = amplitude * Math.sin(t * omega * time_interval);
		
		//Set the cells at t with p or -p in a rectangle
		
		//0
		/*
		for (int y = 141; y < 150; y++)
			position[t][95][y] = p;
		
		for (int y = 250; y < 259; y++)
			position[t][95][y] = -p;
			*/
		
		//3
		for (int x = 116; x < 191; x++)
			position[t][x][240] = p;
		for (int x = 116; x < 191; x++)
			position[t][x][241] = -p;
		
		
	}

	/*
	 * @param The path to your n*n *.jpg or *.png to convert to a solid mask
	 */
	public void setSolid(String path) throws IOException {
		final File file = new File(path);
		final BufferedImage image = ImageIO.read(file);

		int width = image.getWidth();
		int height = image.getHeight();

		boolean[][] visualData = new boolean[(int) (width)][(int) (height)];

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				final int clr = image.getRGB(x, y);
				final int red = (clr & 0x00ff0000) >> 16;
				final int green = (clr & 0x0000ff00) >> 8;
				final int blue = clr & 0x000000ff;

				if (red + green + blue < 240) {
					visualData[x][y] = true;
				} else {
					visualData[x][y] = false;
				}
			}
		}

		solid = visualData;
	}

	/*
	 * @param The time index of the data that has to be converted to visual data
	 * 
	 * @return The Color 2D Array representation of a time index
	 */
	public Color[][] getView(int timeIndex, double a, boolean view) {
		Color[][] visualData = new Color[matrix_size][matrix_size];

		for (int Tx = 0; Tx < matrix_size; Tx++) {
			for (int Ty = 0; Ty < matrix_size; Ty++) {

				int colorLevel = 0;
				if (view) {
					colorLevel = (int) (sigmoid(a, (velocity[timeIndex][Tx][Ty])) * 255);
				} else {
					colorLevel = (int) (sigmoid(a, (position[timeIndex][Tx][Ty])) * 255);
				}

				visualData[Tx][Ty] = new Color(255 - colorLevel, 0, colorLevel);

				if (solid[Tx][Ty])
					visualData[Tx][Ty] = new Color(200, 200, 0);
			}
		}

		return visualData;
	}

	/*
	 * @param a Base
	 * 
	 * @param b -exponent
	 */
	private double sigmoid(double a, double b) {
		return 1 / (1 + (Math.pow(a, -b)));
	}

}
