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
}