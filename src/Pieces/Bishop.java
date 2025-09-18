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
	
public boolean moveable(int targetColumn, int targetRow) {
		
		if(inBounds(targetColumn, targetRow) && isSameTile(targetColumn, targetRow) == false) {
			if(Math.abs(targetColumn - preCol) == Math.abs(targetRow-preRow)) {
				if(isValidTile(targetColumn, targetRow)&& isBlockDiagonal(targetColumn,targetRow)==false) {
					return true;
				}
			}
		}
		return false;
	}
}
