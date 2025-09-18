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
	
	public boolean moveable(int targetColumn, int targetRow) {
		
		int moveDirection;
		
		if(color == ChessPanel.WHITE_SIDE) {
			moveDirection = -1;
		}else {
			moveDirection = 1;
		}
		
		targetPiece = getTargetPiece(targetColumn,targetRow);
		
		if(targetColumn == preCol && targetRow == preRow + moveDirection && targetPiece == null) {
			
			return true;
		}
		
		if(targetColumn == preCol && targetRow == preRow + moveDirection*2 && targetPiece == null &&
				isMoved == false && isBlockedStraight(targetColumn,targetRow) == false) {
			
			return true;
		}
		
		if(Math.abs(targetColumn - preCol) == 1 && targetRow == preRow + moveDirection 
				&& targetPiece != null && targetPiece.color != color) {
			return true;
		}
		
		return false;
	}
}