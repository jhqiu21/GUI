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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingLoader {
    private static MappingLoader instance;
    private static final Pattern TUPLE = Pattern.compile("\\((\\d+)\\s*,\\s*(\\d+)\\)\\s*->\\s*(\\d+)");

    // save dimension of the matrix
    private final FabricMatrix fabric;
    // store each cycle
    private final List<Cycle> cycles;
    private final Map<Integer, String> operationMappings;

    public MappingLoader() throws Exception {
        this.fabric = loadDims(); // parse config file to get dimension of matrix
        this.operationMappings = loadOperations(); // get operation mappings from xml file
        this.cycles = loadCycles(); // load the number of cycles and returns a list
    }

    public FabricMatrix fabric() {
        return fabric;
    }
    public List<Cycle> cycles() {
        return cycles;
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

    private FabricMatrix loadDims() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream input = getClass().getResourceAsStream("/docs/hycube_original_updatemem4x4.json")) {
            JsonNode root = mapper.readTree(input);
            JsonNode dims = root.at("/CGRA/SUBMODS/0/DIMS");
            System.out.println("Dims OK!");
            return new FabricMatrix(dims.get("X").asInt(), dims.get("Y").asInt());
        }
    }

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
        System.out.println("XML OK! Size: " + map.size());

        return map;
    }

    private List<Cycle> loadCycles() throws IOException {
        Map<Integer,List<Mapping>> tmp = new HashMap<>();
        try (InputStream src = getClass().getResourceAsStream("/docs/mapping.txt");
             BufferedReader buffer = new BufferedReader(new InputStreamReader(src, StandardCharsets.UTF_8))) {
            String line;
            int curr = 0;
            while ((line = buffer.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("cycle")) {
                    String num = line.replace("cycle", "").replace(":", "").trim();
                    curr = Integer.parseInt(num);
                    continue;
                }

                if (curr >= 0) {
                    Matcher m = TUPLE.matcher(line);
                    while (m.find()) {
                        int x = Integer.parseInt(m.group(1));
                        int y = Integer.parseInt(m.group(2));
                        int idx = Integer.parseInt(m.group(3));

                        tmp.computeIfAbsent(curr, k -> new ArrayList<>()).add(new Mapping(x, y, idx));
                    }
                }
            }
        }
        List<Cycle> list = new ArrayList<>();
        tmp.keySet().stream().sorted().forEach(c -> list.add(new Cycle(c, tmp.get(c))));
        System.out.println("Cycles OK! Size: " + list.size());
        return list;
    }

    public String opFor(int idx)  {
        return operationMappings.getOrDefault(idx, "?");
    }

}
