package Main;

import Pieces.Piece;

public class Move {
	public Piece piece;
	public Piece capturedPiece;
    public int toCol;
    public int toRow;
    
    public Move(Piece piece, int toCol, int toRow) {
        this.piece = piece;
        this.toCol = toCol;
        this.toRow = toRow;
    }
}
