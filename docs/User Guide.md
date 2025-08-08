# User Guide

## The interface at a glance

![Morpher](img/morpher.png)

**Left column**

- **Pinned Applications:** sample entries (_no operation yet_).
- **Upload AI Models:** click Select File to choose a .c file. It will load into the Source Code tab.

**Center area (tabs)**

- **Visualization:** the CGRA fabric matrix grid with headers.
Use Prev / Next to step through cycles.
  - Cells show nodeIdx and the operation (e.g., ADD, LOAD, STORE) for that cycle only. 
  - Orange routing lines & arrows show outputs from one node to another. Lines snap to gaps between cells. 
  - Large fabrics are scrollable; content stays centered in the viewport.

- **Source Code:** a code editor with the currently loaded C/Python script.

- **DFG:** renders a PDF of the DFG. Use Zoom In / Zoom Out to navigate.

**Right column**

- **Generate Accelerator:** compiles & runs the current code.
- **Upload to Device:** placeholder for further development (no operation yet).
- **Metrics:** fixed demo bars to indicate utilization, battery and performance (no operation yet).

## Typical workflow

1. **Load your code**
   - In Upload AI Models (left), click Select File and choose a `.c`/`.python` file.
   - The file’s contents will appear in Source Code.
2. (Optional) **Edit the code**
   - Edit directly in the Source Code tab. 
   - Changes are used when you generate the accelerator (you **do not** need to re-upload).
3. **Generate & run**

    Click Generate Accelerator (right column). The app will:
   - write your current code into a temp file
   - call `gcc`/python interpreter to build an executable
   - run it and capture output/error and display in an dialog.

    **Note:** Temp files are created in the system temp folder and deleted automatically.

4. **Explore your mapping**
   - Switch to Visualization
   - Use Prev / Next to step across cycles (Cycle N displayed)
   
   Each cycle shows:
   - Nodes active in that cycle labeled operation code. 
   - Orange arrows for input/output routing information of a process element.

5. Browse the DFG
   - Open the DFG tab, the Data Flow Graph is displayed as a PDF in the GUI
   - Use Zoom In / Zoom Out buttons to change the scale
   - Use scrollbars to pan

## Files & data sources for Demo
The visualization uses three resources (packaged in resources/docs by default):
- JSON (e.g., `hycube_original_updatemem4x4.json`) to config fabric matrix. Reads the fabric dimensions (`DIMS: { "X": rows, "Y": cols }`).
- `.prog` files to show operation code and routing information of a PE for each cycle.
- `mapping.txt`(Not used now) to show the mapping information of PE for each cycle.
