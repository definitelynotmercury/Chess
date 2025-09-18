package Pieces;

import Main.ChessPanel;

public class King extends Piece{
	
	public King(int color, int column, int row) {
		super(color,column,row);
		
		if(color == ChessPanel.WHITE_SIDE) {
			image = getImage("/piece/king");
		}else if(color == ChessPanel.BLACK_SIDE) {
			image = getImage("/piece/king1");
		}
	}
	
	
	public boolean moveable(int targetColumn, int targetRow){
		
		if(inBounds(targetColumn,targetRow)) {
			
			if(Math.abs(targetRow-preRow) + Math.abs(targetColumn - preCol) == 1 ||
				Math.abs(targetRow-preRow) * Math.abs(targetColumn - preCol) == 1) {
				
				if(isValidTile(targetColumn,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
}
