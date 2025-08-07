package morpher.ui.visualization;

import java.util.List;

/**
 * Represents a batch of routing instructions over multiple cycles for a single PE.
 *
 * This record includes the list of Routing steps and an optional looping region
 * defined by jumpStart and jumpEnd, which allows periodic behavior.
 *
 * @param routes the list of Routing objects for each cycle
 * @param jumpStart the starting cycle index (inclusive) of the loop; -1 if no loop
 * @param jumpEnd the ending cycle index (inclusive) of the loop
 */
public record RoutingBatch(List<Routing> routes, int jumpStart, int jumpEnd) {}