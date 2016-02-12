package gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class EnvironmentPanel extends JComponent {
	
	public static final int MIN_SPEED = 5000;
	public static final int MAX_SPEED = 10;
	
	private Environment environment;
	private Color[] cellColors = {Color.WHITE, Color.BLACK};
	private int cellWidth, cellHeight;
	
	private boolean isAutomaticStepperRunning = false;
	private Thread automaticStepperThread = new Thread(new AutomaticStepper());
	private JButton automaticStepperToggle = new JButton("Play");
	private JLabel automaticStepperSpeedLabel = new JLabel("Speed (ms in between generations):");
	private JSpinner automaticStepperSpeedSpinner = new JSpinner(new SpinnerNumberModel(500, 0, 10000, 100));
	private JLabel generationLabel = new JLabel("Generation 0");
	
	EnvironmentPanel(int width, int height, int cellWidth, int cellHeight) {
		this.setLayout(new FlowLayout());
		environment = new Environment(width, height);
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

	    for (int x = 0; x < environment.getWidth(); x++) {
		    for (int y = 0; y < environment.getHeight(); y++) {
		    	environment.setCellState(x, y, Math.random() >= 0.9 ? 1 : 0);
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
	    
	    this.setPreferredSize(new Dimension(environment.getWidth() * cellWidth, environment.getHeight() * cellHeight + 50));
	}
	
	public void paintComponent (Graphics g) {
	    for (int x = 0; x < environment.getWidth(); x++) {
		    for (int y = 0; y < environment.getHeight(); y++) {
		    	g.setColor(cellColors[environment.getCellState(x, y)]);
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
