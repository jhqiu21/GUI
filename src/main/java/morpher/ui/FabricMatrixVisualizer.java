package morpher.ui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import morpher.ui.visualization.Coordinate;
import morpher.ui.visualization.FabricMatrix;
import morpher.ui.visualization.GridBuilder;
import morpher.ui.visualization.MappingLoader;
import morpher.ui.visualization.PE;
import morpher.ui.visualization.PELoader;
import morpher.ui.visualization.PortRouting;
import morpher.ui.visualization.Routing;
import morpher.ui.visualization.RoutingLoader;
import morpher.ui.visualization.utils.Direction;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static morpher.ui.visualization.Coordinate.getNeighbourCoordinate;
import static morpher.ui.visualization.utils.Direction.getOppositeDir;

/**
 * A JavaFX component that visualizes a fabric matrix composed of processing elements (PEs).
 *
 * It renders the PE grid using a GridPane, displays their state at each cycle,
 * and draws routing connections dynamically between PEs. This class also supports stepping
 * forward and backward through computation cycles.
 *
 * Use init(FabricMatrix, Map) to load a new matrix, and next(), prev() to navigate the cycles.
 */
public class FabricMatrixVisualizer extends StackPane {
    private final GridPane grid = new GridPane();
    private final GridBuilder gridBuilder = new GridBuilder(grid, this);;
    private FabricMatrix fabric;
    private Map<Coordinate,PE> nodes;
    private int curr;
    private int totalCycle = MappingLoader.get().getNumOfCycle();

    public FabricMatrixVisualizer() {
        getChildren().add(grid);
        setAlignment(Pos.CENTER);
        gridBuilder.buildGrid(5, 5);
        // setPosition();
    }

    public FabricMatrixVisualizer(FabricMatrix fabric, Map<Coordinate,PE> nodes) {
        this();
        init(fabric, nodes);
    }

    /**
     * Initializes the visualizer with a new fabric and PE mapping.
     *
     * @param fabric the fabric layout (rows x cols)
     * @param nodes  the PE node map with per-cycle data
     */
    public void init(FabricMatrix fabric, Map<Coordinate,PE> nodes) {
        System.out.println("init called nodes@" + System.identityHashCode(nodes));

        this.fabric = fabric;
        this.nodes = nodes;
        this.curr = 0;
        grid.getChildren().clear();
        gridBuilder.buildGrid(fabric.rows(), fabric.cols());
        render();
    }

    /**
     * Reloads the visualizer after new routing and mapping data has been loaded
     * from the provided directory.
     *
     * @param viz the visualizer to refresh
     * @param targetDir the directory containing .prog files
     */
    public static void reload(FabricMatrixVisualizer viz, java.nio.file.Path targetDir) {
        RoutingLoader.get().refresh(targetDir);
        PELoader.get().refresh();
        Platform.runLater(() -> {
            FabricMatrix fabric = MappingLoader.get().getFabricMatrix();
            Map<Coordinate, PE> nodes = PELoader.get().getNodes();
            viz.init(fabric, nodes);
        });
    }

    /**
     * Renders the current state of the grid, including labels and routing paths,
     * based on the current cycle.
     */
    private void render() {
        if (fabric == null || nodes == null) {
            return;
        }
        grid.getChildren().removeIf(n -> n instanceof Label || n instanceof Path || n instanceof Line);
        grid.getChildren().forEach(node -> {
            if (node instanceof StackPane sp && sp.getChildren().size() > 1) {
                sp.getChildren().remove(1);
            }
        });

        for (int row = 0; row < fabric.rows(); row++) {
            for (int col = 0; col < fabric.cols(); col++) {
                Coordinate coord = new Coordinate(row, col);
                StackPane cell = gridBuilder.getCell(row, col);

                PE node = nodes.get(coord);
                Label lab = new Label(node.labelAt(curr));
                if (node.coord().row() == 0 && node.coord().col() == 0) {
                    System.out.println(node.coord() + " " + node.labelAt(curr));
                }
                lab.setTextFill(Color.WHITE);
                lab.setFont(Font.font(12));
                cell.getChildren().add(lab);

                drawRoutes(node);
            }
        }

        ScrollPane sp = gridBuilder.findScrollPane(this);
        if (sp != null) {
            javafx.application.Platform.runLater(() -> gridBuilder.hookCentering(sp));
        }
    }

    /**
     * Renders the current state of the grid, including labels and routing paths,
     * based on the current cycle.
     */
    public void next() {
        if (nodes != null && curr < totalCycle) {
            this.curr++;
            render();
        }
    }

    /**
     * Goes back to the previous cycle, if available, and re-renders the grid.
     */
    public void prev() {
        if (nodes != null && curr > 0) {
            this.curr--;
            render();
        }
    }

    public int getCurrentCycle() {
        return curr;
    }

    /**
     * Draws directional routing lines for the given PE at the current cycle.
     *
     * @param pe the processing element to draw routes for
     */
    private void drawRoutes(PE pe) {
        Routing r = pe.routingAt(curr);
        Coordinate currCoord = pe.coord();

        if (r == null) {
            return;
        }
        EnumMap<Direction, PortRouting> ports = r.getPorts();
        for (Direction dir: Direction.values()) {
            PortRouting pr = ports.get(dir);
            if (pr == null) {
                continue;
            }
            Coordinate neighbourCoord = getNeighbourCoordinate(currCoord, dir);
            if (pr.haveInput()) {
                drawDirectLine(neighbourCoord, getOppositeDir(dir), currCoord, dir);
            }
            if (pr.haveOutput()) {
                drawDirectLine(currCoord, dir, neighbourCoord, getOppositeDir(dir));
            }
        }
    }

    /**
     * Draws a direct line between two PE ports with arrowhead, indicating data flow from src to dest.
     *
     * @param src source PE coordinate
     * @param srcDir port side on source PE
     * @param dest destination PE coordinate
     * @param destDir port side on destination PE
     */
    private void drawDirectLine(Coordinate src, Direction srcDir, Coordinate dest, Direction destDir) {
        double[] srcPos = getCoordinate(src, srcDir);
        double[] destPos = getCoordinate(dest, destDir);
        double sx = srcPos[0];
        double sy = srcPos[1];
        double dx = destPos[0];
        double dy = destPos[1];

        Path path = new Path(
                new MoveTo(sx, sy),
                new LineTo(dx, dy)
        );

        path.setStroke(Color.web("#ff9c23"));
        path.setStrokeWidth(2);
        path.setStrokeLineJoin(StrokeLineJoin.MITER);
        addArrowHead(path);
        path.setManaged(false);
        grid.getChildren().add(path);
    }

    /**
     * Get middle point coordinate in specific side for a PE
     * @param coord coordinate of pe
     * @param dir side
     * @return coordinate of middle point.
     */
    public double[] getCoordinate(Coordinate coord, Direction dir) {
        double[] pos = new double[2];
        StackPane cell = gridBuilder.getCell(coord.row(), coord.col());
        Bounds bounds = cell.getBoundsInParent();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double x = bounds.getMinX();
        double y = bounds.getMinY();

        switch (dir) {
        case NORTH:
            pos[0] = x + width / 2;
            pos[1] = y;
            break;
        case SOUTH:
            pos[0] = x + width / 2;
            pos[1] = y + height;
            break;
        case WEST:
            pos[0] = x;
            pos[1] = y + height / 2;
            break;
        case EAST:
            pos[0] = x + width;
            pos[1] = y + height / 2;
            break;
        }
        return pos;
    }

    /**
     * Adds an arrowhead to the end of a given path, pointing in the direction of data flow.
     *
     * @param p the path to modify
     */
    private void addArrowHead(Path p) {
        List<PathElement> els = p.getElements();
        if (els.size() < 2) return;

        PathElement last = els.get(els.size() - 1);
        PathElement prev = els.get(els.size() - 2);

        if (!(last instanceof LineTo end)) return;

        double ex = end.getX(), ey = end.getY();
        double sx, sy;

        if (prev instanceof LineTo pl) {
            sx = pl.getX(); sy = pl.getY();
        } else if (prev instanceof MoveTo pm) {
            sx = pm.getX(); sy = pm.getY();
        } else {
            return;
        }

        double angle = Math.atan2(ey - sy, ex - sx);
        double len   = 8, delta = Math.toRadians(35);

        double ax1 = ex - len * Math.cos(angle - delta);
        double ay1 = ey - len * Math.sin(angle - delta);
        double ax2 = ex - len * Math.cos(angle + delta);
        double ay2 = ey - len * Math.sin(angle + delta);

        els.add(new MoveTo(ex, ey));
        els.add(new LineTo(ax1, ay1));
        els.add(new MoveTo(ex, ey));
        els.add(new LineTo(ax2, ay2));
    }

    /**
     * Attaches a listener to the visualizer's scene property to center the grid in its
     * containing ScrollPane once the scene becomes available.
     *
     * This method ensures that the grid is visually centered within the scroll pane
     * when the component is first added to a scene.
     */
    private void setPosition() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ScrollPane sp = gridBuilder.findScrollPane(this);
                if (sp != null) {
                    gridBuilder.hookCentering(sp);
                }
            }
        });
    }
}


