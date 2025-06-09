package morpher.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class CycleNavigator extends HBox {
    private MappingVisualizer viz;
    public CycleNavigator() {
        Button left = new Button("Prev");
        Button right = new Button("Next");
        left.setOnAction(e -> viz.prev());
        right.setOnAction(e -> viz.next());
        setSpacing(10);
        setAlignment(Pos.CENTER);
        getChildren().addAll(left, right);
    }

    public void bind(MappingVisualizer viz) {
        this.viz = viz;
    }
}