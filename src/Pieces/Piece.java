package Pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.ChessBoard;
import Main.ChessPanel;

public class Piece {
	
	public BufferedImage image;
	public int posX, posY;
	public int col,row, preCol, preRow;
	public int color;
	public Piece targetPiece;
	
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
	
	public int getTargetIndex() {
		for(int i = 0; i < ChessPanel.simPiece.size(); i++) {
			if(ChessPanel.simPiece.get(i) == this) {
				return i;
			}
		}
		return 0;
	}
	
	public void movePosition() {
		posX = getXpos(col);
		posY = getYpos(row);
		preCol = getColumn(posX);
		preRow = getRow(posY);
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
	
	public boolean inBounds(int targetColumn, int targetRow) {
		
		if(targetColumn >= 0 && targetColumn <= 7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}
		return false;
	}
	
	public Piece getTargetPiece(int targetColumn,int targetRow) {
		for(Piece piece: ChessPanel.simPiece) {
			if(piece.col == targetColumn && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	
	public boolean isValidTile(int targetColumn,int targetRow) {
		
		 targetPiece = getTargetPiece(targetColumn,targetRow);
		
		if(targetPiece != null) {
			if(targetPiece.color != this.color) {
				return true;
			}else {
				targetPiece = null;
			}
		}else {
			return true;
		}
		return false;
	}
	
	public boolean isSameTile(int targetColumn,int targetRow) {
		if(targetColumn == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}
	
	
	public BufferedImage getImage(String filePath) {
	    BufferedImage image = null;

	    try {
	        // Debugging line to check resource path
	        System.out.println("Looking for: " + filePath + ".png");
	        System.out.println("Resolved URL: " + getClass().getResource(filePath + ".png"));

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
