package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Scene extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int matrix_size;

	private Color[][] visualData;

	private int[][] positions = new int[0][0];

	Color colorFPS = Color.ORANGE;

	int yArrayMouse;
	int xArrayMouse;

	private final int tile_size = 2;

	// FPS is a bit primitive,
	// MaxFPS can be set as high as I want, but won't be infinite
	static double drawFPS = 0, MaxFPS = 240, SleepTime = 1000.0 / MaxFPS, LastRefresh = 0,
			StartTime = System.currentTimeMillis(), LastFPSCheck = 0, Checks = 0;

	/**
	 * Create the panel.
	 */
	public Scene(int matrix_size) {
		this.matrix_size = matrix_size;
		initVariables();
	}

	private void initVariables() {
		visualData = new Color[matrix_size][matrix_size];
		yArrayMouse = 0;
		xArrayMouse = 0;
	}

	public Point getMousIndices() {
		Point p = new Point();
		if (isInBounds()) {
			p.y = yArrayMouse;
			p.x = xArrayMouse;
		} else {
			p.y = 0;
			p.x = 0;
		}
		return p;
	}

	private boolean isInBounds() {
		boolean myBoolean = false;
		if ((yArrayMouse >= 0) && (yArrayMouse < matrix_size)) {
			if ((xArrayMouse >= 0) && (xArrayMouse < matrix_size)) {
				myBoolean = true;
			}
		}
		return myBoolean;
	}

	public void setVisualData(Color[][] field) {
		this.visualData = field;
	}

	public void setPositions(int[][] positions) {
		this.positions = positions;
	}

	// Refill the Background and overdraw
	private void fillBackground(Graphics g) {
		g.setColor(super.getBackground());
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
	}

	// Draws the FPS
	private void drawCurrentFPS(Graphics g) {
		g.setColor(colorFPS);
		g.drawString("FPS: " + (int) drawFPS, 790, 11);
	}

	private void drawPositions(Graphics g) {
		g.setColor(Color.CYAN);
		for (int m = 0; m < positions.length; m++) {
			int xPos = positions[m][0] * tile_size + 20;
			int yPos = positions[m][1] * tile_size + 20;
			g.drawRect(xPos, yPos, tile_size, tile_size);
			g.drawString(String.valueOf(m), xPos - 6, yPos - 6);
		}
		g.setColor(Color.PINK);
		g.drawRect(getMousIndices().x * tile_size + 20, 
				   getMousIndices().y * tile_size + 20, 
				   tile_size, tile_size);

	}

	@Override
	protected void paintComponent(Graphics g) {
		fillBackground(g);
		drawData(g);
		drawPositions(g);
		showMouseInfo(g);
		drawCurrentFPS(g);
		SleepAndRefresh();
	}

	void drawData(Graphics g) {
		for (int x = 0; x < matrix_size; x++)
			for (int y = 0; y < matrix_size; y++) {
				int xPos = x * tile_size + 20;
				int yPos = y * tile_size + 20;

				g.setColor(visualData[x][y]);
				g.fillRect(xPos, yPos, tile_size, tile_size);

				// Draw optional Grid
				// g.setColor(Color.GRAY);
				// g.drawRect(xPos, yPos, tile_size, tile_size);

			}
	}

	private void showMouseInfo(Graphics g) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, getRootPane());
		// Then use 'p', which was modified by method call above

		xArrayMouse = (p.x - 20 - 10) / tile_size;
		yArrayMouse = (p.y - 20 - 26) / tile_size;

		boolean inField = xArrayMouse < matrix_size && yArrayMouse < matrix_size && xArrayMouse >= 0
				&& yArrayMouse >= 0;
		Color info = new Color(0, 0, 0);
		if (xArrayMouse >= 0 && yArrayMouse >= 0 && xArrayMouse < matrix_size && yArrayMouse < matrix_size) {
			info = visualData[xArrayMouse][yArrayMouse];
		}

		g.setColor(Color.WHITE);
		g.drawString("Mouse Position: " + xArrayMouse + ", " + yArrayMouse, 100, 835);
		g.drawString("Mouse in Field: " + inField + "   " + "Info at Mouse: " + info, 250, 835);
	}

	// FPS
	void SleepAndRefresh() {
		long timeSLU = (long) (System.currentTimeMillis() - LastRefresh);

		Checks++;
		if (Checks >= 15) {
			drawFPS = Checks / ((System.currentTimeMillis() - LastFPSCheck) / 1000.0);
			LastFPSCheck = System.currentTimeMillis();
			Checks = 0;
		}

		if (timeSLU < 1000.0 / MaxFPS) {
			try {
				Thread.sleep((long) (1000.0 / MaxFPS - timeSLU));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		LastRefresh = System.currentTimeMillis();
		repaint();
	}

}
