# Agent Guidelines & Project Architecture: Markdown Desktop Notebook

## 1. Project Overview & Deadlines
* **Course Assignment**: CS3227-2610-MP1 (NUS School of Computing)
* **Application**: Markdown Desktop Notebook (Responsive 3-Pane Desktop Utility)
* **Target Delivery**: 1 Sep 2026 at 14:00 SGT
* **Required Deliverables**:
  * Clean build passing `mvn test` (headless CI verification) and `mvn javafx:run`.
  * `docs/UserGuide.md`, `docs/DeveloperGuide.md`, and `docs/Reflections.md` (including 3 prompt deep dives).
  * `logs/prompt-summaries.md` tracking verified AI collaboration.

---

## 2. Architectural Paradigm: Hexagonal (Ports & Adapters) + MVVM

Strict layer decoupling must be preserved across all contributions:

```text
+-------------------------------------------------------------------------+
|                              UI (JavaFX)                                |
|   MainWindow  <--->  NavigationPane  <--->  PreviewPane (WebKit WebView)|
+-------------------------------------------------------------------------+
                                    │ Bidirectional Bindings & Observables
                                    ▼
+-------------------------------------------------------------------------+
|                        Presentation (ViewModel)                         |
|   MainViewModel (250ms debounce, isDirty tracking, active note binding) |
+-------------------------------------------------------------------------+
                                    │ Pure Java Invocations
                                    ▼
+-------------------------------------------------------------------------+
|                        Core Domain & Logic                              |
|   NotebookManager (Orchestrator)                                        |
|   SearchEngine (Tag indexing, keyword filtering)                        |
|   Note, FolderNode (Pure Domain Models)                                 |
+-------------------------------------------------------------------------+
           │ (Port Interface)                             │ (Port Interface)
           ▼                                              ▼
   [NoteRepository]                               [MarkdownRenderer]
           ▲                                              ▲
           │ Implements                                   │ Implements
+---------------------------+                 +---------------------------+
|    FileSystemStorage      |                 |    CommonMarkRenderer     |
|   (Local .md Adapter)     |                 |  (CommonMark+GFM Adapter) |
+---------------------------+                 +---------------------------+
```

---

## 3. Invariants & Rules for AI Agents

* **Zero JavaFX in Core Domain**:
  * Under no circumstance should `javafx.*` classes be imported into `model`, `logic`, `storage`, or `render`.
  * Only `ui`, `viewmodel`, and `MainApp` may reference JavaFX packages.
  * `mvn test` must execute headless and complete in under 1 second without initializing a JavaFX toolkit.
* **Context Verification Before Editing (CRITICAL)**:
  * Never assume or hallucinate existing file implementations.
  * If a task touches, modifies, or depends on an existing file, prompt the user to paste the exact current code before proposing changes.
* **Safe File I/O & Naming**:
  * Storage writes `.md` files directly using the sanitized note title (`Title.md`) to avoid displaying raw UUIDs.
  * File collisions must append an increment (`Title 1.md`) rather than overwrite.
* **WebView Safe Templating**:
  * When injecting HTML into `WebView`, use standard token replacement (e.g. `replace("{{BODY}}", content)`) rather than `String.formatted()`, as unescaped CSS percentage signs (`%`) cause runtime format exceptions.

---

## 4. Technology Stack & Key Dependencies

* **JDK**: 17
* **JavaFX**: 21.0.11 (`javafx-controls`, `javafx-fxml`, `javafx-web`)
* **Markdown**: CommonMark 0.21.0 with `commonmark-ext-gfm-tables` and `commonmark-ext-gfm-strikethrough`
* **Testing**: JUnit Jupiter 5.10.0
* **Build Tool**: Apache Maven (`javafx-maven-plugin:0.0.8`)

---

## 5. Current Component Status

| Class / File | Layer | Status | Notes |
| :--- | :--- | :--- | :--- |
| `Note.java` | Model | Complete | Tags regex `#([a-zA-Z0-9_-]+)`, defensive copies |
| `FolderNode.java` | Model | Complete | Hierarchical folder tree support |
| `NoteRepository.java` | Port | Complete | Interface for CRUD operations |
| `MarkdownRenderer.java` | Port | Complete | Rendering interface |
| `FileSystemStorage.java` | Adapter | Complete | NIO file persistence under root folder |
| `CommonMarkRenderer.java` | Adapter | Complete | CommonMark + GFM extensions |
| `SearchEngine.java` | Logic | Complete | Tag search (`#tag`) and text matching |
| `NotebookManager.java` | Logic | Complete | Coordinates Repo + Renderer + Search; title sanitizer |
| `MainViewModel.java` | ViewModel | Complete | 250ms debouncer, dirty state, filtering |
| `MainWindow.java` | UI | Complete | SplitPane holding 3 panes |
| `NavigationPane.java` | UI | Complete | Search bar, note list, create/delete buttons |
| `PreviewPane.java` | UI | Complete | WebKit WebView with CSS rendering |
| `MainApp.java` | Entrypoint | Complete | Hexagonal root, stage init, auto-flush on exit |
| `NotebookManagerTest.java` | Test | Complete | Headless unit test using in-memory stubs |