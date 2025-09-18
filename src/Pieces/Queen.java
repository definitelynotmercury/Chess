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
	
	public boolean moveable(int targetColumn, int targetRow) {
		
		if(inBounds(targetColumn, targetRow) && isSameTile(targetColumn, targetRow) == false) {
			
			if(targetColumn == preCol || targetRow == preRow) {
				if(isValidTile(targetColumn, targetRow)&& isBlockedStraight(targetColumn,targetRow)==false) {
					return true;
				}
			}
			
			if(Math.abs(targetColumn - preCol) == Math.abs(targetRow-preRow)) {
				if(isValidTile(targetColumn, targetRow)&& isBlockDiagonal(targetColumn,targetRow)==false) {
					return true;
				}
			}
		}
		
		
		return false;
	}
}
