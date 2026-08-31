Next, the goal is to make the mvp. Here is a test to make sure that javafx is working

Your `pom.xml` is functional, but three specific adjustments should be made before running your Day 0 spike:

---

### Key Adjustments

- **Upgrade JavaFX Version to 21.0.11:** `17.0.2` is an older release that frequently encounters platform-native WebKit rendering bugs, especially on Apple Silicon (M-series) macOS and newer Linux distributions. Upgrading to `21.0.11` (the long-term release aligned with Java 17/21) provides a more stable embedded `WebView`.
- **Fix Missing JavaFX Modules Configuration:** If your project does not use a `module-info.java` file, `javafx-maven-plugin` must explicitly declare which JavaFX modules to include at runtime (`javafx.controls`, `javafx.fxml`, `javafx.web`). Without this, `mvn javafx:run` will throw `java.lang.NoClassDefFoundError: javafx/application/Application`.
- **Align CommonMark and Extension Versions:** Keep `commonmark.version` at `0.21.0` or update cleanly across the parser, table extension, and strikethrough extension.

---

### Updated `pom.xml`

## <Code here>

### Step to Verify Now

Create a minimal `src/main/java/com/notebook/MainApp.java` and execute:

```bash
mvn clean compile javafx:run

```

For your Day 0 de-risking spike, a console `System.out.println("Hello World")` is not enough. You need to verify that **JavaFX initializes, the graphic window opens, and the native embedded WebKit engine (`WebView`) renders HTML** without platform-binding crashes.

Replace the contents of `src/main/java/com/notebook/MainApp.java` with this minimal spike implementation:

```java
package com.notebook;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();

        // Spike test: render basic HTML to confirm WebKit/JavaFX Web works
        String testHtml = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: sans-serif; padding: 20px;">
                <h1 style="color: #2b579a;">Spike: JavaFX WebView Active</h1>
                <p>If you can read this, your JavaFX and WebKit native dependencies are functioning correctly.</p>
            </body>
            </html>
            """;

        webView.getEngine().loadContent(testHtml);

        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("MP1 De-risking Spike");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

```

---

### How to Run the Spike

In your project root terminal, run:

```bash
mvn clean compile javafx:run

```

- **Pass condition:** A 600×400 desktop window pops up rendering the styled blue header and paragraph.
- **Fail condition:** The terminal throws native binary errors (e.g., `libjfxwebkit`, missing architecture `.dylib`/`.so`/`.dll`, or module access warnings).

**Now with javaFX test done successfully, lets build the notebook system, then connect it to the javaFX UI.**

## Session 2: Headless Core Implementation & Verification
- **Date/Time**: 2026-08-30
- **Goal**: Implement and verify the headless Core layer (domain models, repository/renderer ports, adapters, and unit tests) without any JavaFX GUI dependencies.

### Prompts Issued
1. **Prompt 1 (Implementation Request)**: Requested the complete source code and relevant test classes for Day 1:
   - Domain models (`Note`, `FolderNode`).
   - Logic ports (`NoteRepository`, `MarkdownRenderer`).
   - Adapters (`FileSystemStorage`, `CommonMarkRenderer`).
   - Unit tests for storage and markdown parsing.
2. **Prompt 2 (Code Review & Clarification)**: Asked why `FolderNode` returned `Collections.unmodifiableList(...)` instead of raw lists (`getSubFolders()`, `getNoteIds()`).
3. **Prompt 3 (Execution & Verification)**: Provided the output of `mvn clean test` (confirming 6/6 tests passed across `CommonMarkRendererTest` and `FileSystemStorageTest`) and requested a structured handover prompt for the next development phase.

### Key Actions & Decisions
- Implemented `Note` with regex-based `#tag` extraction, defensive copies, and dirty/last-modified timestamps.
- Implemented `FolderNode` enforcing immutability/encapsulation via `Collections.unmodifiableList(...)` to prevent internal state leakage.
- Created `NoteRepository` and `MarkdownRenderer` interfaces in `com.notebook.logic` to decouple core logic from filesystem I/O and third-party libraries.
- Implemented `FileSystemStorage` using `java.nio.file` and `CommonMarkRenderer` configured with GitHub Flavored Markdown (GFM) tables and strikethrough extensions.
- Wrote JUnit 5 tests utilizing `@TempDir` to ensure headless, disk-isolated test execution.
- Executed `mvn clean test`: verified 6 tests run with 0 failures/errors in 3.96 seconds.
- Formulated a comprehensive context-handover prompt detailing the architectural patterns, completed components, `pom.xml`, and remaining milestones for the next session.

AI Interaction Log Entry: Architecture Wiring & JavaFX Stabilization
Date: 31 August 2026
Focus Area: Domain Orchestration, MVVM Presentation, JavaFX 3-Pane UI Assembly, and Bug Fixing
Summary of Activities:
Hexagonal Logic Orchestration (NotebookManager.java): Implemented the domain controller coordinating NoteRepository, MarkdownRenderer, and SearchEngine using pure standard library Java without UI framework dependencies.
Headless Unit Testing (NotebookManagerTest.java): Authored JUnit 5 tests utilizing an in-memory repository stub and deterministic renderer stub to verify CRUD operations, search delegation, and rendering headlessly under mvn test.
MVVM Presentation Layer (MainViewModel.java):
Bound domain operations to JavaFX observable properties (ObservableList<Note>, activeNote, editorContent, htmlPreview, isDirty).
Configured a 250 ms PauseTransition debounce mechanism to throttle HTML rendering and trigger auto-saving during rapid text input.
Resolved missing java.util.Objects import causing compilation failures.
Desktop UI Construction (com.notebook.ui):
Built MainWindow.java utilizing a 3-pane SplitPane layout.
Implemented NavigationPane.java with dynamic keyword/tag search filtering, note creation dialogs, and deletion controls.
Constructed PreviewPane.java hosting an embedded WebKit WebView.
Application Composition Root & Lifecycle (MainApp.java):
Wired outbound file system and Markdown rendering adapters into NotebookManager and MainViewModel.
Configured dual-stage safe shutdown handlers (setOnCloseRequest and stop()) to ensure dirty edits are flushed to disk before application exit.
Bug Fixes & Refactoring:
WebKit CSS Formatting Crash: Diagnosed and resolved an UnknownFormatConversionException where CSS percentage markers (width: 100%;) collided with String.formatted(); refactored HTML injection to use token replacement ({{BODY}}).
UUID Display in Navigation: Identified a discrepancy where UUIDs were displayed instead of human-readable titles due to filename handling in FileSystemStorage; refactored NotebookManager.createNote to sanitize user titles as file identifiers and append auto-increment counters on name collisions.
Unused Imports: Cleaned unneeded java.util.Objects imports in SearchEngine.java.
Project Documentation: Generated AGENTS.md specifying architectural boundaries, headless testing rules, and an agent handoff prompt.

I managed to build a working MVP, and fixed a bug along the way where notebook titles were different. It is time to move on to making the final product
