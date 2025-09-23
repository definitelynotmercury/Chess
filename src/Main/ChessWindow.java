package Main;

import javax.swing.JFrame;

public class ChessWindow {
    public static void main(String[] args) {
        JFrame window = new JFrame("Java Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        ChessPanel chessPanel = new ChessPanel();
        window.add(chessPanel);
        
        window.pack(); // This must come before setLocationRelativeTo
        window.setLocationRelativeTo(null); // This centers the window
        window.setVisible(true);
        
        // NO MORE THREADING! The game is now fully event-driven
        // Just show the window and let mouse events handle everything
    }
}