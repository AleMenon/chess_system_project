package chesslayer.pieces;

import boardlayer.Board;
import boardlayer.Position;
import chesslayer.ChessMatch;
import chesslayer.ChessPiece;
import chesslayer.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;
	
	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch=chessMatch;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}
	
	private boolean testRookCastling(Position position) {
		ChessPiece p=(ChessPiece)getBoard().piece(position);
		return p!=null && p instanceof Rook && p.getColor()==getColor() && p.getMoveCount()==0;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);
		
		int i=0;

		// above
		p.setValues(position.getRow() - 1, position.getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// below
		p.setValues(position.getRow() + 1, position.getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// right
		p.setValues(position.getRow(), position.getColumn()+1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// left
		p.setValues(position.getRow(), position.getColumn()-1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// right above
		p.setValues(position.getRow() - 1, position.getColumn()+1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// right below
		p.setValues(position.getRow() + 1, position.getColumn()+1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// left above
		p.setValues(position.getRow() - 1, position.getColumn()-1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// left below
		p.setValues(position.getRow() + 1, position.getColumn()-1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//special move Castling
		if(getMoveCount()==0 && !chessMatch.getCheck()) {
			//king side
			p.setValues(position.getRow(), position.getColumn()+1);
			while(i<2) {
				if(getBoard().piece(p)==null) {
					p.setColumn(p.getColumn()+1);
				}
				i++;
			}
			if(testRookCastling(p)) {
				mat[p.getRow()][p.getColumn()-1]=true;
			}
			
			//queen side
			i=0;
			p.setValues(position.getRow(), position.getColumn()-1);
			while(i<3) {
				if(getBoard().piece(p)==null) {
					p.setColumn(p.getColumn()-1);
				}
				i++;
			}
			if(testRookCastling(p)) {
				mat[p.getRow()][p.getColumn()+2]=true;
			}
		}
		return mat;
	}

}
