package broken;

// Author: Lucas Galleguillos
// Filename: BrokenLinkFinder.java

public class BrokenLinkFinder {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ApplicationUserInterface gui = new ApplicationUserInterface();
				gui.start();
			}
		});
	}
}
