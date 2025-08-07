package morpher.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A simple UI component for navigating through computation cycles in a fabric matrix.
 *
 * Displays the current cycle and provides "Prev" and "Next" buttons to step through cycles.
 * It is typically bound to a FabricMatrixVisualizer, which handles the underlying logic.
 */
public class CycleNavigator extends HBox {

    private final Button btnPrev = new Button("Prev");
    private final Button btnNext = new Button("Next");
    private final Label  lblCycle = new Label("Cycle 0");

    private FabricMatrixVisualizer mv;

    public CycleNavigator() {
        HBox.setHgrow(lblCycle, Priority.ALWAYS);
        lblCycle.setMaxWidth(Double.MAX_VALUE);
        lblCycle.setAlignment(Pos.CENTER);
        lblCycle.setStyle("-fx-text-fill: white;");
        setAlignment(Pos.CENTER);
        setSpacing(10);
        getChildren().addAll(btnPrev, lblCycle, btnNext);

        btnPrev.setOnAction(e -> { if (mv != null) { mv.prev();  update(); } });
        btnNext.setOnAction(e -> { if (mv != null) { mv.next();  update(); } });
    }

    public void bind(FabricMatrixVisualizer mv) {
        this.mv = mv;
        update();
    }

    private void update() {
        if (mv != null) {
            lblCycle.setText("Cycle " + mv.getCurrentCycle());
        }
    }
}