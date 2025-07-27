package morpher.ui.visualization;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLoader {
    public static List<Connection> load(String xmlResource) throws Exception {
        List<Connection> list = new ArrayList<>();
        try (InputStream input = ConnectionLoader.class.getResourceAsStream(xmlResource)) {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(input);
            NodeList nodes = doc.getElementsByTagName("PE");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element n = (Element) nodes.item(i);
                int fromIdx = Integer.parseInt(n.getAttribute("idx"));

                NodeList outs = ((Element) n.getElementsByTagName("Outputs").item(0))
                        .getElementsByTagName("Output");
                for (int j = 0; j < outs.getLength(); j++) {
                    int toIdx = Integer.parseInt(((Element) outs.item(j))
                            .getAttribute("idx"));
                    list.add(new Connection(fromIdx, toIdx));
                }
            }
        }
        return list;
    }
}
