package morpher.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import morpher.ui.visualization.MappingLoader;
import morpher.ui.visualization.PELoader;


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
    private FabricMatrixVisualizer fabricMatrixVisualizer;
    private CycleNavigator cycleNavigator;


    @FXML
    public void initialize() {
        codeEditor = new CodeEditor(codePane);
        codeCompiler = new CodeCompiler(codeEditor, this);
        modelUploader = new ModelUploader(codeEditor);

        dfgViewer = new DFGViewer(dfgPane);
        dfgViewer.loadDFG();

        pinnedApplications = new PinnedApplications(pinnedListView);
        pinnedApplications.loadApplications();

        fabricMatrixVisualizer = new FabricMatrixVisualizer();
        mappingViz.getChildren().add(fabricMatrixVisualizer);
        cycleNavigator = new CycleNavigator();
        cycleNav.getChildren().add(cycleNavigator);

        // load visualizer
        try {
            MappingLoader mLoader = new MappingLoader();
            PELoader peLoader = PELoader.get();
            fabricMatrixVisualizer.init(mLoader.getFabricMatrix(), peLoader.getNodes());
            cycleNavigator.bind(fabricMatrixVisualizer);
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
        codeCompiler.runSourceCode();
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

    public FabricMatrixVisualizer getFabricMatrixVisualizer() {
        return this.fabricMatrixVisualizer;
    }
}
