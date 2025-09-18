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

        // Regular king movement (1 square in any direction)
        if(inBounds(targetColumn,targetRow)) {
            if(Math.abs(targetRow-preRow) + Math.abs(targetColumn - preCol) == 1 ||
               Math.abs(targetRow-preRow) * Math.abs(targetColumn - preCol) == 1) {

                if(isValidTile(targetColumn,targetRow)) {
                    return true;
                }
            }
        }

        // Castling logic
        if(isMoved == false) {
            
            // King-side castling (short castle) - King moves 2 squares right
            if(targetColumn == preCol + 2 && targetRow == preRow) {
                // Check if path is clear and rook exists
                if(isBlockedStraight(targetColumn, targetRow) == false) {
                    // Look for rook at king-side (column 7)
                    for(Piece piece: ChessPanel.simPiece) {
                        if(piece.col == preCol + 3 && // Rook should be at column 7 (king at 4 + 3 = 7)
                           piece.row == preRow && 
                           piece.isMoved == false && 
                           piece instanceof Rook && // Make sure it's actually a rook
                           piece.color == this.color) { // Same color as king
                            
                            ChessPanel.castlePiece = piece;
                            return true;
                        }
                    }
                }
            }

            // Queen-side castling (long castle) - King moves 2 squares left  
            if(targetColumn == preCol - 2 && targetRow == preRow) {
                // Check if path is clear
                if(isBlockedStraight(targetColumn, targetRow) == false) {
                    // Look for rook at queen-side (column 0)
                    for(Piece piece: ChessPanel.simPiece) {
                        if(piece.col == preCol - 4 && // Rook should be at column 0 (king at 4 - 4 = 0)
                           piece.row == preRow && 
                           piece.isMoved == false && 
                           piece instanceof Rook && // Make sure it's actually a rook
                           piece.color == this.color) { // Same color as king
                            
                            ChessPanel.castlePiece = piece;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
