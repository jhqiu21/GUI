# Morpher GUI

![morpher.png](docs/img/morpher.png)

## Overview
Morpher GUI provides a simple, visual workspace for:
- uploading a source file,
- viewing & editing the code,
- compiling/running script (“Generate Accelerator”),
- inspecting a CGRA fabric mapping cycle-by-cycle,
- and browsing a DFG(Date Flow Graph) PDF file.

## System requirements
- OS: macOS, Windows, or Linux
- Java: JDK 17+ (we recommend 17 LTS).
- JavaFX: bundled via Gradle; no manual install needed when you run from Gradle or the fat JAR.
- C toolchain (optional, for Generate Accelerator): gcc available on your PATH.
- Memory: 512 MB+ free RAM.


- Load project dependencies. Either let your IDE run the first Gradle sync automatically, or execute 
    ```
    ./gradlew build
    ```
  from the project root.


## Getting Started
Follow these steps to build and run the Morpher GUI on your local machine.

### Prepare your environment
- Install JDK 17(the [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) version or another alternative such as the OpenJDK version), set your IDE/project SDK to JDK 17, and confirm with `java -version`.
- Use the Gradle Wrapper included in the project (`./gradlew` / `gradlew.bat`).
- If you plan to compile C code, ensure `gcc` is installed and on your PATH.

### Clone the project
   ```
   git clone https://github.com/jhqiu21/MorpherGUI.git
   cd MorpherGUI
   ```
### Open in your IDE
   - Open the project and [configure your IDE to use the JDK 17](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk) 
   - Let the IDE import it as a Gradle project if your IDE has not already detected it. You may refer to this [guide](https://se-education.org/guides/tutorials/gradle.html#adding-gradle-to-the-project).
   - Load project dependencies. Either let your IDE run the first Gradle sync automatically, or execute
     ```
     ./gradlew build
     ```
     from the project root.

### Sync dependencies & build & Run
   - Run the app using `./gradlew run` to launche `morpher.ui.Main` and opens the GUI.
   - (Optional) Create a distributable JAR using `./gradlew shadowJar`
   - You may also run the application by executing the `morpher.ui.Main` to launch the GUI for testing during development.

Sample resources (DFG PDF, mapping, JSON DIMS, XML) are bundled under resources/docs so you can try the app immediately.

## User Guide

You may find more details in our [user guide](docs/User%20Guide.md).

## FAQ
1. "JavaFX runtime components are missing, and are required to run this application"

    **Cause:** JavaFX 17 is not on the module path at launch time.
    
    **Fix:**
    - You may download [JavaFX 17.0.15 JDK](https://gluonhq.com/products/javafx/)
    - Add the following VM options (adapt the path to your installation):
        ```
        --module-path "\path\to\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing
        ```
    You may refer to [this video](https://www.youtube.com/watch?v=hS_6ek9rTco) for the process step-by-step.