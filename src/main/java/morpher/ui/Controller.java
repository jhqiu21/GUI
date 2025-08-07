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

/**
 * The main controller for the Morpher JavaFX application.
 *
 * This class wires together the UI components (defined in MainWindow.fxml),
 * initializes core modules such as the code editor, visualizer, and DFG viewer,
 * and handles user actions such as file uploads, code compilation, and zooming.
 */
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


    /**
     * Initializes all UI components and modules after the FXML has been loaded.
     *
     * This method sets up the code editor, visualizer, pinned applications, and
     * loads the default data flow graph (DFG). It also centers the DFG view.
     */
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
        // TODO: Pinned Application "more" button
    }

    @FXML
    private void onUploadToDevice() {
        // TODO: upload to device function
    }

    /**
     * Handles the action of selecting a source file from the file system.
     *
     * @param event the action event triggered by the UI
     */
    @FXML
    private void onSelectFile(ActionEvent event) {
        modelUploader.upload((Node) event.getSource());
    }

    /**
     * Compiles and runs the uploaded source code (C or Python) and updates the visualization.
     */
    @FXML
    private void onGenerateAccelerator() {
        codeCompiler.runSourceCode();
    }

    /**
     * Zooms in on the DFG view.
     */
    @FXML
    private void onZoomIn() {
        dfgViewer.zoomIn();
        Platform.runLater(() -> {
            dfgScroll.setHvalue(0.5);
            dfgScroll.setVvalue(0.5);
        });
    }

    /**
     * Zooms out on the DFG view.
     */
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
