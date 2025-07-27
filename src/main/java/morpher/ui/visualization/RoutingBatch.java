package morpher.ui.visualization;

import java.util.List;
import java.util.Map;

public record RoutingBatch(List<Routing> routes, int jumpStart, int jumpEnd) {}