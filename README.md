# Image Triangulation Art Generator (Java, Delaunay Triangulation)

## Run in VS Code
1. Install a JDK (17 or 21) and the "Extension Pack for Java" in VS Code.
2. File > Open Folder... > choose the `TriangulationArt` folder.
3. Open `src/Main.java` and click **Run** above `main`.

Terminal alternative (from the TriangulationArt folder):
```
javac -d bin src/Main.java src/*/*.java
java -cp bin Main
```
(On Windows PowerShell, use: `javac -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName`)

## Java concepts used (for the report / viva)
| Concept | Where |
|---|---|
| Inheritance | `PointSelector` (abstract) -> `EdgePointSelector`, `RandomPointSelector`, `JitteredGridPointSelector`; `MeshRenderer extends FilledRenderer` |
| Abstract class / template method | `PointSelector.select()` |
| Polymorphism | `createSelector()` and `refreshResult()` in `MainFrame`; overridden `getName()` / `render()` |
| Interfaces | `Renderer`, `ProgressCallback` |
| Exception handling | try/catch/finally-style handling in `MainFrame`, `ImageLoader`, `GenerateWorker.done()` |
| Custom exceptions | `InvalidImageException`, `TooFewPointsException`, `ProcessingCancelledException` |
| Multi-threading | `GenerateWorker extends SwingWorker` (background thread, progress bar, cancel) |
| AWT | `Graphics2D`, `Color`, `Point`, `Polygon`, layout managers, `ActionListener`, `MouseAdapter` |
| Collections | `ArrayList`, `HashMap`, `List`, `Map` |
| File I/O | `ImageIO` (open/save), `FileWriter` log (`RunLogger`) |
| Packages | `exceptions`, `model`, `algorithm`, `render`, `util`, `ui` |
| Inner / anonymous classes | `DelaunayTriangulator.Tri`, anonymous `ActionListener`, `phase()` |
| Encapsulation | private fields with getters in `Triangle`, `ArtResult` |

Tip: set the Points slider below 100 and press Generate to demonstrate the custom exception.
