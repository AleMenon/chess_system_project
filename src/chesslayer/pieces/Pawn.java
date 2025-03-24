package chesslayer.pieces;

import boardlayer.Board;
import boardlayer.Position;
import chesslayer.ChessPiece;
import chesslayer.Color;

public class Pawn extends ChessPiece{
	
	private boolean firstMove;
	
	public Pawn(Board board, Color color) {
		super(board, color);
		firstMove=true;
	}
	
	@Override
	public String toString() {
		return "P";
	}
	
	private boolean isFirstMove(){
		if(getMoveCount()==0) {
			return firstMove;
		}
		else {
			firstMove=false;
			return firstMove;
		}
	}
	
	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		if (getColor()==Color.WHITE) {
			if ((this.position.getColumn() + 1 == position.getColumn() || this.position.getColumn() - 1 == position.getColumn()) && p != null && p.getColor() != getColor()) {
				return true;
			}		
			return this.position.getColumn()  == position.getColumn() && (this.position.getRow() - 1 == position.getRow() || (this.position.getRow() - 2 == position.getRow() && isFirstMove() && getBoard().piece(position.getRow()+1, position.getColumn()) == null)) && p == null;
			
		}
		else {
			if ((this.position.getColumn() + 1 == position.getColumn() || this.position.getColumn() - 1 == position.getColumn()) && p != null && p.getColor() != getColor()) {
				return true;
			}		
			return this.position.getColumn()  == position.getColumn() && (this.position.getRow() + 1 == position.getRow() || (this.position.getRow() + 2 == position.getRow() && isFirstMove() && getBoard().piece(position.getRow()-1, position.getColumn()) == null)) && p == null;
			
		}
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat=new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p=new Position(0, 0);
		
		//white
		if(getColor()==Color.WHITE) {	
			//first move
			if(isFirstMove()) {				
				p.setValues(position.getRow() - 2, position.getColumn());
				if (getBoard().positionExists(p) && canMove(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
			//above
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//right above
			p.setValues(position.getRow() - 1, position.getColumn()+1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//left above
			p.setValues(position.getRow() - 1, position.getColumn()-1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		else {
			//first move
			if(isFirstMove()) {				
				p.setValues(position.getRow() + 2, position.getColumn());
				if (getBoard().positionExists(p) && canMove(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
			//below
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//right below
			p.setValues(position.getRow() + 1, position.getColumn()+1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//left below
			p.setValues(position.getRow() + 1, position.getColumn()-1);
			if (getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		
		return mat;
	}
	

}
