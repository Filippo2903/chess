package client.movements;

import client.Client;
import client.Game;
import client.piece.Piece;
import gameUtils.SpecialMoveType;

import java.awt.Point;

public interface Movement {

	/**
	 * Check if the piece can move from a cell to another in a given board
	 * @param from The starting cell
	 * @param to The arrival cell
	 * @param board The board where the piece exists
	 * @return <code>true</code> if the piece can be moved, <code>false</code> if the piece cannot be moved
	 */
	default boolean canMove(Point from, Point to, Piece[][] board) {
		Piece[][] previousBoard = Client.getGame().getBoard();
		MovementUtils.setBoard(board);

		boolean canMove = canMove(from, to);

		MovementUtils.setBoard(previousBoard);
		return canMove;
	}

	/**
	 * Check if the piece can be moved
	 * @param from The starting cell
	 * @param to The arrival cell
	 * @return <code>true</code> if the piece can be moved, <code>false</code> if the piece cannot be moved
	 */
	boolean canMove(Point from, Point to);

	/**
	 * Get the special move
	 * @return <code>null</code> if there's not a special move, otherwise a <code>SpecialMoveType</code>
	 */
	default SpecialMoveType getSpecialMove() {
		return null;
	}
}
