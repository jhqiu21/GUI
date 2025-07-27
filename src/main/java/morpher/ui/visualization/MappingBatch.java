package morpher.ui.visualization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping information in a cycle.
 * @param id cycle index.
 * @param mappings list of Mapping information for this cycle.
 */
public record MappingBatch(int id, List<Mapping> mappings){}
