package morpher.ui.visualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PELoader {
    private static PELoader instance;
    private final Map<Coordinate, PE> nodes;

    private PELoader() {
        this.nodes = loadPEs();
    }

    public Map<Coordinate, PE> getNodes() {
        return nodes;
    }

    public static synchronized PELoader get() {
        if (instance == null) {
            instance = new PELoader();
        }
        return instance;
    }

    private Map<Coordinate, PE> loadPEs() {
        Map<Coordinate, List<Mapping>> mapTable = MappingLoader.get().getMappingTable();
        int totalCycles = MappingLoader.get().getNumOfCycle();
        Map<Coordinate, RoutingBatch> routingMap = RoutingLoader.get().getRoutingBatches();

        Set<Coordinate> coords = new LinkedHashSet<>();
        coords.addAll(mapTable.keySet());
        coords.addAll(routingMap.keySet());

        Map<Coordinate, PE> nodeMap = new LinkedHashMap<>();
        for (Coordinate c : coords) {
            List<Mapping> mappingList = mapTable.getOrDefault(c, initList(totalCycles));
            RoutingBatch routingBatch = routingMap.get(c);
            List<Routing> routingList = initList(totalCycles);
            int jumpStart = -1;
            int jumpEnd = -1;

            if (routingBatch != null) {
                routingList = routingBatch.routes();
                jumpStart = routingBatch.jumpStart();
                jumpEnd   = routingBatch.jumpEnd();
            }

            nodeMap.put(c, new PE(c, mappingList, routingList, jumpStart, jumpEnd));
        }
        return nodeMap;
    }

    private static <T> List<T> initList(int size) {
        return new ArrayList<>(Collections.nCopies(size, null));
    }
}
