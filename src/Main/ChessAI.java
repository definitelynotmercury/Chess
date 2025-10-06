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
	                            legalMoves.add(new Move(piece, col, row));
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    return legalMoves;
	}


	
	
	public Move selectValidMove(ArrayList<Piece> pieces, ChessPanel panel) {
		ArrayList<Move> validMoves =  generateAllValidMoves(pieces,panel);
		
		if(validMoves.isEmpty()) {
            return null; // No legal moves (checkmate/stalemate)
        }
		
		return  validMoves.get(randomizer.nextInt(validMoves.size()));
		
		
	}
}
