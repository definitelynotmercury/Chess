package Pieces;
import Main.ChessPanel;
import Main.Type;

public class King extends Piece {
	
	public King(int color, int column, int row) {
		super(color, column, row);
		
		type = Type.KING;
		if(color == ChessPanel.WHITE_SIDE) {
			image = getImage("/piece/king");
		} else if(color == ChessPanel.BLACK_SIDE) {
			image = getImage("/piece/king1");
		}
	}
	
	public boolean moveable(int targetColumn, int targetRow) {
	    // Regular king movement (1 square in any direction)
	    if(Math.abs(targetRow - preRow) + Math.abs(targetColumn - preCol) == 1 ||
	       Math.abs(targetRow - preRow) * Math.abs(targetColumn - preCol) == 1) {
	        if(isValidTile(targetColumn, targetRow)) {
	            return true;
	        }
	    }
	    
	    // Castling logic
	    if(isMoved == false) {
	        
	        // King-side castling (short castle) - King moves 2 squares right
	        if(targetColumn == preCol + 2 && targetRow == preRow) {
	            // ⭐ FIXED: Check if squares 5 and 6 are empty
	            if(ChessPanel.getPieceAt(preCol + 1, preRow) == null && 
	               ChessPanel.getPieceAt(preCol + 2, preRow) == null) {
	                
	                // Look for unmoved rook at king-side (column 7)
	                for(Piece piece: ChessPanel.pieces) {
	                    if(piece.col == preCol + 3 && // Rook at column 7
	                       piece.row == preRow && 
	                       piece.isMoved == false && 
	                       piece instanceof Rook &&
	                       piece.color == this.color) {
	                        
	                        ChessPanel.castlePiece = piece;
	                        return true;
	                    }
	                }
	            }
	        }
	        
	        // Queen-side castling (long castle) - King moves 2 squares left  
	        if(targetColumn == preCol - 2 && targetRow == preRow) {
	            // ⭐ FIXED: Check if squares 1, 2, and 3 are empty
	            if(ChessPanel.getPieceAt(preCol - 1, preRow) == null && 
	               ChessPanel.getPieceAt(preCol - 2, preRow) == null &&
	               ChessPanel.getPieceAt(preCol - 3, preRow) == null) {
	                
	                // Look for unmoved rook at queen-side (column 0)
	                for(Piece piece: ChessPanel.pieces) {
	                    if(piece.col == preCol - 4 && // Rook at column 0
	                       piece.row == preRow && 
	                       piece.isMoved == false && 
	                       piece instanceof Rook &&
	                       piece.color == this.color) {
	                        
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