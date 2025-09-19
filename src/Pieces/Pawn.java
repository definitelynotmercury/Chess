package Pieces;

import Main.ChessPanel;
import Main.Type;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
	    super(color, col, row);
	    
	    type = Type.PAWN;
	    
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
		//one move
		if(targetColumn == preCol && targetRow == preRow + moveDirection && targetPiece == null) {
			
			return true;
		}
		
		//two move
		if(targetColumn == preCol && targetRow == preRow + moveDirection*2 && targetPiece == null &&
				isMoved == false && isBlockedStraight(targetColumn,targetRow) == false) {
			
			return true;
		}
		
		//attack
		if(Math.abs(targetColumn - preCol) == 1 && targetRow == preRow + moveDirection 
				&& targetPiece != null && targetPiece.color != color) {
			return true;
		}
		
		//en passant
		if(Math.abs(targetColumn - preCol) == 1 && targetRow == preRow + moveDirection ) {
			synchronized(ChessPanel.simPiece) {
				for(Piece piece: ChessPanel.simPiece) {
					if(piece.col == targetColumn && piece.row == preRow && piece.twoTileMove == true) {
						targetPiece =piece;
						return true;
					}
				}
			}
		}
		
		return false;
	}
}