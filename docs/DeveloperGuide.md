# Developer Guide: Markdown Desktop Notebook

## 1. System Architecture
### 1.1 Hexagonal Architecture (Ports & Adapters)
* Explanation of core domain isolation from external frameworks.
* Inbound ports (Manager/Use cases) vs Outbound ports (`NoteRepository`, `MarkdownRenderer`).
* Headless testability invariant (zero JavaFX imports in domain/logic/storage).

### 1.2 MVVM Pattern (UI & Presentation)
* Presentation separation via `MainViewModel`.
* Bidirectional property bindings, 250ms debouncer, dirty state lifecycle.
* View components: `MainWindow`, `NavigationPane`, `PreviewPane`.

## 2. Component Design & Implementation
* **Domain Model**: `Note` (tag parsing, immutability, defensive copies), `FolderNode`.
* **Logic Engine**: `NotebookManager` orchestration, `SearchEngine` tokenization and `#tag` indexing.
* **Adapters**:
  * `FileSystemStorage`: Path sanitization, collision counter logic (`Title 1.md`), atomic writes.
  * `CommonMarkRenderer`: GFM extensions (tables, strikethrough), HTML generation.
  * `PreviewPane`: WebKit `WebView` safe template replacement (`{{BODY}}`).
* **TextStatistics (`com.notebook.logic.TextStatistics`)**:
  * Pure domain record computing real-time document metrics (words, total characters, characters excluding whitespace, lines, and reading duration).
  * Decoupled completely from JavaFX UI controls to support headless unit testing.
  * Observed by `MainViewModel` and bound reactively to the `MainWindow` bottom status bar.
  * **TextFormatter (`com.notebook.logic.TextFormatter`)**:
  * Pure utility class handling inline markdown token wrapping and multi-line prefixes.
  * Preserves selection range and calculates cursor placement without any dependency on JavaFX text controls.
  * Injected into the `MainWindow` editor key event pipeline via platform-independent shortcut accelerators (`event.isShortcutDown()`).

## 3. Key Architectural Workflows
* **Note Creation & Auto-Save**: Flow from UI input -> ViewModel debounce -> Manager -> FileSystemStorage.
* **Search & Filter**: Flow from NavigationPane search bar -> ViewModel -> SearchEngine query evaluation.

## 4. Testing & Verification
* Pure JUnit 5 headless test suite (`mvn test`).
* Mocking and in-memory test stubs for ports.
* CI pipeline guarantees.

## 5. Build & Deployment
* Requirements (JDK 17, Maven 3.9+).
* Packaging and running with `javafx-maven-plugin`.