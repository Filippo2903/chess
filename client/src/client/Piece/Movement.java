package client.Piece;

import java.awt.Point;

public interface Movement {
	boolean canMove(Point from, Point to);
}
