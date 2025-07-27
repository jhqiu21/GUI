package morpher.ui.visualization.utils;

public enum RoutingType {
    OPEN,       // do not have any routing
    ALUOUT,     // output from alu
    INPUT,      // input from neighbour
    OUTPUT,     // output to neighbour
    OTHER;      // other routing type

    public static RoutingType get(String s) {
        try {
            if (s.endsWith("in")) {
                return RoutingType.INPUT;
            }
            if (s.endsWith("out")) {
                return RoutingType.OUTPUT;
            }
            return RoutingType.valueOf(s.trim().toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return OTHER;
        }
    }


//    public static boolean isDirection(RoutingType rt) {
//        return (rt == SOUTH || rt == NORTH || rt == EAST || rt == WEST);
//    }
//
//    public static Direction routingTypeConverter(RoutingType rt) {
//        if (isDirection(rt)) {
//            return switch (rt) {
//                case NORTH -> Direction.NORTH;
//                case SOUTH -> Direction.SOUTH;
//                case WEST -> Direction.WEST;
//                case EAST -> Direction.EAST;
//                default -> null;
//            };
//        }
//        return null;
//    }
}