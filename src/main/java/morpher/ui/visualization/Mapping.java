package morpher.ui.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a mapping from a PE to a node index.
 *
 * @param coordinate the 2D coordinate of PE
 * @param nodeIdx the corresponding node index
 */
public record Mapping(Coordinate coordinate, int nodeIdx) {
    private static final Pattern TUPLE = Pattern.compile("\\((\\d+)\\s*,\\s*(\\d+)\\)\\s*->\\s*(\\d+)");

    /**
     * Parses a line containing coordinate-to-index mappings in the format: (x1, y1) -> idx
     * and returns a list of Mapping objects.
     *
     * @param line the string containing one or more mappings
     * @return a list of parsed Mapping instances from the input line
     */
    public static List<Mapping> parseLine(String line) {
        List<Mapping> res = new ArrayList<>();
        Matcher m = TUPLE.matcher(line);
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            int idx = Integer.parseInt(m.group(3));
            res.add(new Mapping(new Coordinate(x, y), idx));
        }
        return res;
    }
}