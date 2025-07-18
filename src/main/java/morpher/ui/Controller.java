package morpher.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import morpher.ui.visualization.MappingLoader;


public class Controller {
    @FXML private ListView<String> pinnedListView;
    @FXML private StackPane codePane;
    @FXML private StackPane dfgPane;
    @FXML private StackPane mappingViz;
    @FXML private HBox cycleNav;
    @FXML private ScrollPane dfgScroll;

    private CodeEditor codeEditor;
    private DFGViewer dfgViewer;
    private CodeCompiler codeCompiler;
    private PinnedApplications pinnedApplications;
    private ModelUploader modelUploader;
    private MappingVisualizer mappingVisualizer;
    private CycleNavigator cycleNavigator;


    @FXML
    public void initialize() {
        codeEditor = new CodeEditor(codePane);
        dfgViewer = new DFGViewer(dfgPane);
        codeCompiler = new CodeCompiler(codeEditor);
        pinnedApplications = new PinnedApplications(pinnedListView);
        modelUploader = new ModelUploader(codeEditor);
        dfgViewer.loadDFG("/docs/gemm_systolic_r.pdf");
        pinnedApplications.loadApplications();

        mappingVisualizer = new MappingVisualizer();
        mappingViz.getChildren().add(mappingVisualizer);
        cycleNavigator = new CycleNavigator();
        cycleNav.getChildren().add(cycleNavigator);

        // load visualizer
        try {
            MappingLoader loader = new MappingLoader();
            mappingVisualizer.init(loader.fabric(), loader.cycles(), loader::opFor);
            cycleNavigator.bind(mappingVisualizer);
        } catch (Exception e) {
            AlertHelper.showError("Visualization Init Error", e.getMessage());
        }

        Platform.runLater(() -> {
            dfgScroll.setHvalue(0.5);
            dfgScroll.setVvalue(0.5);
        });
    }

    @FXML
    private void onShowMorePinned() {
        // TODO
    }

    @FXML
    private void onUploadToDevice() {
        // TODO
    }

    @FXML
    private void onSelectFile(ActionEvent event) {
        modelUploader.upload((Node) event.getSource());
    }

    @FXML
    private void onGenerateAccelerator() {
        codeCompiler.runCCode();
    }

    @FXML
    private void onZoomIn() {
        dfgViewer.zoomIn();
        Platform.runLater(() -> {
            dfgScroll.setHvalue(0.5);
            dfgScroll.setVvalue(0.5);
        });
    }

    @FXML
    private void onZoomOut() {
        dfgViewer.zoomOut();
        Platform.runLater(() -> {
            dfgScroll.setHvalue(0.5);
            dfgScroll.setVvalue(0.5);
        });
    }
}
