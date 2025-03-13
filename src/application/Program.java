package application;

import java.util.Scanner;

import chesslayer.ChessMatch;
import chesslayer.ChessPiece;
import chesslayer.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		ChessMatch chessMatch=new ChessMatch();
		
		while(true) {		
			UI.printBoard(chessMatch.getPieces());
			System.out.println();
			System.out.print("Source: ");
			ChessPosition source=UI.readChessPosition(sc);
			
			System.out.println();
			System.out.println("Target: ");
			ChessPosition target=UI.readChessPosition(sc);
			
			ChessPiece capturedPiece=chessMatch.performChessMove(source, target);
		}
	}

}