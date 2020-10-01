package controller;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MicrophoneInfo extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int[][] positions;

	public MicrophoneInfo(int numberOfMicrophones) {
		positions = new int[numberOfMicrophones][2];
	}

	public void setPosition(int microphone, int px, int py) {
		positions[microphone][0] = px;
		positions[microphone][1] = py;
	}

	public int[][] getPositions() {
		return this.positions;
	}

	// Refill the Background and overdraw
	private void fillBackground(Graphics g) {
		g.setColor(super.getBackground());
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
	}

	private void showInfo(Graphics g) {
		g.setColor(Color.WHITE);

		for (int m = 0; m < positions.length; m++) {
			g.drawString(String.valueOf(positions[m][0]), 10 + m * 34, 14);
			g.drawString(String.valueOf(positions[m][1]), 10 + m * 34, 34);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		fillBackground(g);
		showInfo(g);
	}
}
