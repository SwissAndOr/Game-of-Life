import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class EnvironmentPanel extends JComponent {
	
	public static final int NUMBER_OF_STATES = 8;
	
	private Environment environment;
	private int cellWidth, cellHeight;
	
	private boolean isAutomaticStepperRunning = false;
	private Thread automaticStepperThread = new Thread(new AutomaticStepper());
	private JButton automaticStepperToggle = new JButton("Play");
	private JLabel automaticStepperSpeedLabel = new JLabel("Speed (ms in between generations):");
	private JSpinner automaticStepperSpeedSpinner = new JSpinner(new SpinnerNumberModel(500, 0, 10000, 100));
	private JLabel generationLabel = new JLabel("Generation 0");
	private JButton optionsButton = new JButton("Options");
	
	private JDialog optionsDialog = new JDialog(Main.window, "Options");
	private JLabel[] tableLabels = new JLabel[2];
	private JButton[] stateColorButtons = new JButton[NUMBER_OF_STATES];
	private ActionListener stateColorChooser;
	private JCheckBox[] stateLivingStatusCheckboxes = new JCheckBox[NUMBER_OF_STATES];
	private JCheckBox[] liveConditionCheckboxes = new JCheckBox[9];
	private JCheckBox[] birthConditionCheckboxes = new JCheckBox[9];
	
	EnvironmentPanel(int width, int height, int cellWidth, int cellHeight) {
		this.setLayout(new FlowLayout());
		environment = new Environment(width, height);
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

	    for (int x = 0; x < environment.getWidth(); x++) {
		    for (int y = 0; y < environment.getHeight(); y++) {
//		    	environment.setCellState(x, y, Math.random() >= 0.9 ? 1 : 0);
		    }
	    }
	    
	    this.addMouseListener(new MouseAdapter() {
	    	
	    	public void mouseReleased(MouseEvent e) {
	    		environment.invertCell(e.getX() / cellWidth, (e.getY() - 50) / cellHeight);
	    		repaint();
	    	}
		});
	    
	    automaticStepperToggle.setSize(75, 50);
	    automaticStepperToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isAutomaticStepperRunning) {
					isAutomaticStepperRunning = false;
					automaticStepperThread.interrupt();
				} else {
					isAutomaticStepperRunning = true;
					if (!automaticStepperThread.isAlive()) {
						automaticStepperThread.start();
					} else {
						automaticStepperThread.interrupt();
					}
				}
				
				automaticStepperToggle.setText(isAutomaticStepperRunning ? "Pause" : "Play");
			}
		});
	    this.add(automaticStepperToggle);
	    this.add(automaticStepperSpeedLabel);
	    automaticStepperSpeedSpinner.setPreferredSize(new Dimension(60, automaticStepperSpeedSpinner.getPreferredSize().height));
	    this.add(automaticStepperSpeedSpinner);
	    this.add(generationLabel);
	    optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsDialog.setVisible(!optionsDialog.isVisible());
			}
		});
	    this.add(optionsButton);
	    
	    optionsDialog.setLayout(new GridLayout(5, 9));
	    optionsDialog.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	boolean[] stateLivingStatuses = new boolean[stateLivingStatusCheckboxes.length];
	        	for (int i = 0; i < stateLivingStatusCheckboxes.length; i++) {
	        		stateLivingStatuses[i] = stateLivingStatusCheckboxes[i].isSelected();
	        	}
	        	environment.stateLivingStatuses = stateLivingStatuses;
	        	
	        	boolean[] liveConditions = new boolean[liveConditionCheckboxes.length];
	        	for (int i = 0; i < liveConditionCheckboxes.length; i++) {
	        		liveConditions[i] = liveConditionCheckboxes[i].isSelected();
	        	}
	        	environment.liveConditions = liveConditions;
	        	
	        	boolean[] birthConditions = new boolean[birthConditionCheckboxes.length];
	        	for (int i = 0; i < birthConditionCheckboxes.length; i++) {
	        		birthConditions[i] = birthConditionCheckboxes[i].isSelected();
	        	}
	        	environment.birthConditions = birthConditions;
	        }
		});
	    
	    stateColorChooser = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i;
				for (i = 0; i < stateColorButtons.length; i++) {
					if (e.getSource().equals(stateColorButtons[i])) {
						break;
					}
				}

				Color selectedColor = JColorChooser.showDialog(null, "Color Chooser", stateColorButtons[i].getBackground());
				if (selectedColor != null) {
					stateColorButtons[i].setBackground(new Color(selectedColor.getRGB() & 16777215));
				}
			}
		};
	    
		tableLabels[0] = new JLabel("State Color:");
		optionsDialog.add(tableLabels[0]);
	    for (int i = 0; i < stateColorButtons.length; i++) {
	    	stateColorButtons[i] = new JButton();
	    	stateColorButtons[i].setBackground(new Color(Color.WHITE.getRGB() & 16777215));
	    	stateColorButtons[i].addActionListener(stateColorChooser);
	    	optionsDialog.add(stateColorButtons[i]);
	    }
    	stateColorButtons[1].setBackground(new Color(Color.BLACK.getRGB() & 16777215));

		tableLabels[1] = new JLabel("State Alive:");
		optionsDialog.add(tableLabels[1]);
	    for (int i = 0; i < stateLivingStatusCheckboxes.length; i++) {
	    	stateLivingStatusCheckboxes[i] = new JCheckBox(Integer.toString(i));
	    	optionsDialog.add(stateLivingStatusCheckboxes[i]);
	    }
	    stateLivingStatusCheckboxes[1].setSelected(true);
	    
	    for (int i = 0; i < 9; i++) {
	    	optionsDialog.add(new JSeparator());
	    }
	    
//		tableLabels[2] = new JLabel("Neighbors to live:");
//		optionsDialog.add(tableLabels[2]);
	    for (int i = 0; i < liveConditionCheckboxes.length; i++) {
	    	liveConditionCheckboxes[i] = new JCheckBox(Integer.toString(i));
	    	optionsDialog.add(liveConditionCheckboxes[i]);
	    }
	    liveConditionCheckboxes[2].setSelected(true);
	    liveConditionCheckboxes[3].setSelected(true);
	    
//		tableLabels[3] = new JLabel("Neighbors for birth:");
//		optionsDialog.add(tableLabels[3]);
	    for (int i = 0; i < birthConditionCheckboxes.length; i++) {
	    	birthConditionCheckboxes[i] = new JCheckBox(Integer.toString(i));
	    	optionsDialog.add(birthConditionCheckboxes[i]);
	    }
	    birthConditionCheckboxes[3].setSelected(true);
	    
	    optionsDialog.pack();
	    
	    this.setPreferredSize(new Dimension(environment.getWidth() * cellWidth, environment.getHeight() * cellHeight + 50));
	}
	
	public void paintComponent (Graphics g) {
	    for (int x = 0; x < environment.getWidth(); x++) {
		    for (int y = 0; y < environment.getHeight(); y++) {
		    	g.setColor(stateColorButtons[environment.getCellState(x, y)].getBackground());
			   	g.fillRect(x * cellWidth, y * cellHeight + 50, cellWidth, cellHeight);
		    }
	    }
	    
		g.setColor(Color.LIGHT_GRAY);
	    for (int x = 0; x <= environment.getWidth(); x++) {
		    g.fillRect(x * cellWidth - 1, 50, 2, environment.getHeight() * cellHeight);	
	    }
	    for (int y = 0; y <= environment.getHeight(); y++) {
	    	g.fillRect(0, y * cellHeight - 1 + 50, environment.getWidth() * cellWidth, 2);	
	    }
	    
	    generationLabel.setText("Generation " + environment.getGeneration());
	}
	
	class AutomaticStepper implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (isAutomaticStepperRunning) {
					try {
						Thread.sleep(Long.valueOf(automaticStepperSpeedSpinner.getValue().toString()));
					} catch (InterruptedException e) {
						continue;
					}
					environment.nextStep();
					repaint();
				} else {
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
		}
		
	}
	
}
