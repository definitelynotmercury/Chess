package Main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

public class ChessPanel extends JPanel implements Runnable{
	
	public static final int PANELWIDTH = 1100;
	public static final int PANELHEIGHT = 800;
	final int framePerSecond = 120;
	Thread chessThread;
	ChessBoard chessBoard = new ChessBoard();
	Controller controller = new Controller();
	
	//setting colors
	public static final int WHITE_SIDE = 0;
	public static final int BLACK_SIDE = 1;
	int currentColor = WHITE_SIDE;
	
	//BOOLEANS
	boolean canMove;
	boolean validTile;
	
	//pieces
	public static ArrayList<Piece> arrPiece = new ArrayList<Piece>();
	public static ArrayList<Piece> simPiece = new ArrayList<Piece>();
	Piece selectedPiece;
	
	// Available moves for selected piece
	ArrayList<int[]> availableMoves = new ArrayList<int[]>();
	
	//sets the panel size
	public ChessPanel() {
		setPreferredSize(new Dimension(PANELWIDTH,PANELHEIGHT));
		setBackground(Color.lightGray);
		addMouseMotionListener(controller);
		addMouseListener(controller);
		createPieces();
		dupePieces(arrPiece,simPiece);
	}
	
	//creates a thread for the gameclock
	public void launchChess() {
		chessThread = new Thread(this);
		chessThread.start();
	}
	
	
	private void update() {
		if(controller.isPressed) {
			if(selectedPiece == null) {
				
				//find the piece that the player wanted to select
				//via loop and checking if the mouse matches column and row
				for(Piece piece: simPiece) {
					if(piece.color == currentColor && 
					   piece.col == controller.posX/ChessBoard.TILE_SIZE && 
					   piece.row == controller.posY/ChessBoard.TILE_SIZE) {
						
						selectedPiece = piece;
						// Calculate available moves when piece is selected
						calculateAvailableMoves();
					}
				}
			} else {
				simulate();
			}
		}
		
		if(!controller.isPressed) {
			if(selectedPiece != null) {
				if(validTile) {
					//update the list of piece when theres a capture
					 if(selectedPiece.targetPiece != null) {
		                    simPiece.remove(selectedPiece.targetPiece.getTargetIndex());
		                }
					
					dupePieces(simPiece,arrPiece);
					selectedPiece.movePosition();
					selectedPiece = null;
					// Clear available moves when piece is deselected
					availableMoves.clear();
				}else {
					dupePieces(arrPiece,simPiece);
					selectedPiece.revert();
					selectedPiece = null;
					// Clear available moves when move is invalid
					availableMoves.clear();
				}
			}
		}
	}
	
	
	private void simulate() {
		
		canMove = false;
		validTile = false;
		//reset the list of piece in every loop
		//this is for the removed pieces during simulation
		//centers the piece when selecting
		selectedPiece.posX = controller.posX - ChessBoard.HALF_TILE_SIZE;
		selectedPiece.posY = controller.posY - ChessBoard.HALF_TILE_SIZE;
		//centers the actual hitbox of the piece
		selectedPiece.col = selectedPiece.getColumn(selectedPiece.posX);
		selectedPiece.row = selectedPiece.getRow(selectedPiece.posY);
		
		if(selectedPiece.moveable(selectedPiece.col, selectedPiece.row)) {
			canMove = true;
			validTile = true;
			
		}
		
		
	}
	
	// Calculate all available moves for the selected piece
	private void calculateAvailableMoves() {
		availableMoves.clear();
		
		if(selectedPiece == null) {
			return;
		}
		
		// Check all tiles on the board
		for(int col = 0; col < 8; col++) {
			for(int row = 0; row < 8; row++) {
				// Temporarily set piece position for testing
				int originalCol = selectedPiece.col;
				int originalRow = selectedPiece.row;
				
				selectedPiece.col = col;
				selectedPiece.row = row;
				
				// Check if the piece can move to this position
				if(selectedPiece.moveable(col, row)) {
					availableMoves.add(new int[]{col, row});
				}
				
				// Restore original position
				selectedPiece.col = originalCol;
				selectedPiece.row = originalRow;
			}
		}
	}
	
	// Draw available move indicators
	private void drawAvailableMoves(Graphics2D g2) {
		if(selectedPiece == null || availableMoves.isEmpty()) {
			return;
		}
		
		// Save original composite
		AlphaComposite originalComposite = (AlphaComposite) g2.getComposite();
		
		// Set transparency for the indicators
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		
		for(int[] move : availableMoves) {
			int col = move[0];
			int row = move[1];
			
			int x = col * ChessBoard.TILE_SIZE;
			int y = row * ChessBoard.TILE_SIZE;
			
			// Check if there's an enemy piece at this position (for capture moves)
			boolean isCapture = false;
			for(Piece piece : simPiece) {
				if(piece.col == col && piece.row == row && piece.color != selectedPiece.color) {
					isCapture = true;
					break;
				}
			}
			
			if(isCapture) {
				// Draw red border for capture moves
				g2.setColor(Color.RED);
				g2.drawRect(x + 5, y + 5, ChessBoard.TILE_SIZE - 10, ChessBoard.TILE_SIZE - 10);
				g2.setColor(new Color(255, 0, 0, 100)); // Transparent red
				g2.fillRect(x + 5, y + 5, ChessBoard.TILE_SIZE - 10, ChessBoard.TILE_SIZE - 10);
			} else {
				// Draw green circle for regular moves
				g2.setColor(new Color(0, 255, 0, 150)); // Transparent green
				int circleSize = ChessBoard.TILE_SIZE / 3;
				int centerX = x + ChessBoard.TILE_SIZE / 2 - circleSize / 2;
				int centerY = y + ChessBoard.TILE_SIZE / 2 - circleSize / 2;
				g2.fillOval(centerX, centerY, circleSize, circleSize);
				
				// Add border
				g2.setColor(Color.GREEN);
				g2.drawOval(centerX, centerY, circleSize, circleSize);
			}
		}
		
		// Restore original composite
		g2.setComposite(originalComposite);
	}
	
	//create pieces
	//add an inverse for picking color later on
	public void createPieces() {
		 for (int col = 0; col < 8; col++) {
		        arrPiece.add(new Pawn(WHITE_SIDE, col, 6)); // row 6 = pawns
		    }
		 arrPiece.add(new Rook(WHITE_SIDE, 0, 7));
		 arrPiece.add(new Rook(WHITE_SIDE, 7, 3));
		 arrPiece.add(new Knight(WHITE_SIDE, 1, 7));
		 arrPiece.add(new Knight(WHITE_SIDE, 6, 7));
		 arrPiece.add(new Bishop(WHITE_SIDE, 2, 7));
		 arrPiece.add(new Bishop(WHITE_SIDE, 5, 7));
		 arrPiece.add(new Queen(WHITE_SIDE, 3, 7));
		 arrPiece.add(new King(WHITE_SIDE, 4, 7));
		
		for (int col = 0; col < 8; col++) {
	        arrPiece.add(new Pawn(BLACK_SIDE, col, 1)); // row 1 = pawns
	    }
	    arrPiece.add(new Rook(BLACK_SIDE, 0, 0));
	    arrPiece.add(new Rook(BLACK_SIDE, 7, 0));
	    arrPiece.add(new Knight(BLACK_SIDE, 1, 0));
	    arrPiece.add(new Knight(BLACK_SIDE, 6, 0));
	    arrPiece.add(new Bishop(BLACK_SIDE, 2, 0));
	    arrPiece.add(new Bishop(BLACK_SIDE, 5, 0));
	    arrPiece.add(new Queen(BLACK_SIDE, 3, 0));
	    arrPiece.add(new King(BLACK_SIDE, 4, 0));
	}
	
	private void dupePieces(ArrayList<Piece> source,ArrayList<Piece> target) {
		
		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	//used to draw
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		chessBoard.draw(g2);
		
		// Draw available moves before drawing pieces
		drawAvailableMoves(g2);
		
		for(Piece piece: simPiece) {
			piece.draw(g2);
		}
		
	}

	
	//has the gameclock
	@Override
	public void run() {
		// TODO Auto-generated method stub
		double updateInterval = 1000000000/framePerSecond;
		double difference = 0;
		long lastUpdate = System.nanoTime();
		long currentUpdate;
		
		
		while(chessThread != null) {
			
			currentUpdate = System.nanoTime();
			difference = (currentUpdate - lastUpdate)/updateInterval;
			
			if(difference >= 1) {
				update();
				repaint();
				difference--;
			}
		}
		
		
	}
}