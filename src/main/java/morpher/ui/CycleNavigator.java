package morpher.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CycleNavigator extends HBox {

    private final Button btnPrev = new Button("Prev");
    private final Button btnNext = new Button("Next");
    private final Label  lblCycle = new Label("Cycle 0");

    private MappingVisualizer mv;

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

    public void bind(MappingVisualizer mv) {
        this.mv = mv;
        update();
    }

    private void update() {
        if (mv != null) {
            lblCycle.setText("Cycle " + mv.getCurrentCycle());
        }
    }
}