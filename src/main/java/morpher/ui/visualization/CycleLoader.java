//package morpher.ui.visualization;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.LinkedHashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//import static java.util.stream.Collectors.groupingBy;
//import static java.util.stream.Collectors.toList;
//import static java.util.stream.Collectors.toMap;

//public class CycleLoader {
//    private static CycleLoader instance;
//    private final List<Cycle> cycles;
//
//    private CycleLoader() {
//        this.cycles = loadCycles();
//    }
//
//    public static synchronized CycleLoader get() {
//        if (instance == null) {
//            instance = new CycleLoader();
//        }
//        return instance;
//    }
//
//    public List<Cycle> getCycles() {
//        return cycles;
//    }
//
//    /**
//     * Load all necessary information for each cycle.
//     * @return List of Cycles which contains all necessary info for each cycle
//     */
//    private List<Cycle> loadCycles() {
//        List<MappingBatch> mappingBatches = MappingLoader.get().getMappingBatches();
//        List<RoutingBatch> routingBatches = RoutingLoader.get().getRoutingBatches();
//        // set up index map
//        Map<Integer, Map<Coordinate, List<Mapping>>> mapMap = mappingBatches.stream()
//                .collect(toMap(
//                        MappingBatch::id,
//                        mb -> mb.mappings().stream().collect(groupingBy(Mapping::coordinate, toList())),
//                        (a,b)->a,
//                        LinkedHashMap::new));
//
//        Map<Integer, Map<Coordinate, Routing>> routingMap = routingBatches.stream()
//                .collect(toMap(RoutingBatch::id, RoutingBatch::routings,
//                        (a,b)->a, LinkedHashMap::new));
//
//        int routingLength = routingMap.size();
//
//        // merge mapping and routing list
//        int numOfCycle = MappingLoader.get().getNumOfCycle();
//        List<Cycle> cycleList = new ArrayList<>();
//        for (int cycleId = 0; cycleId <= numOfCycle; cycleId++) {
//            int routingKey = cycleId % routingLength;
//
//            Map<Coordinate, List<Mapping>> ms = mapMap.getOrDefault(cycleId, Collections.emptyMap());
//            Map<Coordinate, Routing> rs = routingMap.getOrDefault(routingKey, Collections.emptyMap());
//            Set<Coordinate> coords = new LinkedHashSet<>();
//            coords.addAll(ms.keySet());
//            coords.addAll(rs.keySet());
//
//            List<PE> peList = new ArrayList<>();
//            for (Coordinate c : coords) {
//                List<Mapping> maps = ms.getOrDefault(c, Collections.emptyList());
//                int index = maps.isEmpty() ? -1 : maps.get(0).nodeIdx();
//                String opCode = MappingLoader.get().getOpFor(index);
//                Routing routing = rs.get(c);
//                peList.add(new PE(c, index, opCode, Optional.ofNullable(routing)));
//            }
//            cycleList.add(new Cycle(cycleId, List.copyOf(peList)));
//        }
//
//        return cycleList.stream().sorted(Comparator.comparingInt(Cycle::id)).toList();
//    }
//
//}
