package morpher.ui.visualization;

import morpher.ui.visualization.utils.RoutingType;

public record PortRouting(RoutingType in, RoutingType out) {
    public static final PortRouting EMPTY = new PortRouting(RoutingType.OPEN, RoutingType.OPEN);

    public boolean haveInput() {
        return !(this.in == RoutingType.OPEN);
    }

    public boolean haveOutput() {
        return !(this.out == RoutingType.OPEN);
    }

    public PortRouting withOut(RoutingType newOut) {
        return new PortRouting(this.in, newOut);
    }

    public PortRouting withIn(RoutingType newIn) {
        return new PortRouting(newIn, this.out);
    }
}