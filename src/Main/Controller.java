package Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller extends MouseAdapter {
	
	public int posX, posY;
	public boolean isPressed;
	
	public void mousePressed(MouseEvent e) {
		isPressed = true;
		
		}
	
	public void mouseReleased(MouseEvent e) {
		isPressed = false;
		
		}
	public void mouseDragged(MouseEvent e) {
		posX = e.getX();
		posY = e.getY();
		}
	public void mouseMoved(MouseEvent e) {
		posX = e.getX();
		posY = e.getY();
		}

}
