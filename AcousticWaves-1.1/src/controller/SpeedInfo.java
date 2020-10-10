package controller;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class SpeedInfo extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double[] values = new double[100];
	
	private int last_time_index = 0;
	
	private double speed = 0;
	
	public SpeedInfo() { }

	public void addValue(int this_time_index) {
		
		int amount_of_steps = this_time_index - last_time_index;
		last_time_index = this_time_index;
		
		speed += amount_of_steps / 10d;
		
		speed *= 1/1.1d;
		
		for(int i = 1; i < values.length; i++) 
		{
			values[i - 1] = values[i];
		}
		values[values.length - 1] = speed;
	}

	private void showData(Graphics g) {
		double factor = 0.66;

		double maximum_speed = getAbsMaximumSpeed();
		g.setColor(Color.CYAN);
		for (int t = 0; t < values.length - 1; t++) {
			int xPosA = (int) (t / (double) values.length * super.getWidth());
			int yPosA = -(int) (values[t] / (double) maximum_speed * super.getHeight() * 0.5 * factor);
			int xPosB = (int) ((t + 1) / (double) values.length * super.getWidth());
			int yPosB = -(int) (values[t + 1] / (double) maximum_speed * super.getHeight() * 0.5 * factor);
			g.drawLine(xPosA, yPosA + super.getHeight() / 2, xPosB, yPosB + super.getHeight() / 2);
		}
		g.drawString(String.valueOf(maximum_speed), 5, super.getHeight() - 3);
		
		repaint();
	}

	public double getAbsMaximumSpeed() {
		double maximum = 0;
		for (int i = 0; i < values.length; i++) {
			double thisMaximum = Math.abs(values[i]);
			if (thisMaximum > maximum)
				maximum = thisMaximum;
		}
		return maximum;
	}


	// Refill the Background and overdraw
	private void fillBackground(Graphics g) {
		g.setColor(super.getBackground());
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
	}

	@Override
	protected void paintComponent(Graphics g) {
		fillBackground(g);
		showData(g);
	}

}
