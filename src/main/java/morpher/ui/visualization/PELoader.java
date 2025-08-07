package morpher.ui.visualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Singleton class responsible for loading and managing {@link PE} instances,
 * each associated with a {@link Coordinate} in the processing grid.
 *
 * The loader integrates mapping data from {@link MappingLoader} and routing data
 * from {@link RoutingLoader}, and constructs a complete set of PEs with time-based
 * behavior.
 */
public class PELoader {
    private static PELoader instance;
    private Map<Coordinate, PE> nodes;

    private PELoader() {
        this.nodes = loadPEs();
    }

    /**
     * Returns the singleton instance of PELoader, initializing it on first access.
     *
     * @return the singleton PELoader instance
     */
    public static synchronized PELoader get() {
        if (instance == null) {
            instance = new PELoader();
        }
        return instance;
    }

    /**
     * Returns the current map of Coordinate to PE instances.
     *
     * @return a map of coordinates to their corresponding processing elements
     */
    public Map<Coordinate, PE> getNodes() {
        return nodes;
    }

    /**
     * Reloads the processing element data by re-reading the mapping and routing sources.
     * Useful if the underlying configuration or input files have changed.
     */
    public void refresh() {
        this.nodes = loadPEs();
    }

    /**
     * Loads and constructs all PE instances by combining data from MappingLoader and RoutingLoader.
     *
     * Ensures all coordinates with either mapping or routing data are included,
     * and initializes missing data with null placeholders.
     *
     * @return a map of coordinates to fully constructed PE instances
     */
    private Map<Coordinate, PE> loadPEs() {
        Map<Coordinate, List<Mapping>> mapTable = MappingLoader.get().getMappingTable();
        int totalCycles = MappingLoader.get().getNumOfCycle();
        Map<Coordinate, RoutingBatch> routingMap = RoutingLoader.get().getRoutingBatches();

        Set<Coordinate> coords = new LinkedHashSet<>();
        coords.addAll(mapTable.keySet());
        coords.addAll(routingMap.keySet());

        Map<Coordinate, PE> nodeMap = new LinkedHashMap<>();
        for (Coordinate c : coords) {
            List<Mapping> mappingList = new ArrayList<>(mapTable.getOrDefault(c, initList(totalCycles)));
            RoutingBatch routingBatch = routingMap.get(c);
            List<Routing> routingList = initList(totalCycles);
            int jumpStart = -1;
            int jumpEnd = -1;

            if (routingBatch != null) {
                routingList = new ArrayList<>(routingBatch.routes());
                jumpStart = routingBatch.jumpStart();
                jumpEnd   = routingBatch.jumpEnd();
            }
            if (c.row() == 0 && c.col() == 0) {
                System.out.println(mappingList.toString() + " " + routingList.toString());
            }
            nodeMap.put(c, new PE(c, mappingList, routingList, jumpStart, jumpEnd));
        }
        return nodeMap;
    }

    /**
     * Creates a new List of the specified size, filled with null values to initialize empty
     * or partially filled mapping and routing lists.
     *
     * @param size the number of elements in the list
     * @return a list of null elements with the given size
     */
    private static <T> List<T> initList(int size) {
        return new ArrayList<>(Collections.nCopies(size, null));
    }
}
