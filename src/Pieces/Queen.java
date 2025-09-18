package Pieces;

import Main.ChessPanel;

public class Queen extends Piece {

	public Queen(int color, int column, int row) {
		super(color,column,row);
		
		if(color == ChessPanel.WHITE_SIDE) {
			image = getImage("/piece/queen");
		}else if(color == ChessPanel.BLACK_SIDE) {
			image = getImage("/piece/queen1");
		}
	}
}
