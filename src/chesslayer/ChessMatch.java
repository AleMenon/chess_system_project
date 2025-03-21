package chesslayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardlayer.Board;
import boardlayer.Piece;
import boardlayer.Position;
import chesslayer.pieces.King;
import chesslayer.pieces.Rook;

public class ChessMatch {

	private int turn;
	private boolean check;
	private boolean checkMate;
	private Color currentPlayer;
	private Board board;
	private List<Piece> piecesOnTheBoard=new ArrayList<>();
	private List<Piece> capturedPieces=new ArrayList<>();
	
	public ChessMatch() {
		board=new Board(8, 8);
		initialSetup();
		turn=1;
		currentPlayer=Color.WHITE;
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position=sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
			Position source=sourcePosition.toPosition();
			Position target=targetPosition.toPosition();
			validateSourcePosition(source);
			validateTargetPosition(source, target);
			Piece capturedPiece=makeMove(source, target);
			if(testCheck(currentPlayer)) {
				undoMove(source, target, capturedPiece);
				throw new ChessException("You can't put yourself in check.");
			}
			check=testCheck(opponent(currentPlayer));
			if(testCheckMate(opponent(currentPlayer))) {
				checkMate=true;
			}
			else {				
				nextTurn();
			}
			return (ChessPiece)capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		 Piece p=board.removePiece(source);
		 Piece capturedPiece=board.removePiece(target);
		 board.placePiece(p, target);
		 if(capturedPiece!=null) {
			 piecesOnTheBoard.remove(capturedPiece);
			 capturedPieces.add(capturedPiece);
		 }
		 return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		Piece p=board.removePiece(target);
		board.placePiece(p, source);
		if(capturedPiece!=null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There's no piece on the source position.");
		}
		if(currentPlayer!=((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours.");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There's no possible move for the chosen piece.");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece cannot be moved to the target position.");
		}
	}
	
	private void nextTurn() {
		turn++;
		if(currentPlayer==Color.WHITE) {			
			currentPlayer=Color.BLACK;
		}
		else {
			currentPlayer=Color.WHITE;
		}
	}
	
	private Color opponent(Color color) {
		if(color==Color.WHITE) {
			return Color.BLACK;
		}
		else {
			return Color.WHITE;
		}
	}
	
	private ChessPiece king(Color color) {			
		List<Piece> list=piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor()==color).collect(Collectors.toList());
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There's no "+color+" king on the board");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition=king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces=piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor()==opponent(color)).collect(Collectors.toList());
		for(Piece p:opponentPieces) {
			boolean[][] mat=p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]==true) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		List<Piece> list=piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor()==color).collect(Collectors.toList());
		int i, j;
		for(Piece p:list) {
			boolean[][]mat=p.possibleMoves();
			for(i=0; i<board.getRows(); i++) {
				for(j=0; j<board.getColumns();j++) {
					if(mat[i][j]) {
						Position source=((ChessPiece)p).getChessPosition().toPosition();
						Position target=new Position(i, j);
						Piece capturedPiece=makeMove(source, target);
						boolean testCheck=testCheck(color);
						undoMove(source, target, capturedPiece);
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));
        
        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
