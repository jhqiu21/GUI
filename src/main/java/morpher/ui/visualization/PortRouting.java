package morpher.ui.visualization;

import morpher.ui.visualization.utils.RoutingType;

/**
 * Represents the routing configuration for a single port, with input and output directions
 * specified via RoutingType.
 *
 * A PortRouting instance is immutable and provides utility methods for checking whether it
 * has an input or output, as well as methods for constructing modified versions with new
 * input or output settings.
 *
 * @param in  the input routing type
 * @param out the output routing type
 */
public record PortRouting(RoutingType in, RoutingType out) {
    public static final PortRouting EMPTY = new PortRouting(RoutingType.OPEN, RoutingType.OPEN);

    /**
     * Checks whether this port has an input routing (i.e., not OPEN).
     */
    public boolean haveInput() {
        return !(this.in == RoutingType.OPEN);
    }

    /**
     * Checks whether this port has an output routing (i.e., not OPEN).
     */
    public boolean haveOutput() {
        return !(this.out == RoutingType.OPEN);
    }

    /**
     * Returns a new PortRouting instance with the same output but a different input.
     *
     * @param newIn the new input routing type
     * @return a new PortRouting with the updated input
     */
    public PortRouting withIn(RoutingType newIn) {
        return new PortRouting(newIn, this.out);
    }

    /**
     * Returns a new PortRouting instance with the same input but a different output.
     *
     * @param newOut the new output routing type
     * @return a new PortRouting with the updated output
     */
    public PortRouting withOut(RoutingType newOut) {
        return new PortRouting(this.in, newOut);
    }
}