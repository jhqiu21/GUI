package morpher.ui.visualization;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Build and visualize the Fabric Matrix
 */
public class GridBuilder {
    private final GridPane grid;
    private final StackPane wrapper;
    private static final int CELL_SIZE = 60;

    public GridBuilder(GridPane grid, StackPane wrapper) {
        this.grid = grid;
        this.wrapper = wrapper;
    }

    public void buildGrid(int rows, int cols) {
        grid.setHgap(40);
        grid.setVgap(40);
        grid.add(blankCell(), 0, 0);

        for (int col = 0; col < cols; col++) {
            grid.add(headerLabel(String.valueOf(col)), col + 1, 0);
        }

        for (int row = 0; row < rows; row++) {
            grid.add(headerLabel(String.valueOf(row)), 0, row + 1);
            for (int col = 0; col < cols; col++) {
                grid.add(createCell(), col + 1, row + 1);
            }
        }
    }

    /**
     * Creates a single styled grid cell.
     * @return a StackPane representing one grid cell with a styled background
     */
    private StackPane createCell() {
        StackPane cell = new StackPane();
        Rectangle bg = new Rectangle(CELL_SIZE, CELL_SIZE);
        bg.getStyleClass().add("grid-cell");
        cell.getChildren().add(bg);
        return cell;
    }

    /**
     * Creates a blank StackPane to serve as a placeholder cell
     * at the intersection of the X and Y axes.
     * @return a styled StackPane representing an empty placeholder cell.
     */
    private StackPane blankCell() {
        StackPane s = new StackPane();
        s.getStyleClass().add("grid-blank");
        return s;
    }

    /**
     * Creates a styled header cell containing the given text, typically used
     * as a label for the horizontal or vertical axis in a grid or chart.
     *
     * @param txt the text to display in the header label
     * @return a StackPane representing the styled header cell
     */
    private StackPane headerLabel(String txt) {
        StackPane s = new StackPane();
        s.getStyleClass().add("grid-header");
        Label l = new Label(txt);
        l.getStyleClass().add("grid-header");
        s.getChildren().add(l);
        return s;
    }


    public StackPane getCell(int row, int col) {
        int uiRow = row + 1;
        int uiCol = col + 1;
        for (javafx.scene.Node n : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(n);
            Integer c = GridPane.getColumnIndex(n);
            if (r != null && c != null && r == uiRow && c == uiCol) {
                return (StackPane) n;
            }
        }
        throw new IllegalArgumentException("Cell not found: (" + row + "," + col + ")");
    }

    /**
     * Traverses up the scene graph from the given Node to find the nearest
     * ancestor that is an instance of ScrollPane.
     * @param n the starting Node to begin the search from
     * @return the closest ancestor
     */
    public ScrollPane findScrollPane(javafx.scene.Node n) {
        while (n != null && !(n instanceof ScrollPane)) {
            n = n.getParent();
        }
        return (ScrollPane) n;
    }

    /**
     * Adjusts the padding of the wrapper so that the content in the ScrollPane is centered
     * within the viewport whenever the viewport size or grid layout bounds change.
     * @param sp whose content should be centered
     */
    public void hookCentering(ScrollPane sp) {
        sp.setFitToWidth(false);
        sp.setFitToHeight(false);

        Runnable recompute = () -> {
            Bounds vp = sp.getViewportBounds();
            if (vp == null) return;

            double gw = grid.getLayoutBounds().getWidth();
            double gh = grid.getLayoutBounds().getHeight();

            double padX = Math.max(0, (vp.getWidth()  - gw) / 2);
            double padY = Math.max(0, (vp.getHeight() - gh) / 2);

            wrapper.setPadding(new Insets(padY, padX, padY, padX));
        };
        sp.viewportBoundsProperty().addListener((o, ov, nv) -> recompute.run());
        grid.layoutBoundsProperty().addListener((o, ov, nv)   -> recompute.run());

        recompute.run();
    }
}
