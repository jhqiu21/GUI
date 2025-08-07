package morpher.ui.visualization;

import java.util.List;

/**
 * Represents a Processing Element (PE) at a given {@link Coordinate}, which holds
 * time-dependent {@link Mapping} and {@link Routing} information.
 *
 * The PE can support periodic routing behavior via loopStart and loopEnd
 * to simulate instruction reuse or cyclic behavior.
 *
 * @param coord the coordinate of this PE in the grid
 * @param mappings the list of node mappings at each cycle
 * @param routings the list of routing instructions at each cycle
 * @param loopStart the cycle at which looping starts (-1 if no loop)
 * @param loopEnd the cycle at which looping ends (inclusive)
 */
public record PE(Coordinate coord, List<Mapping> mappings, List<Routing> routings, int loopStart, int loopEnd) {
    /**
     * Returns the Routing instruction at the given cycle k, taking into account
     * looping behavior if applicable.
     *
     * If loopStart is -1 or k is before loopEnd, the routing is returned directly.
     * Otherwise, routing is calculated with modular arithmetic over the looping region.
     *
     * @param k the cycle number
     * @return the Routing at cycle k, or null if k exceeds the routing list
     */
    public Routing routingAt(int k) {
        int pc = (loopStart == -1 || k <= loopEnd)
                ? k
                : loopStart + (k - loopStart) % (loopEnd - loopStart + 1);
        return (pc < routings.size()) ? routings.get(pc) : null;
    }

    /**
     * Returns the Mapping at the given cycle k.
     * @param k the cycle number
     * @return the Mapping at cycle k, or null if k exceeds the mapping list or there is no mapping information.
     */
    public Mapping mappingAt(int k) {
        return (k < mappings.size()) ? mappings.get(k) : null;
    }

    //NOTE NOP will return empty string

    /**
     * Returns the operation code for this PE at cycle k, as defined in the Routing
     *
     * If the routing is null or the operation code is NOP (no operation), this method returns an empty string.
     * @param k the cycle number
     * @return the operation code at cycle k, or an empty string if no operation is scheduled
     */
    public String opCodeAt(int k) {
        Routing rt = routingAt(k);
        if (rt == null || rt.getOpCode().equalsIgnoreCase("NOP")) {
            return "";
        }
        return rt.getOpCode();
    }

    /**
     * Returns a label string for this PE at cycle k, combining node index and operation code.
     *
     * If there is no mapping, only the operation code is returned.
     * Otherwise, the format is: "nodeIdx \n opCode"
     * @param k the cycle number
     * @return a label combining mapping and operation code at cycle {@code k}
     */
    public String labelAt(int k) {
        // FIXME include mapping information later
//      Mapping m = mappingAt(k);
        String opCode = opCodeAt(k);
//        if (m == null) {
//            return opCode;
//        }
//        return m.nodeIdx() + "\n" + opCode;
        return opCode;
    }
}