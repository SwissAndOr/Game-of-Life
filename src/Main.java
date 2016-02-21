import javax.swing.JFrame;

public class Main {

	public static JFrame window = new JFrame();
	public static EnvironmentPanel environmentPanel = new EnvironmentPanel(200, 100, 10, 10);

	public static void main(String[] args) {
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("SwissAndOr's Game of Life");

		window.add(environmentPanel);
		window.pack();

		window.setVisible(true);
	}

}
