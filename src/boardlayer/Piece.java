package boardlayer;

public abstract class Piece {
	
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
	}

	protected Board getBoard() {
		return board;
	}
	
	public abstract boolean[][] possibleMoves();
	
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}
	
	public boolean isThereAnyPossibleMove() {
		boolean[][] mat=possibleMoves();
		
		int i, j;
		
		for(i=0; i<mat.length; i++) {
			for(j=0; j<mat.length; j++) {
				if(mat[i][j]==true) {
					return true;
				}
			}
		}
		return false;
	}
	
}
