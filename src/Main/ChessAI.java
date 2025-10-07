package Main;

import java.util.ArrayList;
import java.util.Random;

import Pieces.Piece;

public class ChessAI {
	
	private int aiColor;
	private Random randomizer;
	
	
	public ChessAI(int color) {
        this.aiColor = color;
        this.randomizer = new Random();
    }
	
	public ArrayList<Move> generateAllValidMoves(ArrayList<Piece> pieces, ChessPanel panel){
	    ArrayList<Move> legalMoves = new ArrayList<>();
	    
	    ArrayList<Piece> piecesCopy = new ArrayList<>(pieces); // <-- create a copy
	    
	    for(Piece piece: piecesCopy) {
	        if(piece.color == aiColor) {
	            for(int col = 0; col < 8; col++) {
	                for(int row = 0; row < 8; row++) {
	                    if(piece.moveable(col, row)) {
	                        if(!panel.moveExposesKing(piece, col, row)) {
	                        	Move move = new Move(piece, col, row);
	                        	
	                        	move.capturedPiece = panel.getPieceAt(col, row);
	                        	
	                        	
	                        	
	                            legalMoves.add(move);
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    return legalMoves;
	}
	
	public Move selectBestMove(ArrayList<Piece> pieces,ChessPanel panel) {
		ArrayList<Move> validMoves =  generateAllValidMoves(pieces,panel);
	    ArrayList<scoredMove> scoredMoves = new ArrayList<>();
	    
	    // Score each move
	    for (Move move : validMoves) {
	        int score = scoreMove(move,panel);
	        scoredMoves.add(new scoredMove(move, score));
	    }
	    
	    // Find the move with highest score
	    scoredMove best = scoredMoves.get(0);
	    for (scoredMove sm : scoredMoves) {
	        if (sm.score > best.score) {
	            best = sm;
	        }
	    }
	    
	    return best.move;
	}
	

	private int scoreMove(Move move, ChessPanel panel) {
		 int score = 0;
		    
		    // 1. CAPTURE VALUE
		    score += scoreForCapturedPiece(move);
		    
		    // 2. THREAT ASSESSMENT (Safety check)
		    score += scoreForSafety(move,panel);
		    
		    // 3. CENTER CONTROL
		    score += scoreCenterControl(move);
		    
		    // 4. CHECK BONUS
		    score += scoreCheckBonus(move,panel);
		    
		    // 5. Tiny random factor (prevents robotic play)
		    score += Math.random() * 2 - 1;  // Random between -1 and +1
		    
		    return score;
	}
	
	
	private int scoreForCapturedPiece(Move move) {
		   if (move.capturedPiece != null) {
		        // Return the value of what we're capturing, multiplied by 10
		        // (Multiply by 10 to make captures more important than other factors)
		        return move.capturedPiece.type.getValue() * 10;
		    }
		return 0;
	}
	
	private int scoreForSafety(Move move, ChessPanel panel) {
		
		ArrayList<Piece>dupedPieces = panel.pieces;
		int destCol = move.toCol;
	    int destRow = move.toRow;
	    
	    int originalColumn = move.piece.col;
		int originalRow = move.piece.row;
		
		// Temporarily "move" the piece
	    move.piece.col = move.toCol;
	    move.piece.row = move.toRow;
	    
	    
	    Piece capturedPiece = null;
	    if(move.capturedPiece!=null) {
	    	capturedPiece = move.capturedPiece;
	    	ChessPanel.pieces.remove(capturedPiece);
	    }
	    
	    boolean canBeCaptured = false;
	    
	    for(Piece piece:dupedPieces) {
	    	if (piece.moveable(destCol, destRow)&&piece.color!=aiColor) {
		        // Danger! We might lose this piece
	    		canBeCaptured = true;
		        
		    }
	    }
	    
	    move.piece.col = originalColumn;
	    move.piece.row = originalRow;
	    if (capturedPiece != null) {
	    	ChessPanel.pieces.add(capturedPiece);
	    }
	    
	    if (canBeCaptured) {
	        // We gain: what we captured
	        int gain = (move.capturedPiece != null) ? move.capturedPiece.type.getValue() : 0;
	        
	        // We lose: our piece
	        int loss = move.piece.type.getValue();
	        
	        // Net result
	        int tradeValue = gain - loss;
	        
	        // If it's a bad trade, penalize heavily
	        if (tradeValue < 0) {
	            return tradeValue * 10;  // Negative score for bad trades
	        }
	        
	        // If it's an equal or good trade, small penalty (moving to danger)
	        return -1;  // Slight hesitation, but not forbidden
	    }
	    
	    
	    return 0;
	}
	
	private int scoreCenterControl(Move move) {
	    int destCol = move.toCol;
	    int destRow = move.toRow;
	    
	    // The 4 center squares are: d4, d5, e4, e5
	    // In coordinates (assuming 0-7): (3,3), (3,4), (4,3), (4,4)
	    
	    if ((destCol == 3 || destCol == 4) && (destRow == 3 || destRow == 4)) {
	        return 5;  // Bonus for center squares
	    }
	    
	    // Extended center (c3-c6, f3-f6, etc.)
	    if (destCol >= 2 && destCol <= 5 && destRow >= 2 && destRow <= 5) {
	        return 2;  // Smaller bonus for near-center
	    }
	    
	    return 0;
	}
	
	private int scoreCheckBonus(Move move, ChessPanel panel) {
		int originalColumn = move.piece.col;
		int originalRow = move.piece.row;
		
		// Temporarily "move" the piece
	    move.piece.col = move.toCol;
	    move.piece.row = move.toRow;
	    
	    Piece capturedPiece = null;
	    if(move.capturedPiece!=null) {
	    	capturedPiece = move.capturedPiece;
	    	ChessPanel.pieces.remove(capturedPiece);
	    }
	    // Is the enemy king now in check?
	    Piece enemyKing = panel.enemyMyKing(move.piece.color);
	    boolean givesCheck = panel.isCheck(enemyKing);
	    
	    
	    move.piece.col = originalColumn;
	    move.piece.row = originalRow;
	    if (capturedPiece != null) {
	    	ChessPanel.pieces.add(capturedPiece);
	    }
	    
	    if (givesCheck) {
	        return 50;
	    }
	    
	    return 0;
	}
	//for random picking of move
	public Move selectValidMove(ArrayList<Piece> pieces, ChessPanel panel) {
		ArrayList<Move> validMoves =  generateAllValidMoves(pieces,panel);
		
		if(validMoves.isEmpty()) {
            return null; // No legal moves (checkmate/stalemate)
        }
		
		return  validMoves.get(randomizer.nextInt(validMoves.size()));
		
		
	}
}
