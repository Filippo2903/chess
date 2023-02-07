package client.movements;

import gameUtils.SpecialMoveType;

import java.awt.Point;

public interface Movement {
	boolean canMove(Point from, Point to);

	default SpecialMoveType getSpecialMove() {
		return null;
	}
}
