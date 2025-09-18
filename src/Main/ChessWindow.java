package Main;

import javax.swing.JFrame;

public class ChessWindow {
	
	public static void main(String[]args) {
		JFrame frame = new JFrame("JAVA CHESS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		ChessPanel panel = new ChessPanel();
		frame.add(panel);
		frame.pack();
		
		panel.launchChess();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
