package morpher.ui.visualization;

import morpher.ui.visualization.utils.Direction;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public record PE(Coordinate coord, List<Mapping> mappings, List<Routing> routings, int loopStart, int loopEnd) {
    /**
     * Get routing information of pe at cycle k
     * @param k cycle
     * @return routing information.
     */
    public Routing routingAt(int k) {
        int pc = (loopStart == -1 || k <= loopEnd)
                ? k
                : loopStart + (k - loopStart) % (loopEnd - loopStart + 1);
        return (pc < routings.size()) ? routings.get(pc) : null;
    }

    /**
     * Get mapping information of pe at cycle k
     * @param k cycle
     * @return mapping information
     */
    public Mapping mappingAt(int k) {
        return (k < mappings.size()) ? mappings.get(k) : null;
    }

    public String opCodeAt(int k) {
        Routing rt = routingAt(k);
        if (rt == null || rt.getOpCode().equalsIgnoreCase("NOP")) {
            return "";
        }
        return rt.getOpCode();
    }

    public String labelAt(int k) {
        Mapping m = mappingAt(k);
        String opCode = opCodeAt(k);
        if (m == null) {
            return opCode;
        }
        return m.nodeIdx() + "\n" + opCode;
    }
}