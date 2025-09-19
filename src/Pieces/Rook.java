package Pieces;

import Main.ChessPanel;
import Main.Type;

public class Rook extends Piece{
	
	public Rook(int color, int column, int row) {
		super(color,column,row);
		
		type = Type.ROOK;
		
		if(color == ChessPanel.WHITE_SIDE) {
			image = getImage("/piece/rook");
		}else if(color == ChessPanel.BLACK_SIDE) {
			image = getImage("/piece/rook1");
		}
	}
	
	public boolean moveable(int targetColumn, int targetRow) {
		
		if(inBounds(targetColumn, targetRow) && isSameTile(targetColumn, targetRow) == false) {
			if(targetColumn == preCol || targetRow == preRow) {
				if(isValidTile(targetColumn, targetRow)&& isBlockedStraight(targetColumn,targetRow)==false) {
					return true;
				}
			}
		}
		return false;
	}
}
