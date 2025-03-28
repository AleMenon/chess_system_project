package chesslayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardlayer.Board;
import boardlayer.Piece;
import boardlayer.Position;
import chesslayer.pieces.Bishop;
import chesslayer.pieces.King;
import chesslayer.pieces.Knight;
import chesslayer.pieces.Pawn;
import chesslayer.pieces.Queen;
import chesslayer.pieces.Rook;

public class ChessMatch {

	private int turn;
	private boolean check;
	private boolean checkMate;
	
	private Color currentPlayer;
	private Board board;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
		turn = 1;
		currentPlayer = Color.WHITE;
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

	public ChessPiece[][] getPieces() {
		int i, j;
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		
		for (i = 0; i < board.getRows(); i++) {
			for (j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	public ChessPiece getPromoted() {
		return promoted;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check.");
		}
		
		ChessPiece movedPiece = (ChessPiece) board.piece(target);

		// Special move promotion
		promoted = null;
		if (movedPiece instanceof Pawn && (target.getRow() == 0 || target.getRow() == 7)) {
			promoted = movedPiece;
			promoted = replacePromotedPiece("Q");
		}

		check = testCheck(opponent(currentPlayer));
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} 
		else {
			nextTurn();
		}
		
		// Special move en passant
		if (movedPiece instanceof Pawn
				&& (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} 
		else {
			enPassantVulnerable = null;
		}
		return (ChessPiece) capturedPiece;
	}

	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There's no piece to be promoted.");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted;
		}

		Position p = promoted.getChessPosition().toPosition();
		
		piecesOnTheBoard.remove(board.removePiece(p));
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, p);
		piecesOnTheBoard.add(newPiece);
		return newPiece;
	}

	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) {
			return new Bishop(board, color);
		} 
		else if (type.equals("N")) {
			return new Knight(board, color);
		} 
		else if (type.equals("R")) {
			return new Rook(board, color);
		} 
		else {
			return new Queen(board, color);
		}
	}

	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		
		board.placePiece(p, target);
		((ChessPiece) p).increaseMoveCount();
		
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		// Castling king side
		if (p instanceof King && ((King) p).getChessPosition().toPosition().getColumn() - 2 == source.getColumn()) {
			source = ((King) p).getChessPosition().toPosition();
			target = ((King) p).getChessPosition().toPosition();
			source.setColumn(source.getColumn() + 1);
			target.setColumn(target.getColumn() - 1);
			p = board.removePiece(source);
			board.placePiece(p, target);
		}
		
		// Castling queen side
		if (p instanceof King && ((King) p).getChessPosition().toPosition().getColumn() + 2 == source.getColumn()) {
			source = ((King) p).getChessPosition().toPosition();
			target = ((King) p).getChessPosition().toPosition();
			source.setColumn(source.getColumn() - 2);
			target.setColumn(target.getColumn() + 1);
			p = board.removePiece(source);
			board.placePiece(p, target);
		}

		// White en passant
		if (p instanceof Pawn && capturedPiece == null && source.getRow() == 3 && (target.getColumn() + 1 == source.getColumn() || target.getColumn() - 1 == source.getColumn())) {
			capturedPiece = board.removePiece(new Position(target.getRow() + 1, target.getColumn()));
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);

		}

		// Black en passant
		if (p instanceof Pawn && capturedPiece == null && source.getRow() == 4 && (target.getColumn() + 1 == source.getColumn() || target.getColumn() - 1 == source.getColumn())) {
			capturedPiece = board.removePiece(new Position(target.getRow() - 1, target.getColumn()));
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		return capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		Piece p = board.removePiece(target);
		
		board.placePiece(p, source);
		((ChessPiece) p).decreaseMoveCount();
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}

		// Castling king side
		if (p instanceof King && ((King) p).getChessPosition().toPosition().getColumn() + 2 == source.getColumn()) {
			target.setColumn(source.getColumn() + 1);
			source.setColumn(source.getColumn() - 1);
			p = board.removePiece(source);
			board.placePiece(p, target);
			((ChessPiece) p).decreaseMoveCount();
		}

		// Castling queen side
		if (p instanceof King && ((King) p).getChessPosition().toPosition().getColumn() - 2 == source.getColumn()) {
			target.setColumn(source.getColumn() - 2);
			source.setColumn(source.getColumn() + 1);
			p = board.removePiece(source);
			board.placePiece(p, target);
			((ChessPiece) p).decreaseMoveCount();
		}

		// White en passant
		if (p instanceof Pawn && capturedPiece != null && source.getRow() == 3 && (target.getColumn() + 1 == source.getColumn() || target.getColumn() - 1 == source.getColumn())) {
			board.placePiece(capturedPiece, new Position(target.getRow() + 1, target.getColumn()));
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}

		// Black en passant
		if (p instanceof Pawn && capturedPiece == null && source.getRow() == 4 && (target.getColumn() + 1 == source.getColumn() || target.getColumn() - 1 == source.getColumn())) {
			board.placePiece(capturedPiece, new Position(target.getRow() - 1, target.getColumn()));
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There's no piece on the source position.");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours.");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There's no possible move for the chosen piece.");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece cannot be moved to the target position.");
		}
	}

	private void nextTurn() {
		turn++;
		if (currentPlayer == Color.WHITE) {
			currentPlayer = Color.BLACK;
		} 
		else {
			currentPlayer = Color.WHITE;
		}
	}

	private Color opponent(Color color) {
		if (color == Color.WHITE) {
			return Color.BLACK;
		} 
		else {
			return Color.WHITE;
		}
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There's no " + color + " king on the board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()] == true) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		
		int i, j;
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (i = 0; i < board.getRows(); i++) {
				for (j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
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
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));

		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
}
