# Developer Guide: Markdown Desktop Notebook[cite: 1]

## 1. System Architecture

The project is built on **Hexagonal Architecture (Ports & Adapters)** coupled with the **Model-View-ViewModel (MVVM)** pattern for presentation[cite: 1, 3]. Business logic and domain entities are kept strictly independent from JavaFX UI controls and underlying storage mechanisms[cite: 1, 3].

```text
+-------------------------------------------------------------------------+
|                              UI (JavaFX)                                |
|   MainWindow  <--->  NavigationPane  <--->  PreviewPane (WebKit WebView)|
+-------------------------------------------------------------------------+
                                    │ Bidirectional Bindings & Observables[cite: 3]
                                    ▼
+-------------------------------------------------------------------------+
|                        Presentation (ViewModel)                         |
|   MainViewModel (250ms debounce, isDirty tracking, active note binding) |[cite: 3]
+-------------------------------------------------------------------------+
                                    │ Pure Java Invocations[cite: 3]
                                    ▼
+-------------------------------------------------------------------------+
|                        Core Domain & Logic                              |
|   NotebookManager (Orchestrator)                                        |[cite: 3]
|   SearchEngine (Tag indexing, keyword filtering)                        |[cite: 3]
|   TextStatistics, TextFormatter (Domain Records & Utilities)            |[cite: 1]
|   Note, FolderNode (Pure Domain Models)                                 |[cite: 1, 3]
+-------------------------------------------------------------------------+
           │ (Port Interface)[cite: 3]                   │ (Port Interface)[cite: 3]
           ▼                                              ▼
   [NoteRepository]                               [MarkdownRenderer][cite: 3]
           ▲                                              ▲
           │ Implements[cite: 3]                         │ Implements[cite: 3]
+---------------------------+                 +---------------------------+
|    FileSystemStorage      |                 |    CommonMarkRenderer     |
|   (Local .md Adapter)     |                 |  (CommonMark+GFM Adapter) |[cite: 3]
+---------------------------+                 +---------------------------+
```

### Architectural Invariants
* **Headless Core Logic**: No `javafx.*` imports are permitted inside `model`, `logic`, `storage`, or `render` packages[cite: 1, 3]. This allows the domain and storage engines to execute headless unit tests via `mvn test` in under 1 second without initializing a JavaFX toolkit[cite: 1, 3].
* **Inversion of Control**: Core orchestration relies exclusively on inbound/outbound ports (`NoteRepository`, `MarkdownRenderer`)[cite: 1, 3].

---

## 2. Component Design & Implementation

### 2.1 Domain Models
* **`Note` (`com.notebook.model.Note`)**: Immutable representation of a note (`id`, `title`, `content`, `tags`, `lastModified`)[cite: 1, 3].
  * Extracts inline tags using the regular expression `#([a-zA-Z0-9_-]+)`[cite: 3].
  * Maintains defensive copying over tag sets to guarantee immutability[cite: 1, 3].
* **`FolderNode` (`com.notebook.model.FolderNode`)**: Domain node structure for hierarchical tree representations[cite: 1, 3].

### 2.2 Domain Logic & Services
* **`NotebookManager` (`com.notebook.logic.NotebookManager`)**: Primary orchestrator coordinating CRUD actions between the repository, search engine, and renderer[cite: 1, 3]. Handles sanitized title normalization[cite: 3].
* **`SearchEngine` (`com.notebook.logic.SearchEngine`)**:
  * Parses search tokens: queries beginning with `#` query the indexed tag registry[cite: 1, 3].
  * Standard text queries run case-insensitive substring matches across note titles and bodies[cite: 1, 3].
* **`TextStatistics` (`com.notebook.logic.TextStatistics`)**:
  * Pure domain record computing real-time document metrics (words, total characters, characters excluding whitespace, line counts, and estimated reading time at 200 WPM)[cite: 1, 2].
  * Pure Java implementation decoupled from UI controls to allow headless testing[cite: 1].
* **`TextFormatter` (`com.notebook.logic.TextFormatter`)**:
  * Pure utility handling inline token injection (`**`, `*`, `` ` ``) and multi-line formatting prefixes[cite: 1, 2].
  * Calculates new cursor and selection indices without depending on JavaFX text controls[cite: 1].

### 2.3 Ports & Adapters
* **`NoteRepository` (`com.notebook.port.NoteRepository`)**: Outbound port defining storage operations (`save`, `delete`, `findById`, `listAll`)[cite: 1, 3].
* **`MarkdownRenderer` (`com.notebook.port.MarkdownRenderer`)**: Outbound port specifying Markdown-to-HTML compilation[cite: 1, 3].
* **`FileSystemStorage` (`com.notebook.adapter.FileSystemStorage`)**:
  * Implements `NoteRepository` using Java NIO[cite: 3].
  * Writes `.md` files named after the sanitized note title[cite: 3].
  * Resolves file naming collisions by appending an incremental counter (e.g., `Title 1.md`)[cite: 1, 3].
  * Performs atomic file writes to prevent corrupted disk states[cite: 1].
* **`CommonMarkRenderer` (`com.notebook.adapter.CommonMarkRenderer`)**:
  * Implements `MarkdownRenderer` using CommonMark 0.21.0 with `TablesExtension` and `StrikethroughExtension`[cite: 1, 3, 4].

### 2.4 Presentation & UI (MVVM)
* **`MainViewModel` (`com.notebook.viewmodel.MainViewModel`)**:
  * Manages UI state, selected note, search queries, and dirty-state tracking[cite: 1, 3].
  * Uses a 250ms debouncer timer for editor input before triggering persistence and re-rendering[cite: 1, 3].
* **`MainWindow` (`com.notebook.ui.MainWindow`)**:
  * Root SplitPane view housing the three panes[cite: 1, 3].
  * Handles native keyboard shortcut accelerators (`event.isShortcutDown()`) mapped to `TextFormatter`[cite: 1].
  * Binds the bottom status bar reactively to `TextStatistics`[cite: 1].
* **`PreviewPane` (`com.notebook.ui.PreviewPane`)**:
  * Renders output in a WebKit `WebView`[cite: 1, 3].
  * Uses safe token replacement (`template.replace("{{BODY}}", content)`) rather than `String.format()` to avoid exceptions with CSS `%` characters[cite: 1, 3].

---

## 3. Key Architectural Workflows

### 3.1 Note Editing and Auto-Save Sequence
```text
User            EditorPane           MainViewModel          NotebookManager      FileSystemStorage
 |                  |                      |                       |                     |
 |-- Types text --->|                      |                       |                     |
 |                  |-- textProperty ----->|                       |                     |
 |                  |   (250ms Debounce) ->|                       |                     |
 |                  |                      |-- saveNote() -------->|                     |
 |                  |                      |                       |-- save(note) ------>|
 |                  |                      |<-- Note persisted ----|<-- Write OK --------|
 |                  |<-- Update preview ---|                       |                     |
```
*(References:[cite: 1, 3])*

### 3.2 Instant Search Workflow
1. User enters text into the search input of `NavigationPane`[cite: 1, 3].
2. The input updates `searchQueryProperty` in `MainViewModel`[cite: 3].
3. `MainViewModel` invokes `NotebookManager.search(query)`[cite: 1, 3].
4. `SearchEngine` executes a tag index lookup (if query starts with `#`) or a case-insensitive content match[cite: 1, 3].
5. The resulting note collection updates the filtered `ObservableList<Note>` bound to the note list view[cite: 1, 3].

---

## 4. Testing & Verification

The project enforces headless CI verification[cite: 1, 3]. Unit tests use in-memory stubs and mock implementations of ports (`NoteRepository`, `MarkdownRenderer`)[cite: 1, 3].

### Running Tests
Execute the JUnit Jupiter test suite from the repository root[cite: 1, 3, 4]:
```bash
mvn clean test
```
* **Coverage Scope**: Validates note immutability, tag extraction regex, file collision counters, search tokenization, debouncing logic, and markdown formatting utilities[cite: 1, 3].
* **Speed Invariant**: The test suite runs in under 1 second without launching a graphical environment[cite: 3].

---

## 5. Build & Deployment

### Build Configurations
* **Java Version**: 25[cite: 4]
* **Build Tool**: Apache Maven[cite: 1, 3]
* **Bundler**: `maven-shade-plugin` configured with entrypoint `com.notebook.Launcher`[cite: 4]

### Build Commands
* **Compile and Run Locally**:
  ```bash
  mvn clean test
  ```
* **Package Shaded Uber-JAR**:
  ```bash
  mvn clean package
  ```
* **Execute Packaged Application**:
  ```bash
  java -jar target/mp1-notebook-1.0-all.jar
  ```
*(References:[cite: 4])*

---

## 6. Acknowledgements

* **CommonMark**: [commonmark-java](https://github.com/commonmark/commonmark-java) for Markdown parsing and the GFM table and strikethrough extensions[cite: 1, 3, 4].
* **OpenJFX**: [JavaFX](https://openjfx.io/) controls, FXML, and WebKit WebView integration[cite: 1, 3, 4].
* **Architecture Inspiration**: Alistair Cockburn's Hexagonal Architecture (Ports and Adapters) pattern for domain isolation[cite: 1, 3].
* **Testing Framework**: [JUnit Jupiter 5](https://junit.org/junit5/) for headless unit testing[cite: 1, 3, 4].