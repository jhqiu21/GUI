package morpher.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DFGViewer {
    @FXML
    private StackPane container;
    private ImageView imageView;
    private double zoomFactor = 1.0;
    public DFGViewer(StackPane container) {
        this.container = container;
    }

    public void loadDFG(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("PDF resource not found: " + resourcePath);
                return;
            }
            File tempPdf = File.createTempFile("dfg", ".pdf");
            tempPdf.deleteOnExit();
            try (FileOutputStream os = new FileOutputStream(tempPdf)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            try (PDDocument document = PDDocument.load(tempPdf)) {
                PDFRenderer renderer = new PDFRenderer(document);
                BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 150);
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView = new ImageView(fxImage);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(600);
                container.getChildren().add(imageView);
            }
        } catch (Exception e) {
            AlertHelper.showError("PDF Load Error", e.getMessage());
        }
    }

    private void applyZoom() {
        if (imageView != null) {
            imageView.setScaleX(zoomFactor);
            imageView.setScaleY(zoomFactor);
        }
    }

    public void zoomIn() {
        zoomFactor *= 1.2;
        applyZoom();
    }

    public void zoomOut() {
        zoomFactor *= 0.8;
        applyZoom();
    }

}
