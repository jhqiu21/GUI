package morpher.ui.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Mapping(Coordinate coordinate, int nodeIdx) {
    private static final Pattern TUPLE = Pattern.compile("\\((\\d+)\\s*,\\s*(\\d+)\\)\\s*->\\s*(\\d+)");

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