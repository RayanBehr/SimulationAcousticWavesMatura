package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class ViewWindow extends JFrame implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Scene scene;

	private ArrayList<Chart> charts = new ArrayList<Chart>();

	private boolean mouseIsPressed;

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public ViewWindow(int matrix_size, int time_steps, int number_of_microphones) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ViewWindow.class.getResource("/icons/icon.png")));
		setTitle("Wave Dynamics Simulation (ViewWindow)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, 1282, 920);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		scene = new Scene(matrix_size);
		scene.setBackground(Color.DARK_GRAY);
		scene.setBounds(10, 26, 840, 840);
		contentPane.add(scene);

		int chartPositionY = 26;
		int id = 0;
		for (int m = 0; m < number_of_microphones; m++) {
			charts.add(new Chart(time_steps));
			charts.get(m).setBackground(Color.DARK_GRAY);
			charts.get(m).setBounds(860, chartPositionY, 400, 75);
			charts.get(m).setId(String.valueOf(id));
			contentPane.add(charts.get(m));
			chartPositionY += 85;
			id++;
		}

		JLabel lblTileView = new JLabel(
				"Tile View | 300mm x 300mm | " + 
				 matrix_size + " x " + matrix_size + 
				 " Tiles | 0.75mm/Tile");
		lblTileView.setHorizontalAlignment(SwingConstants.CENTER);
		lblTileView.setBounds(10, 8, 840, 14);
		contentPane.add(lblTileView);

		JLabel lblMicrophoneCharts = new JLabel("Microphone Charts");
		lblMicrophoneCharts.setHorizontalAlignment(SwingConstants.CENTER);
		lblMicrophoneCharts.setBounds(860, 8, 266, 14);
		contentPane.add(lblMicrophoneCharts);
		
		JLabel lblPressure = new JLabel("position");
		lblPressure.setBackground(Color.BLACK);
		lblPressure.setForeground(Color.CYAN);
		lblPressure.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPressure.setBounds(1146, 8, 48, 14);
		contentPane.add(lblPressure);
		
		JLabel lblChange = new JLabel("Change");
		lblChange.setBackground(Color.BLACK);
		lblChange.setForeground(Color.ORANGE);
		lblChange.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblChange.setBounds(1212, 8, 48, 14);
		contentPane.add(lblChange);

		addMouseListener(this);
	}

	public void setScene(Color[][] field) {
		scene.setVisualData(field);
	}
	

	public void setCharts(double[][] samplesPressure, double[][] samplesChange, 
						  int currentIteration, int shownTimeIndex) {
		for (int m = 0; m < samplesPressure.length; m++) {

			charts.get(m).setCurrentIteration(currentIteration);
			charts.get(m).setShownTimeIndex(shownTimeIndex);

		}

		for (int m = 0; m < samplesPressure.length; m++) {
			double[] pressureValues = new double[samplesPressure[0].length];
			for (int t = 0; t < samplesPressure[0].length - 1; t++) {
				pressureValues[t] = samplesPressure[m][t];
			}
			charts.get(m).setPressureValues(pressureValues);

		}

		for (int m = 0; m < samplesPressure.length; m++) {
			double[] changeValues = new double[samplesChange[0].length];
			for (int t = 0; t < samplesChange[0].length - 1; t++) {
				changeValues[t] = samplesChange[m][t];
			}
			charts.get(m).setChangeValues(changeValues);

		}

	}

	public void setChartPressures() {

	}

	public void setChartChanges() {

	}

	public double getMaxPressure(int chartID) {
		return charts.get(chartID).getAbsMaximumPressure();
	}

	public double getMaxChange(int chartID) {
		return charts.get(chartID).getAbsMaximumChange();
	}

	public Point getMouseOnArray() {
		return scene.getMousIndices();
	}

	public boolean mouseIsPressed() {
		return this.mouseIsPressed;
	}

	public void setPositionIndicies(int[][] positions) {
		scene.setPositions(positions);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseIsPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseIsPressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
