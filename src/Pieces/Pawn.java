package Pieces;

import Main.ChessPanel;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
	    super(color, col, row);

	    if (color == ChessPanel.WHITE_SIDE) {
	    	image = getImage("/piece/pawn");   // instead of res/piece/pawn
	    } else if(color == ChessPanel.BLACK_SIDE) {
	        image = getImage("/piece/pawn1"); // Will become "src/res/piece/pawn1.png"
	    }
	}
}