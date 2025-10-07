package Main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

public class ChessPanel extends JPanel {
	
	public static final int PANELWIDTH = 1100;
	public static final int PANELHEIGHT = 800;
	
	ChessBoard chessBoard = new ChessBoard();
	
	//setting colors
	public static final int WHITE_SIDE = 0;
	public static final int BLACK_SIDE = 1;
	int currentColor = WHITE_SIDE;
	
	//BOOLEANS
	boolean promote;
	boolean gameOver;
	boolean vsAI = false;
	public static boolean playerIsWhite = true;
	boolean showColorSelection = false;
	
	
	//pieces - simplified to single list (no more arrPiece/simPiece confusion)
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
	ArrayList<Piece> promotionPiece = new ArrayList<Piece>();
	Piece selectedPiece;
	public static Piece castlePiece;
	
	// Available moves for selected piece
	ArrayList<int[]> availableMoves = new ArrayList<int[]>();
	
	//GameState 
	enum GameState { SETUP, PLAYING, GAME_OVER }
	GameState gameState = GameState.SETUP;
	
	//click rectangles
	Rectangle passPlayButton = new Rectangle(850, 200, 200, 50);
	Rectangle vsAIButton = new Rectangle(850, 270, 200, 50);
	Rectangle whiteButton = new Rectangle(850, 360, 200, 50);
	Rectangle blackButton = new Rectangle(850, 430, 200, 50);
	Rectangle startButton = new Rectangle(850, 500, 200, 50);
	
	//Ai
	private ChessAI ai;
	private int aiColor;
	private boolean waitingForAI = false;
	
	//sets the panel size
	public ChessPanel() {
		setPreferredSize(new Dimension(PANELWIDTH,PANELHEIGHT));
		setBackground(Color.lightGray);
		setupMouseHandlers();
	}
	
	// NEW: Event-driven mouse handling
	private void setupMouseHandlers() {
		MouseAdapter mouseHandler = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleMouseClick(e.getX(), e.getY());
			}
		};
		
		addMouseListener(mouseHandler);
	}
	
	// NEW: Main click handler
	private void handleMouseClick(int mouseX, int mouseY) {
		int clickedCol = mouseX / ChessBoard.TILE_SIZE;
		int clickedRow = mouseY / ChessBoard.TILE_SIZE;
		
		if (gameState == GameState.SETUP) {
			handleSetup(mouseX, mouseY);
	    } else if (promote) {
			// During promotion, allow clicks in the promotion area (column 9)
			handlePromotion(clickedCol, clickedRow);
		} else {
			// During regular play, only allow clicks within board bounds
			if (clickedCol < 0 || clickedCol > 7 || clickedRow < 0 || clickedRow > 7) {
				return;
			}
			handleRegularMove(clickedCol, clickedRow);
		}
		
		repaint(); // Only repaint when something actually changes
	}
	
	private void handleSetup(int mouseX, int mouseY) {
		
		// Pass & Play button
	    if (passPlayButton.contains(mouseX, mouseY)) {
	        vsAI = false;
	        showColorSelection = false;
	        return;
	    }
	    
	    // vs AI button
	    if (vsAIButton.contains(mouseX, mouseY)) {
	        vsAI = true;
	        showColorSelection = true;
	        playerIsWhite = true; // Default to white
	        return;
	    }
	    
	    // Color selection buttons (only if vs AI selected)
	    if (showColorSelection) {
	        if (whiteButton.contains(mouseX, mouseY)) {
	            playerIsWhite = true;
	            return;
	        }
	        
	        if (blackButton.contains(mouseX, mouseY)) {
	            playerIsWhite = false;
	            return;
	        }
	    }
	    
	    // Start game button
	    if (startButton.contains(mouseX, mouseY)) {
	        if (vsAI && !showColorSelection) return; // Need color selection for AI
	        
	        // Start the game
	        gameState = GameState.PLAYING;
	        setupGame();
	        return;
	    }
		
	}
	
	private void drawSetupScreen(Graphics2D g2) {
	    // Set up text rendering (like your existing status messages)
	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    
	    // Title
	    g2.setFont(new Font("Book Antiqua", Font.BOLD, 40));
	    g2.setColor(Color.white);
	    g2.drawString("Chess Game ", 840, 150);
	    
	    // Game mode buttons
	    g2.setFont(new Font("Book Antiqua", Font.PLAIN, 24));
	    
	    // Pass & Play button
	    g2.setColor(vsAI ? Color.lightGray : Color.green);
	    g2.fillRect(passPlayButton.x, passPlayButton.y, passPlayButton.width, passPlayButton.height);
	    g2.setColor(Color.black);
	    g2.drawRect(passPlayButton.x, passPlayButton.y, passPlayButton.width, passPlayButton.height);
	    g2.drawString("Pass & Play", passPlayButton.x + 20, passPlayButton.y + 30);
	    
	    // vs AI button
	    g2.setColor(vsAI ? Color.green : Color.lightGray);
	    g2.fillRect(vsAIButton.x, vsAIButton.y, vsAIButton.width, vsAIButton.height);
	    g2.setColor(Color.black);
	    g2.drawRect(vsAIButton.x, vsAIButton.y, vsAIButton.width, vsAIButton.height);
	    g2.drawString("vs Computer", vsAIButton.x + 20, vsAIButton.y + 30);
	    
	    // Color selection (only if vs AI)
	    if (showColorSelection) {
	        g2.setColor(Color.white);
	        g2.drawString("Choose your color:", 850, 350);
	        
	        // White button
	        g2.setColor(playerIsWhite ? Color.green : Color.lightGray);
	        g2.fillRect(whiteButton.x, whiteButton.y, whiteButton.width, whiteButton.height);
	        g2.setColor(Color.black);
	        g2.drawRect(whiteButton.x, whiteButton.y , whiteButton.width, whiteButton.height);
	        g2.drawString("Play White", whiteButton.x + 20, whiteButton.y + 30);
	        
	        // Black button
	        g2.setColor(!playerIsWhite ? Color.green : Color.lightGray);
	        g2.fillRect(blackButton.x, blackButton.y, blackButton.width, blackButton.height);
	        g2.setColor(Color.black);
	        g2.drawRect(blackButton.x, blackButton.y, blackButton.width, blackButton.height);
	        g2.drawString("Play Black", blackButton.x + 20, blackButton.y + 30);
	    }
	    
	    // Start button
	    g2.setColor(Color.yellow);
	    g2.fillRect(startButton.x, startButton.y, startButton.width, startButton.height);
	    g2.setColor(Color.black);
	    g2.drawRect(startButton.x, startButton.y, startButton.width, startButton.height);
	    g2.drawString("Start Game", startButton.x + 20, startButton.y + 30);
	}
	
	private void setupGame() {
	    createPieces();
	    gameState = GameState.PLAYING;
	    
	    if(vsAI) {
	        // Set AI color (opposite of player)
	        aiColor = playerIsWhite ? BLACK_SIDE : WHITE_SIDE;
	        ai = new ChessAI(aiColor);
	        
	        // If AI goes first (player chose black)
	        if(aiColor == WHITE_SIDE) {
	            aiTurnToMove();
	        }
	    }
	}
	
	private void aiTurnToMove() {
		if(!vsAI || currentColor != aiColor) {
	        return;
	    }
		
		waitingForAI = true;
		
		Move aiMove = ai.selectBestMove(pieces, this);
		
		if(aiMove != null) {
            // Execute the AI's chosen move
            executeAIMove(aiMove);
            waitingForAI = false;
            repaint();
        }
		
		
	}
	
	private void executeAIMove(Move move) {
		selectedPiece = move.piece;
		executeMove(move.toCol, move.toRow);
	}
	
	// NEW: Handle regular game moves
	private void handleRegularMove(int clickedCol, int clickedRow) {
		if (gameOver || waitingForAI) {
	        return; // Don't allow moves if game is over or AI is thinking
	    }
	    
	    // Prevent player from moving AI pieces
	    if(vsAI && currentColor == aiColor) {
	        return;
	    }
	    
		if (selectedPiece == null) {
			// Try to select a piece
			selectPiece(clickedCol, clickedRow);
		} else {
			// Try to move the selected piece
			attemptMove(clickedCol, clickedRow);
		}
	}
	
	// NEW: Select a piece
	private void selectPiece(int col, int row) {
		for (Piece piece : pieces) {
			if (piece.col == col && piece.row == row && piece.color == currentColor) {
				selectedPiece = piece;
				calculateAvailableMoves();
				break;
			}
		}
	}
	
	// NEW: Attempt to move selected piece
	private void attemptMove(int targetCol, int targetRow) {
		// Check if the move is valid using our piece's moveable method
		if (selectedPiece.moveable(targetCol, targetRow)) {
			// Check if this move would expose our king
			if (!moveExposesKing(selectedPiece, targetCol, targetRow)) {
				executeMove(targetCol, targetRow);
			} else {
				// Invalid move - deselect piece
				deselectPiece();
			}
		} else {
			// Invalid move - deselect piece
			deselectPiece();
		}
	}
	
	// NEW: Execute a valid move
	private void executeMove(int targetCol, int targetRow) {
		// Handle captures (including en passant)
		handleCaptures(targetCol, targetRow);
		
		// Move the piece
		selectedPiece.col = targetCol;
		selectedPiece.row = targetRow;
		selectedPiece.movePosition();
		
		// Handle castling
		if (castlePiece != null) {
			castle();
		}
		
		// Check for promotion
		if (canPromote()) {
			promote = true;
			setupPromotionOptions();
		} else {
			finishMove();
		}
	}
	
	// NEW: Handle captures including en passant
	private void handleCaptures(int targetCol, int targetRow) {
		// Regular capture
		Piece capturedPiece = getPieceAt(targetCol, targetRow);
		if (capturedPiece != null && capturedPiece.color != selectedPiece.color) {
			pieces.remove(capturedPiece);
			return;
		}
		
		// En passant capture
		if (selectedPiece.type == Type.PAWN && Math.abs(targetCol - selectedPiece.col) == 1) {
			Piece enPassantTarget = getPieceAt(targetCol, selectedPiece.row);
			if (enPassantTarget != null && enPassantTarget.type == Type.PAWN && 
				enPassantTarget.color != selectedPiece.color && enPassantTarget.twoTileMove) {
				pieces.remove(enPassantTarget);
			}
		}
	}
	
	// NEW: Get piece at specific location
	public static Piece getPieceAt(int col, int row) {
		for (Piece piece : pieces) {
			if (piece.col == col && piece.row == row) {
				return piece;
			}
		}
		return null;
	}
	
	// NEW: Deselect piece
	private void deselectPiece() {
		selectedPiece = null;
		availableMoves.clear();
		castlePiece = null;
	}
	
	// NEW: Finish move and pass turn
	private void finishMove() {
		deselectPiece();
		passTurn();
		if(vsAI && currentColor == aiColor && !gameOver) {
	        aiTurnToMove();
	    }
	}
	
	// NEW: Setup promotion options
	private void setupPromotionOptions() {
		promotionPiece.clear();
		promotionPiece.add(new Rook(currentColor, 9, 2));
		promotionPiece.add(new Knight(currentColor, 9, 3));
		promotionPiece.add(new Bishop(currentColor, 9, 4));
		promotionPiece.add(new Queen(currentColor, 9, 5));
	}
	
	// NEW: Handle promotion selection
	private void handlePromotion(int col, int row) {
		// Debug: Print what was clicked
		System.out.println("Promotion click at: " + col + ", " + row);
		
		for (Piece piece : promotionPiece) {
			System.out.println("Checking piece at: " + piece.col + ", " + piece.row + " (type: " + piece.type + ")");
			if (piece.col == col && piece.row == row) {
				System.out.println("Promotion selected: " + piece.type);
				
				// Store the position where the pawn promoted
				int promotionCol = selectedPiece.col;
				int promotionRow = selectedPiece.row;
				
				// Remove the pawn
				pieces.remove(selectedPiece);
				
				// Add the new piece at the promotion location
				switch (piece.type) {
					case ROOK: pieces.add(new Rook(currentColor, promotionCol, promotionRow)); break;
					case KNIGHT: pieces.add(new Knight(currentColor, promotionCol, promotionRow)); break;
					case BISHOP: pieces.add(new Bishop(currentColor, promotionCol, promotionRow)); break;
					case QUEEN: pieces.add(new Queen(currentColor, promotionCol, promotionRow)); break;
					default: break;
				}
				
				promote = false;
				finishMove();
				return;
			}
		}
		System.out.println("No promotion piece found at clicked location");
	}
	
	// KEPT: handles the turns (simplified - no more sync issues)
	private void passTurn() {
		// Switch turns first
		if(currentColor == WHITE_SIDE) {
			currentColor = BLACK_SIDE;
			
			// Remove en passant option from black pieces
			for(Piece piece: pieces) {
				if(piece.color == BLACK_SIDE) {
					piece.twoTileMove = false;
				}
			}
		} else {
			currentColor = WHITE_SIDE;
			
			// Remove en passant option from white pieces
			for(Piece piece: pieces) {
				if(piece.color == WHITE_SIDE) {
					piece.twoTileMove = false;
				}
			}
		}
		
		if (checkMate()) {
	        String winner = (currentColor == WHITE_SIDE) ? "Black" : "White";
	        System.out.println("CHECKMATE! " + winner + " wins!");
	        gameOver = true; // You'll need to add this boolean field
	        return; // Don't do further checks
	    }
		
		 if (staleMate()) {
		        System.out.println("STALEMATE! Game is a draw!");
		        gameOver = true;
		        return;
		}
		
		// Check if the current player's king is in check
		Piece king = getMyKing(currentColor);
		if(isCheck(king)) {
			String playerColor = (currentColor == WHITE_SIDE) ? "White" : "Black";
			System.out.println(playerColor + " king is in check");
		}
	}
	
	// KEPT: Castle logic
	private void castle() {
		// King-side castling: king moves to column 6, rook moves to column 5
		if(selectedPiece.col == 6) {
			castlePiece.col = 5;
		} 
		// Queen-side castling: king moves to column 2, rook moves to column 3
		else if(selectedPiece.col == 2) {
			castlePiece.col = 3;
		}
		castlePiece.movePosition();
		castlePiece = null;
	}
	
	// KEPT: Check if promotion is available
	private boolean canPromote() {
		if(selectedPiece.type == Type.PAWN) {
			if(playerIsWhite) {
				if(currentColor == WHITE_SIDE && selectedPiece.row == 0 || 
						   currentColor == BLACK_SIDE && selectedPiece.row == 7) {
							return true;
						}
			}else {
				if(currentColor == BLACK_SIDE && selectedPiece.row == 0 || 
						   currentColor == WHITE_SIDE && selectedPiece.row == 7) {
							return true;
						}
			}
			
		}
		return false;
	}
	
	// KEPT: King attack validation (simplified - no more sync needed)
	public boolean kingMovesToAttackedSquare(Piece king, int targetColumn, int targetRow) {
		// Store original position
		int originalColumn = king.col;
		int originalRow = king.row;
		
		// Move king to target position temporarily
		king.col = targetColumn;
		king.row = targetRow;
		
		// Check if there's a piece at target (we'll need to remove it temporarily)
	    Piece pieceAtTarget = null;
		
		for(Piece piece: pieces) {
			// Check if any enemy piece can attack the target position
			if(piece != king && piece.col == targetColumn && piece.row == targetRow) {
				pieceAtTarget = piece;
				break;
			}
		}
		
		if(pieceAtTarget != null) {
	        pieces.remove(pieceAtTarget);
	    }
		
		boolean isAttacked = false;
	    for(Piece piece: pieces) {
	        if(piece != king && piece.color != king.color && piece.moveable(targetColumn, targetRow)) {
	            isAttacked = true;
	            break;
	        }
	    }
	    
	    if(pieceAtTarget != null) {
	        pieces.add(pieceAtTarget);
	    }
		
		// Restore original position
		king.col = originalColumn;
		king.row = originalRow;
		return isAttacked;
	}

	// KEPT: Check if move exposes king (simplified)
	public boolean moveExposesKing(Piece selectedPiece, int targetColumn, int targetRow) {
		if(selectedPiece.type == Type.KING) {
			return kingMovesToAttackedSquare(selectedPiece, targetColumn, targetRow);
		}
		
		Piece king = getMyKing(currentColor);
		
		// Store original position
		int originalColumn = selectedPiece.col;
		int originalRow = selectedPiece.row;
		
		// Store captured piece (if any)
		Piece capturedPiece = getPieceAt(targetColumn, targetRow);
		if (capturedPiece != null && capturedPiece.color == selectedPiece.color) {
			capturedPiece = null; // Can't capture own piece
		}
		
		// Simulate the move
		selectedPiece.col = targetColumn;
		selectedPiece.row = targetRow;
		
		// Remove captured piece temporarily
		if(capturedPiece != null) {
			pieces.remove(capturedPiece);
		}
		
		// Check if king is in check after this move
		boolean wouldBeInCheck = isCheck(king);
		
		// Restore the game state
		selectedPiece.col = originalColumn;
		selectedPiece.row = originalRow;
		
		// Restore captured piece
		if(capturedPiece != null) {
			pieces.add(capturedPiece);
		}
		
		return wouldBeInCheck;
	}
	
	// KEPT: Get current player's king
	public Piece getMyKing(int currentColor) {
		for(Piece piece : pieces) {
			if(piece.type == Type.KING && piece.color == currentColor) {
				return piece;
			}
		}
		System.out.println("ERROR: No king found for color " + currentColor);
		return null;
	}
	
	public Piece enemyMyKing(int currentColor) {
		for(Piece piece : pieces) {
			if(piece.type == Type.KING && piece.color != currentColor) {
				return piece;
			}
		}
		System.out.println("ERROR: No king found for color " + currentColor);
		return null;
	}
	
	// KEPT: Check if king is in check
	public boolean isCheck(Piece king) {
		if(king == null) {
			return false;
		}
		
		for(Piece piece: pieces) {
			if(piece.color != king.color && piece.moveable(king.col, king.row)) {
				return true;
			}
		}
		return false;
	}
	
	// IMPROVED: Calculate available moves (fixed the bug you mentioned)
	private void calculateAvailableMoves() {
		availableMoves.clear();
		
		if(selectedPiece == null) {
			return;
		}
		
		// Check all tiles on the board
		for(int col = 0; col < 8; col++) {
			for(int row = 0; row < 8; row++) {
				// Check if the piece can move to this position
				if(selectedPiece.moveable(col, row)) {
					// Check if this move would expose our king
					if(!moveExposesKing(selectedPiece, col, row)) {
						availableMoves.add(new int[]{col, row});
					}
				}
			}
		}
		
		
	}
	
	private boolean checkMate() {
			
		Piece king = getMyKing(currentColor);
		
		if (!isCheck(king)) {
	        return false;
	    }
		
		for(Piece piece: new ArrayList<>(pieces)) {
		    if(piece.color == currentColor) {
		        for(int col = 0; col < 8; col++) {
		            for(int row = 0; row < 8; row++) {
		                if(piece.moveable(col, row)) {
		                    if(!moveExposesKing(piece, col, row)) {
		                        return false;
		                    }
		                }
		            }
		        }
		    }
		}
		return true;
	}
	
	private boolean staleMate() {
		
		Piece king = getMyKing(currentColor);
		
		if (isCheck(king)) {
	        return false;
	    }
		
		for(Piece piece: pieces) {
			if(piece.color == currentColor) {
				for(int col = 0; col < 8; col++) {
					for(int row = 0; row < 8; row++) {
						// Check if the piece can move to this position
						if(piece.moveable(col, row)) {
							// Check if this move would expose our king
							if(!moveExposesKing(piece, col, row)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	// KEPT: Draw available move indicators
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
			
			// Check if there's an enemy piece at this position
			boolean isCapture = false;
			Piece pieceAtTarget = getPieceAt(col, row);
			if (pieceAtTarget != null && pieceAtTarget.color != selectedPiece.color) {
				isCapture = true;
			}
			
			if(isCapture) {
				// Draw red border for capture moves
				g2.setColor(Color.RED);
				g2.drawRect(x + 5, y + 5, ChessBoard.TILE_SIZE - 10, ChessBoard.TILE_SIZE - 10);
				g2.setColor(new Color(255, 0, 0, 100));
				g2.fillRect(x + 5, y + 5, ChessBoard.TILE_SIZE - 10, ChessBoard.TILE_SIZE - 10);
			} else {
				// Draw green circle for regular moves
				g2.setColor(new Color(0, 255, 0, 150));
				int circleSize = ChessBoard.TILE_SIZE / 3;
				int centerX = x + ChessBoard.TILE_SIZE / 2 - circleSize / 2;
				int centerY = y + ChessBoard.TILE_SIZE / 2 - circleSize / 2;
				g2.fillOval(centerX, centerY, circleSize, circleSize);
				
				g2.setColor(Color.GREEN);
				g2.drawOval(centerX, centerY, circleSize, circleSize);
			}
		}
		
		// Restore original composite
		g2.setComposite(originalComposite);
	}
	
	// KEPT: Create pieces
	public void createPieces() {
		
		if(playerIsWhite) {
			// White pieces
			for (int col = 0; col < 8; col++) {
				pieces.add(new Pawn(WHITE_SIDE, col, 6));
			}
				pieces.add(new Rook(WHITE_SIDE, 0, 7));
				pieces.add(new Knight(WHITE_SIDE, 1, 7));
				pieces.add(new Bishop(WHITE_SIDE, 2, 7));
				pieces.add(new Queen(WHITE_SIDE, 3, 7));
				pieces.add(new King(WHITE_SIDE, 4, 7));
				pieces.add(new Bishop(WHITE_SIDE, 5, 7));
				pieces.add(new Knight(WHITE_SIDE, 6, 7));
				pieces.add(new Rook(WHITE_SIDE, 7, 7));
				
			// Black pieces
			for (int col = 0; col < 8; col++) {
				pieces.add(new Pawn(BLACK_SIDE, col, 1));
			}
				pieces.add(new Rook(BLACK_SIDE, 0, 0));
				pieces.add(new Knight(BLACK_SIDE, 1, 0));
				pieces.add(new Bishop(BLACK_SIDE, 2, 0));
				pieces.add(new Queen(BLACK_SIDE, 3, 0));
				pieces.add(new King(BLACK_SIDE, 4, 0));
				pieces.add(new Bishop(BLACK_SIDE, 5, 0));
				pieces.add(new Knight(BLACK_SIDE, 6, 0));
				pieces.add(new Rook(BLACK_SIDE, 7, 0));
			}else {
				// Black pieces (at bottom for Black POV)
				for (int col = 0; col < 8; col++) {
				    pieces.add(new Pawn(BLACK_SIDE, col, 6));
				}
				pieces.add(new Rook(BLACK_SIDE, 0, 7));
				pieces.add(new Knight(BLACK_SIDE, 1, 7));
				pieces.add(new Bishop(BLACK_SIDE, 2, 7));
				pieces.add(new Queen(BLACK_SIDE, 4, 7));
				pieces.add(new King(BLACK_SIDE, 3, 7));
				pieces.add(new Bishop(BLACK_SIDE, 5, 7));
				pieces.add(new Knight(BLACK_SIDE, 6, 7));
				pieces.add(new Rook(BLACK_SIDE, 7, 7));

				// White pieces (at top for Black POV)
				for (int col = 0; col < 8; col++) {
				    pieces.add(new Pawn(WHITE_SIDE, col, 1));
				}
				pieces.add(new Rook(WHITE_SIDE, 0, 0));
				pieces.add(new Knight(WHITE_SIDE, 1, 0));
				pieces.add(new Bishop(WHITE_SIDE, 2, 0));
				pieces.add(new Queen(WHITE_SIDE, 4, 0));
				pieces.add(new King(WHITE_SIDE, 3, 0));
				pieces.add(new Bishop(WHITE_SIDE, 5, 0));
				pieces.add(new Knight(WHITE_SIDE, 6, 0));
				pieces.add(new Rook(WHITE_SIDE, 7, 0));

			}
		}
		
	
	// KEPT: Paint component (simplified - no more sync issues)
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);

	    Graphics2D g2 = (Graphics2D)g;
	    
	    

	    chessBoard.draw(g2);
	    
	    if (gameState == GameState.SETUP) {
	        drawSetupScreen(g2);
	        return;
	    }

	    // Draw available moves before drawing pieces
	    drawAvailableMoves(g2);

	    // Draw all pieces (much simpler now)
	    for(Piece piece: pieces) {
	        piece.draw(g2);
	    }

	    // Status messages
	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
	    g2.setColor(Color.white);

	    if(promote) {
	        g2.drawString("Promote to:", 840, 150);
	        for(Piece piece: promotionPiece) {
	            g2.drawImage(piece.image, piece.getXpos(piece.col), piece.getYpos(piece.row),
	                    ChessBoard.TILE_SIZE, ChessBoard.TILE_SIZE, null);
	        }
	    } else if(gameOver) { // ADD THIS - Check for game over first
	        // Make the text bigger and more prominent for game over
	        g2.setFont(new Font("Book Antiqua", Font.BOLD, 50));
	        g2.setColor(Color.RED);
	        
	        if(checkMate()) {
	        	 g2.setColor(Color.WHITE);
	            // Determine winner (opposite of current player since they can't move)
	            String winner = (currentColor == WHITE_SIDE) ? "Black Wins!" : "White Wins!";
	            g2.drawString(winner, 820, 350);
	            g2.drawString("Checkmate!", 820, 420);
	        } else if(staleMate()) { // You'll need to create this method
	            g2.drawString("Stalemate!", 840, 350);
	            g2.drawString("Draw!", 840, 420);
	        }
	    } else {
	        // Normal turn messages
	        if (currentColor == WHITE_SIDE) {
	            g2.drawString("White's turn", 840, 550);
	        } else {
	            g2.drawString("Black's turn", 840, 250);
	        }
	    }
	}
}