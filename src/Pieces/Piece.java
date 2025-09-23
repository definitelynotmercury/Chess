package Pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.ChessBoard;
import Main.ChessPanel;
import Main.Type;

public class Piece {
	
	public Type type;
	public BufferedImage image;
	public int posX, posY;
	public int col, row, preCol, preRow;
	public int color;
	public Piece targetPiece;
	public boolean isMoved, twoTileMove;
	
	public Piece(int color, int col, int row) {
		this.color = color;
		this.col = col;
		this.row = row;
		posX = getXpos(col);
		posY = getYpos(row);
		preCol = col;
		preRow = row;
	}
	
	public int getXpos(int col) {
		return col * ChessBoard.TILE_SIZE;
	}
	
	public int getYpos(int row) {
		return row * ChessBoard.TILE_SIZE;
	}
	
	public int getColumn(int posX) {
		return (posX + ChessBoard.HALF_TILE_SIZE)/ChessBoard.TILE_SIZE;
	}
	
	public int getRow(int posY) {
		return (posY + ChessBoard.HALF_TILE_SIZE)/ChessBoard.TILE_SIZE;
	}
	
	// UPDATED: Now works with the single pieces list
	public int getTargetIndex() {
		for(int i = 0; i < ChessPanel.pieces.size(); i++) {
			if(ChessPanel.pieces.get(i) == this) {
				return i;
			}
		}
		return 0;
	}
	
	public void movePosition() {
		if(type == Type.PAWN) {
			if(Math.abs(row - preRow) == 2) {
				twoTileMove = true;
			}
		}
		
		isMoved = true;
		posX = getXpos(col);
		posY = getYpos(row);
		preCol = col; // Update preCol to current col
		preRow = row; // Update preRow to current row
	}
	
	public void revert() {
		col = preCol;
		row = preRow;
		posX = getXpos(col);
		posY = getYpos(row);
	}
	
	public boolean moveable(int targetColumn, int targetRow) {
		return false;
	}
	
	
	// UPDATED: Now works with the single pieces list (no more sync needed)
	public Piece getTargetPiece(int targetColumn, int targetRow) {
		for(Piece piece: ChessPanel.pieces) {
			if(piece.col == targetColumn && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	
	public boolean isValidTile(int targetColumn, int targetRow) {
		targetPiece = getTargetPiece(targetColumn, targetRow);
		
		if(targetPiece != null) {
			if(targetPiece.color != this.color) {
				return true;
			} else {
				targetPiece = null;
			}
		} else {
			return true;
		}
		return false;
	}
	
	public boolean isSameTile(int targetColumn, int targetRow) {
		if(targetColumn == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}
	
	// UPDATED: Simplified blocking checks (no more sync needed)
	public boolean isBlockedStraight(int targetColumn, int targetRow) {
		// Check every tile on the left of a single row
		for(int column = preCol-1; column > targetColumn; column--) {
			for(Piece piece : ChessPanel.pieces) {
				if(piece.col == column && piece.row == targetRow) {
					targetPiece = piece;
					return true;
				}
			}
		}
		
		// Check every tile on the right of a single row
		for(int column = preCol+1; column < targetColumn; column++) {
			for(Piece piece : ChessPanel.pieces) {
				if(piece.col == column && piece.row == targetRow) {
					targetPiece = piece;
					return true;
				}
			}
		}
		
		// Check every tile below on a single column
		for(int row = preRow+1; row < targetRow; row++) {
			for(Piece piece : ChessPanel.pieces) {
				if(piece.col == targetColumn && piece.row == row) {
					targetPiece = piece;
					return true;
				}
			}
		}
		
		// Check every tile above on a single column
		for(int row = preRow-1; row > targetRow; row--) {
			for(Piece piece : ChessPanel.pieces) {
				if(piece.col == targetColumn && piece.row == row) {
					targetPiece = piece;
					return true;
				}
			}
		}
		
		return false;
	}
	
	// UPDATED: Simplified diagonal blocking (no more sync needed)
	public boolean isBlockDiagonal(int targetColumn, int targetRow) {
		if(targetRow < preRow) {
			// up left
			for(int column = preCol - 1; column > targetColumn; column--) {
				int difference = Math.abs(column - preCol);
				for(Piece piece : ChessPanel.pieces) {
					if(piece.col == column && piece.row == preRow - difference) {
						targetPiece = piece;
						return true;
					}
				}
			}
			
			// up right
			for(int column = preCol + 1; column < targetColumn; column++) {
				int difference = Math.abs(column - preCol);
				for(Piece piece : ChessPanel.pieces) {
					if(piece.col == column && piece.row == preRow - difference) {
						targetPiece = piece;
						return true;
					}
				}
			}
		}
		
		if(targetRow > preRow) {
			// down left
			for(int column = preCol - 1; column > targetColumn; column--) {
				int difference = Math.abs(column - preCol);
				for(Piece piece : ChessPanel.pieces) {
					if(piece.col == column && piece.row == preRow + difference) {
						targetPiece = piece;
						return true;
					}
				}
			}
			
			// down right
			for(int column = preCol + 1; column < targetColumn; column++) {
				int difference = Math.abs(column - preCol);
				for(Piece piece : ChessPanel.pieces) {
					if(piece.col == column && piece.row == preRow + difference) {
						targetPiece = piece;
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public BufferedImage getImage(String filePath) {
		BufferedImage image = null;

		try {
			image = ImageIO.read(getClass().getResourceAsStream(filePath + ".png"));

			if (image == null) {
				System.out.println("⚠️ Failed to load image: " + filePath + ".png");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}
	
	public void draw(Graphics2D g2) {
		g2.drawImage(image, posX, posY, ChessBoard.TILE_SIZE, ChessBoard.TILE_SIZE, null);
	}
}