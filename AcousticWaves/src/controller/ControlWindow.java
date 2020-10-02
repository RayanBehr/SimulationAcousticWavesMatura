package controller;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.Simulation;
import view.Chart;
import view.ViewWindow;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ControlWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int matrix_size = 400;
	private final int number_of_microphones = 10;
	private final int time_steps = 3000;
	// Because the picture is 300mm x 300mm
	private final double tile_size = 0.00075; // [m] (1.0mm)

	ViewWindow vw;
	Simulation simulation;

	private JPanel contentPane;
	private JTextField textFieldTimeInterval;

	private JTextField textFieldSolidPath;
	private JToggleButton tglbtnChangeMicrophonePosition;

	private JSpinner spinner;
	private MicrophoneInfo panelMicrophoneInfo;

	private JSlider sliderShownTimeIndex;
	private JProgressBar progressBar;
	private JLabel lblCurrentEmulatorTask;

	private JToggleButton tglbtnUpdateCharts;

	JToggleButton tglbtnLive;

	JLabel lblErrorText;

	JSlider sliderSigmoidBase;

	Chart progressionInfo;

	JButton btnSimulationToggle;

	JTextField textFieldFrequency;

	JToggleButton btnView;
	
	JToggleButton btnAuto;
	private JTextField textFieldAutoFrequqncyStep;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//System.out.println("Args: " + args);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlWindow frame = new ControlWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ControlWindow() {

		initializeViewAndModel();

		setIconImage(Toolkit.getDefaultToolkit().getImage(ControlWindow.class.getResource("/icons/icon.png")));
		setTitle("Wave Dynamics Simulation (ControlWindow)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1200, 100, 372, 561);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JSeparator separatorMicrophonePositions = new JSeparator();
		separatorMicrophonePositions.setBounds(10, 183, 340, 2);
		contentPane.add(separatorMicrophonePositions);

		JLabel lblParameters = new JLabel("Parameters");
		lblParameters.setHorizontalAlignment(SwingConstants.CENTER);
		lblParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblParameters.setBounds(127, 196, 122, 14);
		contentPane.add(lblParameters);

		textFieldTimeInterval = new JTextField();
		textFieldTimeInterval.setEditable(false);
		textFieldTimeInterval.setBackground(Color.GRAY);
		textFieldTimeInterval.setHorizontalAlignment(SwingConstants.TRAILING);
		textFieldTimeInterval.setToolTipText("Time Interval [ns]");
		textFieldTimeInterval.setText(String.valueOf(tile_size / 343d * Math.pow(10, 9) * 4d));
		textFieldTimeInterval.setColumns(10);
		textFieldTimeInterval.setBounds(10, 241, 113, 20);
		contentPane.add(textFieldTimeInterval);

		JSeparator separatorParameters = new JSeparator();
		separatorParameters.setBounds(10, 272, 340, 2);
		contentPane.add(separatorParameters);

		textFieldFrequency = new JTextField();
		textFieldFrequency.setBackground(Color.GRAY);
		textFieldFrequency.setToolTipText("Frequency [Hz]");
		textFieldFrequency.setText("3000");
		textFieldFrequency.setHorizontalAlignment(SwingConstants.TRAILING);
		textFieldFrequency.setColumns(10);
		textFieldFrequency.setBounds(261, 241, 89, 20);
		contentPane.add(textFieldFrequency);

		JLabel lblSolidMap = new JLabel("Solid Map");
		lblSolidMap.setHorizontalAlignment(SwingConstants.CENTER);
		lblSolidMap.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSolidMap.setBounds(10, 11, 340, 14);
		contentPane.add(lblSolidMap);

		textFieldSolidPath = new JTextField();
		textFieldSolidPath.setBackground(Color.GRAY);
		textFieldSolidPath.setToolTipText("Path to [*.jpg or *.png]");
		//Change this path if the repository was cloned to another user
		textFieldSolidPath.setText("C:\\Users\\rayan\\git\\SimulationAcousticWavesMatura\\AcousticWaves\\src\\maps\\0.jpg");
		//"C:\\StaticData\\Geometry\\Wellenleitung.jpg"
		//"C:\\StaticData\\Geometry\\SpeakerCabinet_x400_300mm.jpg"
		textFieldSolidPath.setBounds(10, 36, 239, 20);
		contentPane.add(textFieldSolidPath);
		textFieldSolidPath.setColumns(10);

		JLabel lblMicrophonePositions = new JLabel("Microphone Positions");
		lblMicrophonePositions.setHorizontalAlignment(SwingConstants.CENTER);
		lblMicrophonePositions.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMicrophonePositions.setToolTipText("There are 10 microphones to set.");
		lblMicrophonePositions.setBounds(10, 80, 340, 14);
		contentPane.add(lblMicrophonePositions);

		spinner = new JSpinner();
		spinner.setBackground(Color.GRAY);
		spinner.setModel(new SpinnerNumberModel(0, 0, 9, 1));
		spinner.setBounds(80, 101, 60, 20);
		contentPane.add(spinner);

		tglbtnChangeMicrophonePosition = new JToggleButton("Select Position");
		tglbtnChangeMicrophonePosition.setBackground(Color.GRAY);
		tglbtnChangeMicrophonePosition
				.setToolTipText("If selected lets you set a position of a microphone in the scene.");
		tglbtnChangeMicrophonePosition.setBounds(150, 98, 130, 23);
		contentPane.add(tglbtnChangeMicrophonePosition);

		btnSimulationToggle = new JButton("Stop or Restart");
		btnSimulationToggle.setBackground(Color.GRAY);
		btnSimulationToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulation.setTimeInterval(stringToDouble(textFieldTimeInterval.getText()) / Math.pow(10, 9)); // [ns]
																												// to
																												// [s]
				simulation.setActuationFrequency(stringToDouble(textFieldFrequency.getText()));
				simulation.startStopOrRestartEmulation();
			}
		});
		btnSimulationToggle.setBounds(218, 285, 130, 23);
		contentPane.add(btnSimulationToggle);

		JLabel lblControl = new JLabel("Control");
		lblControl.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblControl.setBounds(10, 289, 100, 14);
		contentPane.add(lblControl);

		JSeparator separatorControl = new JSeparator();
		separatorControl.setBounds(10, 345, 340, 2);
		contentPane.add(separatorControl);

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					simulation.setSolid(textFieldSolidPath.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnImport.setToolTipText("Import a " + matrix_size + "x"+  matrix_size + " *.jpg or *.png");
		btnImport.setBackground(Color.GRAY);
		btnImport.setBounds(261, 35, 89, 23);
		contentPane.add(btnImport);

		JSeparator separatorSolidMap = new JSeparator();
		separatorSolidMap.setBounds(10, 67, 340, 2);
		contentPane.add(separatorSolidMap);

		panelMicrophoneInfo = new MicrophoneInfo(number_of_microphones);
		panelMicrophoneInfo.setBackground(Color.DARK_GRAY);
		panelMicrophoneInfo.setBounds(10, 132, 338, 40);
		contentPane.add(panelMicrophoneInfo);

		JLabel lblSimulation = new JLabel("Simulation");
		lblSimulation.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSimulation.setBounds(10, 362, 100, 14);
		contentPane.add(lblSimulation);

		lblCurrentEmulatorTask = new JLabel("Simulation not initialized");
		lblCurrentEmulatorTask.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentEmulatorTask.setToolTipText("Current Emulator Task");
		lblCurrentEmulatorTask.setBounds(193, 362, 155, 14);
		contentPane.add(lblCurrentEmulatorTask);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 387, 340, 14);
		contentPane.add(progressBar);

		JSeparator separatorEmulatorInfo = new JSeparator();
		separatorEmulatorInfo.setBounds(10, 412, 340, 2);
		contentPane.add(separatorEmulatorInfo);

		lblErrorText = new JLabel("Error Text");
		lblErrorText.setHorizontalAlignment(SwingConstants.CENTER);
		lblErrorText.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblErrorText.setBounds(10, 495, 173, 14);
		contentPane.add(lblErrorText);

		JButton btnResetErrorMessage = new JButton("Reset Error Message");
		btnResetErrorMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblErrorText.setText("Error Text");
			}
		});
		btnResetErrorMessage.setBackground(Color.GRAY);
		btnResetErrorMessage.setBounds(193, 491, 157, 23);
		contentPane.add(btnResetErrorMessage);

		sliderShownTimeIndex = new JSlider();
		sliderShownTimeIndex.setBackground(Color.GRAY);
		sliderShownTimeIndex.setValue(0);
		sliderShownTimeIndex.setMaximum(time_steps - 1);
		sliderShownTimeIndex.setToolTipText("Shown Time Index");
		sliderShownTimeIndex.setBounds(10, 319, 340, 15);
		contentPane.add(sliderShownTimeIndex);

		JLabel lblVisualMode = new JLabel("Visual Mode");
		lblVisualMode.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblVisualMode.setBounds(10, 425, 82, 14);
		contentPane.add(lblVisualMode);

		JSeparator separatorVisualMode = new JSeparator();
		separatorVisualMode.setBounds(10, 478, 340, 2);
		contentPane.add(separatorVisualMode);

		tglbtnUpdateCharts = new JToggleButton("Update Charts");
		tglbtnUpdateCharts.setBackground(Color.GRAY);
		tglbtnUpdateCharts.setBounds(10, 444, 147, 23);
		contentPane.add(tglbtnUpdateCharts);
		
		tglbtnLive = new JToggleButton("Live");
		tglbtnLive.setBackground(Color.GRAY);
		tglbtnLive.setBounds(167, 444, 100, 23);
		contentPane.add(tglbtnLive);

		sliderSigmoidBase = new JSlider();
		sliderSigmoidBase.setBackground(Color.GRAY);
		sliderSigmoidBase.setToolTipText("Visualization Sigmoid Base from 0 to 1");
		sliderSigmoidBase.setBounds(120, 424, 230, 15);
		contentPane.add(sliderSigmoidBase);

		btnView = new JToggleButton("View");
		btnView.setBackground(Color.GRAY);
		btnView.setBounds(277, 444, 73, 23);
		contentPane.add(btnView);
		
		btnAuto = new JToggleButton("Auto");
		btnAuto.setBackground(Color.GRAY);
		btnAuto.setBounds(104, 358, 89, 23);
		contentPane.add(btnAuto);
		
		JToggleButton btnParallelism = new JToggleButton("Prallelism");
		btnParallelism.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				simulation.setParallelism(btnParallelism.isSelected());
			}
		});
		btnParallelism.setBackground(Color.GRAY);
		btnParallelism.setBounds(108, 285, 100, 23);
		contentPane.add(btnParallelism);
		
		JLabel lblDeltaTime = new JLabel("Delta Time [ns]");
		lblDeltaTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblDeltaTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeltaTime.setBounds(10, 224, 119, 14);
		contentPane.add(lblDeltaTime);
		
		textFieldAutoFrequqncyStep = new JTextField();
		textFieldAutoFrequqncyStep.setToolTipText("Time Interval [ns]");
		textFieldAutoFrequqncyStep.setText("5");
		textFieldAutoFrequqncyStep.setHorizontalAlignment(SwingConstants.TRAILING);
		textFieldAutoFrequqncyStep.setColumns(10);
		textFieldAutoFrequqncyStep.setBackground(Color.GRAY);
		textFieldAutoFrequqncyStep.setBounds(136, 241, 113, 20);
		contentPane.add(textFieldAutoFrequqncyStep);
		
		JLabel lblAutoFrequqncyStep = new JLabel("Auto Step [Hz]");
		lblAutoFrequqncyStep.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblAutoFrequqncyStep.setHorizontalAlignment(SwingConstants.CENTER);
		lblAutoFrequqncyStep.setBounds(139, 224, 110, 14);
		contentPane.add(lblAutoFrequqncyStep);
		
		JLabel lblFrequqncy = new JLabel("Frequqncy [Hz]");
		lblFrequqncy.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFrequqncy.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrequqncy.setBounds(261, 224, 89, 14);
		contentPane.add(lblFrequqncy);
		
		JLabel labelDeltaS = new JLabel("Tile Size: 0.75mm");
		labelDeltaS.setFont(new Font("Tahoma", Font.PLAIN, 11));
		labelDeltaS.setHorizontalAlignment(SwingConstants.CENTER);
		labelDeltaS.setBounds(20, 196, 97, 14);
		contentPane.add(labelDeltaS);
		
		JLabel lblX = new JLabel("400 x 400 Tiles");
		lblX.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblX.setHorizontalAlignment(SwingConstants.CENTER);
		lblX.setBounds(253, 196, 97, 14);
		contentPane.add(lblX);
		
		startControlLoop();

	}

	private void startControlLoop() {
		new Thread(() -> {
			while (this.isEnabled()) {
				// If the view toggle is selected and the mouse in the view is pressed
				if (tglbtnChangeMicrophonePosition.isSelected() && vw.mouseIsPressed()) {
					// Create an empty point object
					Point m = new Point();
					// Get the mouse position on the array and store it
					m = vw.getMouseOnArray();
					panelMicrophoneInfo.setPosition((int) spinner.getValue(), m.x, m.y);
					vw.setPositionIndicies(panelMicrophoneInfo.getPositions());
					// repaint the microphone info panel
					panelMicrophoneInfo.repaint();
				} else {
					// Show the standard view
					vw.setScene(simulation.getView(sliderShownTimeIndex.getValue(),
							(double) sliderSigmoidBase.getValue() / (double) sliderSigmoidBase.getMaximum(),
							btnView.isSelected()));
				}
				if (tglbtnUpdateCharts.isSelected()) {
					// Update Charts
					vw.setCharts(simulation.getPressureSamples(panelMicrophoneInfo.getPositions()),
							simulation.getChangeSamples(panelMicrophoneInfo.getPositions()),
							simulation.getIterationIndex(), sliderShownTimeIndex.getValue());
				}
				// Live mechanics
				if (tglbtnLive.isSelected()) {
					sliderShownTimeIndex.setValue(simulation.getIterationIndex());
				}
				// Show simulation Progress
				progressBar.setValue(simulation.getProgressPercent());
				// System.out.println(simulation.getProgressPercent());
				lblCurrentEmulatorTask.setText(simulation.getCurrentTask());

				// automation measurement mechanic
				if (simulation.getIterationIndex() == time_steps - 2 && btnAuto.isSelected()) {
					System.out.print(textFieldFrequency.getText() + "	");
					for(int id = 5; id < 6; id++) { //number_of_microphones
						System.out.print(vw.getMaxPressure(id) + "	" +
										 vw.getMaxChange(id) + "	");
					}
					System.out.println();
					
					btnSimulationToggle.doClick(); // Pragmatic action
					// btnEmulatorToggle.setSelected(true); //documentation gave me the idea
					int frequency = Integer.valueOf(textFieldFrequency.getText());
					frequency += (int) Integer.valueOf(textFieldAutoFrequqncyStep.getText());
					textFieldFrequency.setText(String.valueOf(frequency));
				}

			}
		}).start();
	}

	private void initializeViewAndModel() {
		simulation = new Simulation(matrix_size, time_steps);
		simulation.start();
		vw = new ViewWindow(matrix_size, time_steps, number_of_microphones);
		vw.setVisible(true);
	}

	private double stringToDouble(String s) {
		double d = 0;
		try {
			d = Double.parseDouble(s);
		} catch (Exception e) {
			lblErrorText.setText("YOu iDIoT..." + s);
		}
		return d;
	}
}
