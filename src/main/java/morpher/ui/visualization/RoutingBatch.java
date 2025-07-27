package morpher.ui.visualization;

import java.util.List;

public record RoutingBatch(List<Routing> routes, int jumpStart, int jumpEnd) {}