package Pieces;

import Main.ChessPanel;
import Main.Type;

public class Knight extends Piece {

	public Knight(int color, int column, int row) {
		super(color,column,row);
		type = Type.KNIGHT;
		if(color == ChessPanel.WHITE_SIDE) {
			image = getImage("/piece/knight");
		}else if(color == ChessPanel.BLACK_SIDE) {
			image = getImage("/piece/knight1");
		}
	}
	
	public boolean moveable(int targetColumn, int targetRow) {

			if(Math.abs(targetColumn - preCol) * Math.abs(targetRow - preRow) ==2) {
				if(isValidTile(targetColumn,targetRow)) {
					return true;
				}
			}
		return false;
	}
}
