package morpher.ui.visualization;

import morpher.ui.visualization.utils.Direction;
import morpher.ui.visualization.utils.RoutingType;

import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static morpher.ui.visualization.utils.Direction.getDirection;

/**
 * Represents the routing configuration of a Processing Element (PE) for a specific cycle.
 *
 * A Routing includes an opCode representing the instruction executed,
 * and a mapping of directional ports to PortRouting, which describes the input and output connections.
 */
public class Routing {
    private static final Pattern PORT_LINE = Pattern.compile("(\\w+)\\s*->\\s*(\\w+)");
    private EnumMap<Direction, PortRouting> ports;
    private String opCode;

    public Routing() {
        this.ports = new EnumMap<Direction, PortRouting>(Direction.class);
        this.opCode = "";
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getOpCode() {
        return opCode;
    }

    public EnumMap<Direction, PortRouting> getPorts() {
        return ports;
    }

    public void setInput(Direction d, RoutingType type) {
        PortRouting pr = ports.getOrDefault(d, PortRouting.EMPTY);
        ports.put(d, pr.withIn(type));
    }

    public void setOutput(Direction d, RoutingType type) {
        PortRouting pr = ports.getOrDefault(d, PortRouting.EMPTY);
        ports.put(d, pr.withOut(type));
    }

    public static void parseRoutingLine(String line, Routing routes) {
        Matcher rm = PORT_LINE.matcher(line);
        while (rm.find()) {
            String src  = rm.group(1);
            String dst = rm.group(2).toLowerCase();
            if (src.equalsIgnoreCase("open")) {
                continue;
            }
            // set input if input is in XXXIn format
            if (src.endsWith("In")) {
                String dir = src.substring(0, src.length() - 2).toLowerCase();
                Direction inputDir = getDirection(dir);
                RoutingType rt = RoutingType.get(src);
                routes.setInput(inputDir, rt);
            }
            // set output
            if (dst.endsWith("_out")) {
                String dir = dst.substring(0, dst.length() - 4).toLowerCase();
                Direction outputDir = getDirection(dir);
                RoutingType rt = RoutingType.get(dst);
                routes.setOutput(outputDir, rt);
            }
        }
    }
}
