package morpher.ui;

import javafx.scene.control.ListView;

public class PinnedApplications {
    private ListView<String> appList;
    public PinnedApplications(ListView<String> appList) {
        this.appList = appList;
    }

    public void loadApplications() {
        appList.getItems().addAll(
                "1. Fast Fourier Transform - 42.05 KB",
                "2. AES Encryption - 13.69 KB",
                "3. Mobilenet v2 - 1.62 MB",
                "4. Resnet50 - 21.93 MB",
                "5. YOLO v8 - 3.05 MB"
        );
    }
}
