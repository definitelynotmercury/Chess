package Pieces;

import Main.ChessPanel;

public class Bishop extends Piece {
	
	public Bishop(int color, int column, int row) {
		super(color,column,row);
		
		if(color == ChessPanel.WHITE_SIDE) {
			image = getImage("/piece/bishop");
		}else if (color == ChessPanel.BLACK_SIDE) {
			image = getImage("/piece/bishop1");
		}
	}
}
