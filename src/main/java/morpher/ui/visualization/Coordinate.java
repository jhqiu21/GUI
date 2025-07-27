package morpher.ui.visualization;

import morpher.ui.visualization.utils.Direction;

public record Coordinate(int row, int col) {
    /**
     * @param c coordinate.
     * @param d direction of its neighbour.
     * @return the neighbour of coordinate in direction d.
     */
    public static Coordinate getNeighbourCoordinate(Coordinate c, Direction d) {
        int row = c.row();
        int col = c.col();
        return switch (d) {
            case NORTH -> new Coordinate(row - 1, col);
            case SOUTH -> new Coordinate(row + 1, col);
            case EAST  -> new Coordinate(row, col + 1);
            case WEST  -> new Coordinate(row, col - 1);
        };
    }
}
