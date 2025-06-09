package morpher.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import morpher.ui.visualization.Cycle;
import morpher.ui.visualization.FabricMatrix;
import morpher.ui.visualization.GridBuilder;
import morpher.ui.visualization.Mapping;

import java.util.List;

public class MappingVisualizer extends StackPane {
    private final GridPane grid = new GridPane();
    private final StackPane wrapper = new StackPane();
    private FabricMatrix fabric;
    private List<Cycle> cycles;
    private OpResolver opResolver;
    private GridBuilder gridBuilder = new GridBuilder(grid, wrapper);

    private int idx = 0;

    @FunctionalInterface
    public interface OpResolver {
        String opOf(int nodeIdx);
    }

    public MappingVisualizer() {
        wrapper.getChildren().add(grid);
        wrapper.setAlignment(Pos.CENTER);
        getChildren().add(wrapper);
        setAlignment(Pos.CENTER);
        gridBuilder.buildGrid(1, 1);
        setPosition();
    }

    private void setPosition() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ScrollPane sp = gridBuilder.findScrollPane(this);
                if (sp != null) {
                    System.out.println(sp);
                    gridBuilder.hookCentering(sp);
                }
            }
        });
    }

    public MappingVisualizer(FabricMatrix fabric, List<Cycle> cycles, OpResolver resolver) {
        this();
        init(fabric, cycles, resolver);
    }

    public void init(FabricMatrix fabric, List<Cycle> cycles, OpResolver resolver) {
        this.fabric = fabric;
        this.cycles = cycles;
        this.opResolver = resolver;

        grid.getChildren().clear();
        gridBuilder.buildGrid(fabric.rows(), fabric.cols());

        idx = 0;
        render();
    }

    private void render() {
        if (fabric == null || cycles == null) {
            return;
        }

        grid.getChildren().forEach(node -> {
            if (node instanceof StackPane sp && sp.getChildren().size() > 1) {
                sp.getChildren().remove(1);
            }
        });

        Cycle cycle = cycles.get(idx);
        for (Mapping m : cycle.mappings()) {
            StackPane cell = gridBuilder.getCell(m.row(), m.col());
            String text = m.nodeIdx() + "\n" + opResolver.opOf(m.nodeIdx());
            Label lab = new Label(text);
            lab.setFont(Font.font(12));
            cell.getChildren().add(lab);
        }
    }

    public void next() {
        if (cycles != null && idx < cycles.size() - 1) {
            idx++;
            render();
        }
    }

    public void prev() {
        if (cycles != null && idx > 0) {
            idx--;
            render();
        }
    }
}

