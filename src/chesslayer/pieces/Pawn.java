package chesslayer.pieces;

import boardlayer.Board;
import boardlayer.Position;
import chesslayer.ChessMatch;
import chesslayer.ChessPiece;
import chesslayer.Color;

public class Pawn extends ChessPiece {
	
	private boolean firstMove;
	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		firstMove = true;
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "P";
	}

	private boolean isFirstMove() {
		if (getMoveCount() == 0) {
			return firstMove;
		} 
		else {
			firstMove = false;
			return firstMove;
		}
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		if (getColor() == Color.WHITE) {
			if ((this.position.getColumn() + 1 == position.getColumn() || this.position.getColumn() - 1 == position.getColumn()) && p != null && p.getColor() != getColor()) {
				return true;
			}
			return this.position.getColumn() == position.getColumn() && (this.position.getRow() - 1 == position.getRow() || (this.position.getRow() - 2 == position.getRow() && isFirstMove() && getBoard().piece(position.getRow() + 1, position.getColumn()) == null)) && p == null;

		} 
		else {
			if ((this.position.getColumn() + 1 == position.getColumn()
					|| this.position.getColumn() - 1 == position.getColumn()) && p != null
					&& p.getColor() != getColor()) {
				return true;
			}
			return this.position.getColumn() == position.getColumn() && (this.position.getRow() + 1 == position.getRow() || (this.position.getRow() + 2 == position.getRow() && isFirstMove() && getBoard().piece(position.getRow() - 1, position.getColumn()) == null)) && p == null;
		}
	}

	@Override
	public boolean[][] possibleMoves() {
		Position p = new Position(0, 0);
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		// white
		if (getColor() == Color.WHITE) {
			// first move
			if (isFirstMove()) {
				p.setValues(position.getRow() - 2, position.getColumn());
				if (getBoard().positionExists(p) && canMove(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
			
			// above
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// right above
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// left above
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// Special move en passant
			if (position.getRow() == 3) {
				if(position.getColumn()>=0) {
					p.setValues(position.getRow(), position.getColumn() - 1);					
				}
				if (chessMatch.getEnPassantVulnerable()!=null && getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
					mat[p.getRow() - 1][p.getColumn()] = true;
				}

				if(position.getColumn()+1<8) {					
					p.setValues(position.getRow(), position.getColumn() + 1);
				}
				if (chessMatch.getEnPassantVulnerable()!=null && getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
					mat[p.getRow() - 1][p.getColumn()] = true;
				}
			}
		} 
		else {
			// first move
			if (isFirstMove()) {
				p.setValues(position.getRow() + 2, position.getColumn());
				if (getBoard().positionExists(p) && canMove(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
			
			// below
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// right below
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// left below
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// Special move en passant
			if (position.getRow() == 4) {
				if(position.getColumn()+1<8) {
					p.setValues(position.getRow(), position.getColumn() + 1);					
				}
				if (chessMatch.getEnPassantVulnerable()!=null && getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
					mat[p.getRow() + 1][p.getColumn()] = true;
				}

				if(position.getColumn()-1>=0) {
					p.setValues(position.getRow(), position.getColumn() - 1);					
				}
				if (chessMatch.getEnPassantVulnerable()!=null && getBoard().piece(p) == chessMatch.getEnPassantVulnerable()) {
					mat[p.getRow() + 1][p.getColumn()] = true;
				}
			}
		}
		return mat;
	}
}
