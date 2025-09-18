package Main;

import java.awt.Color;
import java.awt.Graphics2D;

public class ChessBoard {
	
	//constants
	final int COL = 8;
	final int ROW = 8;
	public static final int TILE_SIZE = 100;
	public static final int HALF_TILE_SIZE = TILE_SIZE/2;
	boolean swap = true;
	
	//creates the chess board
	public void draw(Graphics2D g2) {
		for(int r = 0; r < COL ; r++) {
			
			for(int c = 0; c < ROW; c++) {
				
				if(swap == true) {
					g2.setColor(new Color(249,231,167,255));
					swap = false;
				} else {
					
					g2.setColor(new Color(20,93,74,255));
					swap = true;
				}
				g2.fillRect(r*TILE_SIZE, c*TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}

			if(swap == true) {
				swap = false;
			} else {
				swap = true;
			}
		}
	}
}
