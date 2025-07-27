package morpher.ui.visualization;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static morpher.ui.visualization.Routing.parseRoutingLine;

public class RoutingLoader {
    private static final Logger LOGGER = Logger.getLogger(RoutingLoader.class.getName());
    private static final Pattern FILE_NAME = Pattern.compile("PE-Y(\\d+)X(\\d+)\\.prog");
    private static final Pattern JUMP_PATTERN = Pattern.compile(
            "^\\s*(?:inst|operation):\\s*jump\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*]\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern OPCODE_PAT =
            Pattern.compile("^\\s*(?:inst|operation)\\s*:\\s*([A-Z]+)",
                    Pattern.CASE_INSENSITIVE);
    private final Map<Coordinate, RoutingBatch> routingBatches;
    private static RoutingLoader instance;

    private RoutingLoader() {
        this.routingBatches = loadRouting();
    }

    public static RoutingLoader get() {
        if (instance == null) {
            try {
                instance = new RoutingLoader();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public Map<Coordinate, RoutingBatch> getRoutingBatches() {
        return routingBatches;
    }

    private Map<Coordinate, RoutingBatch> loadRouting() {
        Map<Coordinate, RoutingBatch> routingMap = new LinkedHashMap<>();
        try {
            URI uri = Objects.requireNonNull(getClass().getResource("/docs/single_sided_array_add_4x4/")).toURI();
            Path docsDir = Paths.get(uri);
            try (Stream<Path> files = Files.list(docsDir)) {
                files.filter(p -> p.getFileName().toString().matches("PE-Y\\d+X\\d+\\.prog"))
                        .forEach(path -> parseRoutingFile(path, routingMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load .prog files", e);
        }
        return routingMap;
    }

    private void parseRoutingFile(Path path, Map<Coordinate,RoutingBatch> routingBatchMap) {
        String fileName = path.getFileName().toString();
        Matcher fm = FILE_NAME.matcher(fileName);
        // file do not match the pattern
        if (!fm.matches()) {
            return;
        }
        // get PE from file name
        int row = Integer.parseInt(fm.group(1));
        int col = Integer.parseInt(fm.group(2));
        Coordinate coord = new Coordinate(row, col);
        System.out.println(coord);
        List<Routing> routeingList = new ArrayList<>();
        int cycle = 0;
        int jumpStart = -1;
        int jumpEnd = -1;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            boolean inSwitch = false;
            Routing routes = null;
            String opCode = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.toLowerCase().startsWith("operation:")) {
                    Matcher m = OPCODE_PAT.matcher(line);
                    opCode = m.find() ? m.group(1).toUpperCase() : "";
                    System.out.println(opCode + "-C" + cycle);

                    if (opCode.equalsIgnoreCase("JUMP")) {
                        Matcher jm = JUMP_PATTERN.matcher(line);
                        if (jm.find()) {
                            jumpStart = Integer.parseInt(jm.group(1));
                            jumpEnd   = Integer.parseInt(jm.group(2));
                        }
                    }
                    continue;
                }

                if (line.toLowerCase().startsWith("switch_config")) {
                    inSwitch = true;
                    routes = new Routing();
                    routes.setOpCode(opCode);
                    continue;
                }


                if (inSwitch && line.startsWith("}")) {

                    // routeingList.set(cycle, routes.isEmpty() ? null : routes);
                    routeingList.add(routes);
                    cycle++;
                    inSwitch = false;
                    continue;
                }

                if (inSwitch) {
                    parseRoutingLine(line, routes);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Cannot parse {0}: {1}", new Object[]{fileName, ex.getMessage()});
        }

        RoutingBatch routingBatch = new RoutingBatch(
                Collections.unmodifiableList(new ArrayList<>(routeingList)),
                jumpStart, jumpEnd);
        routingBatchMap.put(coord, routingBatch);
    }
}
