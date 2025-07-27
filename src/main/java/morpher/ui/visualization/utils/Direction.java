package morpher.ui.visualization.utils;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public static Direction getDirection(String s) {
        return switch (s) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "east" -> Direction.EAST;
            case "west" -> Direction.WEST;
            default -> null;
        };
    }
    public static Direction getOppositeDir(Direction d) {
        return switch (d) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST  -> Direction.WEST;
            case WEST  -> Direction.EAST;
        };
    }
}

