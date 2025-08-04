package morpher.ui.visualization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingLoader {
    private static MappingLoader instance;
    private final FabricMatrix fabric;
    private final Map<Coordinate, List<Mapping>> mappingTable;
    private final Map<Integer, String> operationMappings;
    private int numOfCycle;

    public MappingLoader() throws Exception {
        this.fabric = loadDims(); // parse config file to get dimension of matrix
        this.operationMappings = loadOperations(); // get operation mappings from xml file
        this.mappingTable = loadMappingTable();
    }

    /**
     * @return dimension of fabric matrix
     */
    public FabricMatrix getFabricMatrix() {
        return fabric;
    }


    public Map<Coordinate, List<Mapping>> getMappingTable() {
        return mappingTable;
    }

    //FIXME Remove this part if using prog file op. info.
    public Map<Integer, String> getOperationMappings() {
        return operationMappings;
    }

    public int getNumOfCycle() {
        return numOfCycle;
    }

    public static MappingLoader get() {
        if (instance == null) {
            try {
                instance = new MappingLoader();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    //FIXME Remove this part if using prog file op. info.
    /**
     * Return operation for a specific node.
     * @param index of node.
     * @return Operation of node.
     */
    public String getOpFor(int index)  {
        if (index == -1) {
            return "NAN";
        }
        return operationMappings.getOrDefault(index, "NAN");
    }

    /**
     * Load the dimension of fabric matrix from configuration file.
     * @return Fabric matrix object represents dimension.
     * @throws IOException
     */
    private FabricMatrix loadDims() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream input = getClass().getResourceAsStream("/docs/hycube_original_updatemem4x4.json")) {
            JsonNode root = mapper.readTree(input);
            JsonNode dims = root.at("/CGRA/SUBMODS/0/DIMS");
            return new FabricMatrix(dims.get("X").asInt(), dims.get("Y").asInt());
        }
    }


    //FIXME Remove this part if using prog file op. info.
    /**
     * Load operations for each PE from xml config file
     * @return Map of PE index and operation.
     * @throws Exception
     */
    private Map<Integer,String> loadOperations() throws Exception {
        Map<Integer,String> map = new HashMap<>();
        try (InputStream is = getClass().getResourceAsStream("/docs/gemm_systolic_1.xml")) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList nodes = doc.getElementsByTagName("Node");
            for (int i = 0; i < nodes.getLength(); i++) {
                org.w3c.dom.Element elem = (org.w3c.dom.Element) nodes.item(i);
                int idx = Integer.parseInt(elem.getAttribute("idx"));
                String op = elem.getElementsByTagName("OP").item(0).getTextContent().trim();
                map.put(idx, op);
            }
        }
        return map;
    }

    /**
     * Load Mapping information for each cycle.
     * @return a list of mapping information for each cycle, sorted in ascending order of cycle index.
     * @throws IOException
     */
    private Map<Coordinate, List<Mapping>> loadMappingTable() throws IOException {
        Map<Integer, List<Mapping>> tmp = new HashMap<>();
        try (InputStream src = getClass().getResourceAsStream("/docs/mapping.txt");
             BufferedReader buffer = new BufferedReader(new InputStreamReader(src, StandardCharsets.UTF_8))) {
            String line;
            int curr = -1;
            while ((line = buffer.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("cycle")) {
                    String num = line.replace("cycle", "").replace(":", "").trim();
                    curr = Integer.parseInt(num);
                    continue;
                }

                if (curr >= 0) {
                    tmp.computeIfAbsent(curr, k -> new ArrayList<>()).addAll(Mapping.parseLine(line));
                }
            }
            this.numOfCycle = curr;
        }

        Map<Coordinate, List<Mapping>> mapTable = new HashMap<>();

        int listSize = numOfCycle + 1;
        for (var entry : tmp.entrySet()) {
            int cycleId = entry.getKey();
            for (Mapping m : entry.getValue()) {
                mapTable.computeIfAbsent(m.coordinate(), k -> initList(listSize)).set(cycleId, m);
            }
        }
        return mapTable;
    }

    private static <T> List<T> initList(int size) {
        return new ArrayList<>(Collections.nCopies(size, null));
    }
}
