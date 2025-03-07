package chesslayer;

import boardlayer.Board;

public class ChessMatch {

	private Board board;

	public ChessMatch() {
		board=new Board(8, 8);
	}
	
	public ChessPiece[][] getPieces(){
		 ChessPiece[][] mat=new ChessPiece[board.getRows()][board.getColumns()];
		 int i, j;
		 
		 for(i=0; i<board.getRows(); i++) {
			 for(j=0; j<board.getColumns(); j++) {
				 mat[i][j]=(ChessPiece) board.piece(i, j);
			 }
		 }
		 
		 return mat;
	}
}
