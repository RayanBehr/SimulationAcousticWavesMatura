package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Chart extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double[] pressureValues;
	private double[] changeValues;
	
	private int time_steps;

	private int currentIteration = 0;
	private int shownTimeIndex = 0;

	private String id;

	public Chart(int time_steps) {
		this.time_steps = time_steps;
		pressureValues = new double[time_steps];
		changeValues = new double[time_steps];
	}

	public void setPressureValues(double[] pressureValues) {
		this.pressureValues = pressureValues;
	}

	public void setChangeValues(double[] changeValues) {
		this.changeValues = changeValues;
	}

	public void setCurrentIteration(int currentIteration) {
		this.currentIteration = currentIteration;
	}

	public void setShownTimeIndex(int shownTimeIndex) {
		this.shownTimeIndex = shownTimeIndex;
	}

	public void setId(String id) {
		this.id = id;
	}

	private void showData(Graphics g) {

		g.setColor(Color.GREEN);
		g.drawLine(currentIteration * super.getWidth() / pressureValues.length, 0,
				currentIteration * super.getWidth() / pressureValues.length, super.getHeight());
		g.setColor(Color.YELLOW);
		g.drawLine(shownTimeIndex * super.getWidth() / pressureValues.length, 0,
				shownTimeIndex * super.getWidth() / pressureValues.length, super.getHeight());

		double factor = 0.66;

		/*
		 * int xPosA = (int) (t / (double) data.length * super.getWidth()); int yPosA =
		 * -(int) (data[t] / (double) maximum * super.getHeight() * 0.5 * factor); int
		 * xPosB = (int) ((t + 1) / (double) data.length * super.getWidth()); int yPosB
		 * = -(int) (data[t + 1] / (double) maximum * super.getHeight() * 0.5 * factor);
		 * g.drawLine(xPosA, yPosA + super.getHeight() / 2, xPosB, yPosB +
		 * super.getHeight() / 2);
		 */

		double maximum_pressure = getAbsMaximumPressure();
		g.setColor(Color.CYAN);
		for (int t = 0; t < pressureValues.length - 1; t++) {
			int xPosA = (int) (t / (double) pressureValues.length * super.getWidth());
			int yPosA = -(int) (pressureValues[t] / (double) maximum_pressure * super.getHeight() * 0.5 * factor);
			int xPosB = (int) ((t + 1) / (double) pressureValues.length * super.getWidth());
			int yPosB = -(int) (pressureValues[t + 1] / (double) maximum_pressure * super.getHeight() * 0.5 * factor);
			g.drawLine(xPosA, yPosA + super.getHeight() / 2, xPosB, yPosB + super.getHeight() / 2);
		}
		g.drawString(String.valueOf(maximum_pressure), 10, super.getHeight() - 3);

		double maximum_change = getAbsMaximumChange();
		g.setColor(Color.ORANGE);
		for (int t = 0; t < changeValues.length - 1; t++) {
			int xPosA = (int) (t / (double) changeValues.length * super.getWidth());
			int yPosA = -(int) (changeValues[t] / (double) maximum_change * super.getHeight() * 0.5 * factor);
			int xPosB = (int) ((t + 1) / (double) changeValues.length * super.getWidth());
			int yPosB = -(int) (changeValues[t + 1] / (double) maximum_change * super.getHeight() * 0.5 * factor);
			g.drawLine(xPosA, yPosA + super.getHeight() / 2, xPosB, yPosB + super.getHeight() / 2);
		}
		g.drawString(String.valueOf(maximum_change), 200, super.getHeight() - 3);
		repaint();
	}


	public double getAbsMaximumPressure() {
		double maximum = 0;
		// for (double pressureValue : pressureValues) {
		for (int i = 0; i < currentIteration; i++) {
			double thisMaximum = Math.abs(pressureValues[i]);
			if (thisMaximum > maximum)
				maximum = thisMaximum;
		}
		return maximum;
	}

	public double getAbsMaximumChange() {
		double maximum = 0;
		// for (double pressureValue : pressureValues) {
		for (int i = 0; i < currentIteration; i++) {
			double thisMaximum = Math.abs(changeValues[i]);
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

	private void showId(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString(id, super.getWidth() - 20, 15);
	}

	private void showSceneTimestep(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(shownTimeIndex), super.getWidth() - 20, super.getHeight() - 8);
	}

	@Override
	protected void paintComponent(Graphics g) {
		fillBackground(g);
		showId(g);
		showSceneTimestep(g);
		showData(g);
	}

}
