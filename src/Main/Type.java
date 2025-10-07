package Main;

public enum Type {
    PAWN(1),
    KNIGHT(3),
    BISHOP(3),
    ROOK(5),
    QUEEN(9),
    KING(0);  // or Integer.MAX_VALUE, but 0 works since king can't be captured
    
    private final int value;
    
    Type(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}