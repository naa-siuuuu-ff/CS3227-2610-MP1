First, I just tried to understand what the assignment deliverables are, and the tools I need to make the project.
I also tried to ensure that my markdown editor can be made using javafx

### Prompt & Interaction Summary Log

- **Date & Context:** Project initialization and environment setup for CS3227-2610-MP1 (Individual Project).
- **AI Tool Used:** AI Assistant
- **Key Topics Covered:** Project requirements breakdown, tooling evaluation, project feasibility/scoping, project scaffolding, and build tool configuration.

---

**Summary of Key Discussion Points & Engineering Decisions:**

- **Deliverables Breakdown:**
- Clarified repository requirements (`CS3227-2610-MP1`) including source code, `docs/UserGuide.md`, `docs/DeveloperGuide.md`, `docs/Reflections.md`, `logs/` directory, and Canvas quiz submission.

- **AI Tooling Comparison:**
- Analyzed GPT/Codex ($30 SGD budget limit) vs. Google Antigravity across workflow alignment, agent capabilities, logging mechanics, and IDE integration.

- **Scope Evaluation (Warm-up & App Idea):**
- Recommended skipping a full CS2103/T Duke warm-up rewrite to preserve the $30 SGD API budget and meet the 1-Sep deadline, opting instead for a 30-minute calibration test.
- Evaluated the feasibility of a Notion clone. Scoped it down to an offline Markdown/Hierarchical Note Manager using a dual-pane JavaFX architecture (`TextArea` + `WebView` via `commonmark-java`) to avoid UI complexity and adhere to the "no to-do manager" restriction.

- **Project Scaffolding & Build System:**
- Confirmed that Maven is practically mandatory for managing decoupled JavaFX modules and external dependencies.
- Defined the Maven directory structure (`src/main/java`, `src/main/resources`, `src/test/java`, `pom.xml`) and outlined setup steps tailored for Visual Studio Code on macOS.

---

**Human Verification:**

- Verified structure meets the assignment submission guidelines.
- Scoped feature set confirmed to satisfy non-to-do utility app constraints.

Then I tried to narrow down the functionality of the markdown editor, and what it would look like:

### Evaluation of the Three Approaches

**Approach 1: Dual-Pane Split Editor (`TextArea` + `WebView`) — Best Pick**

- **Verdict:** Strongly recommended for this deadline.
- **Why it works:** You get a live preview with very little boilerplate. Libraries like `commonmark-java` convert raw strings to HTML in a few lines of code, which `WebView` renders immediately via `webEngine.loadContent(html)`.
- **Where LLMs shine:** LLMs generate standard regex hooks, debounced text change listeners (`PauseTransition`), and CommonMark parser configurations reliably without hallucinating non-existent JavaFX APIs.
- **Testing & Architecture:** Clean separation of concerns. Your core Markdown parsing, file I/O, and note indexing logic remain pure Java with zero GUI dependencies, making unit testing with JUnit straightforward.
- **Implementation tip:** Add a simple debounce (e.g., 150–300 ms) on the text listener before re-rendering the HTML to prevent stuttering during fast typing.

---

**Approach 2: In-Editor Syntax Highlighting (`RichTextFX`) — Viable, but risky UI quirks**

- **Verdict:** Doable, but introduces unnecessary UI debugging overhead.
- **Why it is tricky:** JavaFX's standard `TextArea` does not support inline styling, so you must use the third-party `RichTextFX` library. While syntax highlighting via regex/AST token matching is technically sound, `RichTextFX` has notorious CSS styling quirks, occasional cursor jumps, and custom dependency issues with Java modularity (`module-info.java`).
- **Fit for MP1:** It does not give peer reviewers the visual satisfaction of a rendered HTML preview (tables, bold styling, headers), yet it requires more debugging time than Approach 1.

---

**Approach 3: Notion-Style In-Place Block/WYSIWYG Editor — High Risk, Avoid**

- **Verdict:** Avoid completely under the current time constraints.
- **Why it is tricky:** True WYSIWYG or block-based editing in JavaFX either requires building a custom nested node tree (which is fragile and prone to state sync bugs) or embedding a JavaScript-based web editor (such as TipTap, Monaco, or Quill) inside `WebView` with a two-way `Java-JS bridge`.
- **Fit for MP1:** Debugging JavaScript-to-Java bridges and state synchronization under a tight deadline will drain your time and token budget on platform plumbing rather than core features, test coverage, and documentation.

---

### Comparison Matrix

| Approach                                  | Implementation Effort | Peer Review Polish                  | Unit Testing Simplicity         | LLM Generation Reliability                 | Overall Recommendation  |
| ----------------------------------------- | --------------------- | ----------------------------------- | ------------------------------- | ------------------------------------------ | ----------------------- |
| **1. Dual-Pane (SplitPane + WebView)**    | ~1–2 days             | High (Clean formatted HTML preview) | High (Pure Java parser & model) | High (Standard APIs, minimal bugs)         | **Recommended**         |
| **2. In-Editor (`RichTextFX`)**           | ~3–4 days             | Medium (Only colors text)           | Medium (Tied to UI tokens)      | Medium (Frequent CSS/span styling issues)  | Feasible, but secondary |
| **3. Block/WYSIWYG (JavaFX / JS Bridge)** | >1–2 weeks            | High (If working)                   | Low (Complex async state)       | Low (High hallucination on JS-Java bridge) | **Avoid**               |

---

### Next Step

If you'd like, we can lay down the exact system specifications, data model (folders, notes, metadata), and functional command structure for **Approach 1** so you can begin drafting your initial prompts and logging workflow.

Prompt: I first want to know from the users perspective, what would appraoch 1 look like and be like to use?

### Visual Layout

The application window uses a standard **3-pane layout**, similar to desktop editors like Obsidian, Typora, or standard IDEs:

```
+------------------+------------------------+------------------------+
| [Search Bar    ] | Note: Machine_Learning | Preview                |
|                  +------------------------+------------------------+
| > Notebooks      | # Linear Regression    | Linear Regression      |
|   v CS3227       |                        |                        |
|     * Lecture 1  | Gradient descent finds | Gradient descent finds |
|     * Lecture 2  | the minimum of cost    | the minimum of cost    |
|   > Personal     | function:              | function:              |
|                  |                        |                        |
|                  | - Model parameters     | • Model parameters     |
|                  | - Learning rate        | • Learning rate        |
|                  |                        |                        |
+------------------+------------------------+------------------------+
| 1. Navigation    | 2. Raw Editor          | 3. Live HTML Preview   |
| (TreeView)       | (JavaFX TextArea)      | (JavaFX WebView)       |
+------------------+------------------------+------------------------+

```

---

### Core User Flow & Experience

**1. Browsing and Managing Notes (Left Pane)**

- **Notebook Hierarchy:** The user sees a tree structure representing folders and files saved in a local storage directory.
- **Selection:** Clicking any `.md` file in the tree immediately loads the raw Markdown text into the center editor and renders the preview on the right.
- **Quick Filtering / Search:** A search bar above the tree allows real-time filtering by note title, tags (e.g., `#ai`), or full-text keywords.

**2. Writing & Live Preview (Center & Right Panes)**

- **Typing:** The user types raw Markdown into the center `TextArea` (headings, lists, bold/italics, code blocks, tables).
- **Live Update:** As the user pauses typing (~200–300 ms debounce), the right `WebView` pane automatically re-renders the document into formatted HTML with clean typography, styled headers, and styled code blocks.
- **Distraction-Free Option:** The user can drag the central divider (`SplitPane`) to hide the raw editor or hide the preview pane if they want a single-pane view.

**3. Inter-Note Navigation (Wiki-Style Linking)**

- **Writing Links:** The user writes links using double brackets, such as `[[Lecture 2]]`.
- **Clickable Jumps:** In the rendered preview pane on the right, clicking `[[Lecture 2]]` automatically switches the editor to `Lecture 2.md` and updates the navigation tree selection.

**4. Persistence & Saving**

- **Auto-Save:** File changes are saved automatically whenever the debounced preview renders, or explicitly via `Ctrl+S` / `Cmd+S`.
- **Zero Cloud Dependence:** All files exist as human-readable `.md` files in a local folder on the user's hard drive, readable by any external text editor.

---

### What Makes This Practical for Testing & Peer Evaluation

- **Zero friction setup:** No complex external database or account setup needed; running the Java jar points directly to a local test directory.
- **Intuitive for reviewers:** Anyone familiar with standard note-taking tools understands the interface immediately.
- **Robust:** No flaky web bridges or content-editable bugs that cause crashes during peer grading.

Then I tried to get the architechture of the application down:

### 1. High-Level System Architecture

The architecture follows standard CS2103/T **Layered / MVC patterns** with strict separation between GUI, business logic, storage, and external parsing libraries:

```
                  +-----------------------------------+
                  |         UI Layer (JavaFX)         |
                  |  - MainWindow (3-pane layout)     |
                  |  - FileTreeView, Editor, Preview  |
                  +-----------------+-----------------+
                                    |
                                    v
                  +-----------------+-----------------+
                  |          Logic Layer              |
                  |  - NotebookManager / Controller   |
                  |  - SearchEngine & Indexer         |
                  |  - MarkdownParser (CommonMark)    |
                  +-----------------+-----------------+
                                    |
                                    v
                  +-----------------+-----------------+
                  |          Model Layer              |
                  |  - Notebook, Note, Tag, Link      |
                  +-----------------+-----------------+
                                    |
                                    v
                  +-----------------+-----------------+
                  |          Storage Layer            |
                  |  - LocalFileSystemStorage         |
                  |  - MetadataIndexStorage (JSON)    |
                  +-----------------------------------+

```

---

### 2. Component Structure & Responsibilities

#### A. UI Layer (`seedu.markdownnotebook.ui` or `com.notebook.ui`)

- **`MainWindow`**: Container hosting a top toolbar (New Note, Save, Export, Search bar) and a `SplitPane` with three sub-views:
- **`NavigationPane` (`TreeView<TreeItemData>`)**: Displays hierarchical folder directories and note files.
- **`EditorPane` (`TextArea`)**: Plain-text editor capturing keystrokes and emitting text-change events.
- **`PreviewPane` (`WebView` / `WebEngine`)**: Renders HTML generated by the parser. Listens to hyperlink clicks (intercepting internal `[[Page]]` navigation).

- **`DebounceService`**: Uses a JavaFX `PauseTransition` (200–300 ms) to debounce rapid keystrokes before triggering markdown conversion and file auto-saving.

#### B. Logic Layer (`com.notebook.logic`)

- **`NotebookManager`**: The core controller orchestrating state:
- Manages the currently open `Note` and active `Notebook`.
- Handles CRUD operations (create, rename, delete notes/folders).
- Coordinates auto-saving and dirty-state tracking.

- **`MarkdownParser`**:
- Wraps `org.commonmark:commonmark` to convert raw text to HTML.
- Custom AST Visitor or Regex processor: Replaces `[[Target Note]]` syntax with `<a href="internal://Target Note">Target Note</a>` before or during rendering.

- **`SearchEngine`**:
- In-memory indexing of note titles, tags, and full text.
- Provides quick filtering (`filterByTag("ai")`, `search("gradient descent")`).

#### C. Model Layer (`com.notebook.model`)

- **`Note`**: Represents a single markdown document.
- `id`: UUID or relative path.
- `title`: String.
- `content`: String (raw markdown body).
- `tags`: `Set<String>` (extracted from `#tag` or frontmatter).
- `lastModified`: `Instant`.
- `isDirty`: `boolean` (tracks unsaved changes).

- **`Folder` / `Notebook**`: Represents a category or physical directory holding `Note` and sub-`Folder` instances.
- **`NoteMetadata`**: Lightweight representation for searching and navigation without loading full document bodies into memory.

#### D. Storage Layer (`com.notebook.storage`)

- **`FileSystemStorage`**:
- Reads/writes raw `.md` files directly on disk under a root data folder (e.g., `./data/notebooks/`).
- Pure standard Java (`java.nio.file.Files`, `java.nio.file.Path`).

- **`MetadataStorage`** _(Optional)_:
- Simple JSON serialization (via Jackson/Gson) if you wish to store extra metadata (pinned notes, recent files) separately from raw `.md` files.

---

### 3. Detailed Data Models (Java Class Design)

```java
// Model Layer

public class Note {
    private final Path filePath;
    private String title;
    private String content;
    private Set<String> tags;
    private Instant lastModified;
    private boolean isDirty;

    public Note(Path filePath, String title, String content) {
        this.filePath = filePath;
        this.title = title;
        this.content = content;
        this.tags = extractTags(content);
        this.lastModified = Instant.now();
        this.isDirty = false;
    }

    // Getters, Setters, Tag Parsing, Dirty state flags
}

public class FolderNode {
    private final String name;
    private final Path path;
    private final List<FolderNode> subFolders;
    private final List<Path> noteFiles;

    // Constructors and tree traversal helpers
}

```

---

### 4. Key Execution Workflows

#### A. Document Edit & Preview Pipeline

```
[User Types in EditorPane]
          |
          v
[EditorPane Listener triggers DebounceService] (waits 250ms)
          |
          v
[MarkdownParser.toHtml(content)] -> [Converts [[Links]] & Markdown]
          |
          +---> [PreviewPane.getEngine().loadContent(html)]
          |
          +---> [NotebookManager.autoSave(currentNote)] -> [FileSystemStorage.write(path, content)]

```

#### B. Internal Link Navigation (`[[Note Name]]`)

```
[User clicks <a href="internal://Lecture2"> in PreviewPane]
          |
          v
[WebEngine location listener intercepts URI starting with "internal://"]
          |
          v
[Event cancelled in WebView] -> [NotebookManager.openNoteByName("Lecture2")]
          |
          v
[Load file from Storage] -> [Update Navigation Tree Selection] -> [Populate Editor & Preview]

```

---

### 5. Recommended Project Directory Scaffolding

```text
CS3227-2610-MP1/
├── .github/
├── docs/
│   ├── UserGuide.md
│   ├── DeveloperGuide.md
│   └── Reflections.md
├── logs/
│   └── (prompt-summaries.md)
├── src/
│   ├── main/
│   │   ├── java/com/notebook/
│   │   │   ├── MainApp.java
│   │   │   ├── logic/
│   │   │   │   ├── NotebookManager.java
│   │   │   │   ├── MarkdownParser.java
│   │   │   │   └── SearchEngine.java
│   │   │   ├── model/
│   │   │   │   ├── Note.java
│   │   │   │   └── FolderNode.java
│   │   │   ├── storage/
│   │   │   │   └── FileSystemStorage.java
│   │   │   └── ui/
│   │   │       ├── MainWindow.java
│   │   │       ├── NavigationPane.java
│   │   │       └── PreviewPane.java
│   │   └── resources/
│   │       ├── css/
│   │       │   ├── editor.css
│   │       │   └── preview.css (HTML typography)
│   │       └── fxml/
│   │           └── MainWindow.fxml (if using FXML)
│   └── test/
│       └── java/com/notebook/
│           ├── logic/MarkdownParserTest.java
│           ├── logic/SearchEngineTest.java
│           └── storage/FileSystemStorageTest.java
└── pom.xml

```

---

### 6. Recommended Next Step

To begin building with your LLM workflow:

1. Initialize the `pom.xml` with dependencies (`org.openjfx`, `org.commonmark:commonmark`, and `org.junit.jupiter`).
2. Prompt the model for the foundational non-UI components (`Note`, `FileSystemStorage`, and `MarkdownParser`) with accompanying unit tests to establish your initial entry in `logs/`.

Asked claude (Was using gemini for the previous things): Comment on the given architecture, and give alternative architechtures that you think may be better

Both — comment on the structure first, then considerations and code.

Searched the web

Good bones on this — clean top-level split, tests mirror main, docs/logs match the submission spec exactly. But three real gaps, all tracing back to what we landed on last message:
No ports. storage/FileSystemStorage.java isn't behind an interface anything else can depend on — same for MarkdownParser. As written, whatever calls into storage is coupled to the concrete file class, which is exactly what the hexagonal split was meant to avoid. Small fix, but skip it and you lose the "swap the adapter, not the caller" story for the DG.
No ViewModel layer. ui/ has MainWindow, NavigationPane, PreviewPane — reads like Views/Controllers talking straight to logic. That's a legitimate simpler layered approach on its own, but it's not what we landed on, and it's the piece most likely to get silently dropped under time pressure since nothing in the tree forces it to exist. Decide this explicitly rather than by omission.
NotebookManager risk. It's the only coordinator in logic/ — folder tree, note CRUD, probably save/load orchestration too. Doesn't need splitting for a project this size, but watch it for "does everything" sprawl once real methods land in there.
One more thing: tests only cover MarkdownParser, SearchEngine, FileSystemStorage — nothing for NotebookManager, almost certainly your most logic-heavy class.
Minimal-diff fix — you don't need to rebuild this, just add four things:
src/main/java/com/notebook/
├── MainApp.java ← composition root: wires adapters into services
├── logic/
│ ├── NotebookManager.java
│ ├── NoteRepository.java + NEW: port (interface)
│ ├── MarkdownRenderer.java + NEW: port (interface)
│ └── SearchEngine.java
├── model/
│ ├── Note.java
│ └── FolderNode.java
├── storage/
│ └── FileSystemStorage.java → implements NoteRepository
├── render/ + NEW package
│ └── CommonMarkRenderer.java → implements MarkdownRenderer (was MarkdownParser)
├── viewmodel/ + NEW package
│ └── MainViewModel.java ← properties the Views bind to
└── ui/
├── MainWindow.java → binds to MainViewModel, no direct logic calls
├── NavigationPane.java
└── PreviewPane.java

src/test/java/com/notebook/logic/
└── NotebookManagerTest.java + NEW: your most logic-heavy class needs coverage
Considerations
JavaFX threading. File I/O and any full-folder rescan must not run on the FX Application Thread or the UI will visibly freeze. Wrap those in javafx.concurrent.Task, run on a background executor, push results back through your ViewModel's properties (which JavaFX marshals onto the FX thread for you if updated via Platform.runLater).
Metadata persistence format. Plain .md has nowhere to put tags/timestamps/backlink targets. Pick one: YAML frontmatter at the top of each file (portable, self-contained, and gives you a citable prior-art pattern for the DG — Obsidian/Jekyll do this), or a sidecar index file the app maintains separately (simpler to parse, can drift if files are edited externally). Write the decision down — it's good Reflections.md material either way.
Note identity. Is a note's ID its file path, or a stable ID independent of path? If you build [[links]]/backlinks at any point, path-based identity breaks the moment a note is renamed — worth deciding before Note.java locks in its ID scheme.
Maven + JavaFX classifiers. Peer testers will be on a mix of Windows/macOS/Linux. If your pom.xml pins a single OS classifier for the JavaFX artifacts, it won't run on someone else's machine. Let the javafx-maven-plugin handle platform detection instead of hardcoding one.
Distribution. Don't reach for jlink/jpackage this close to the deadline — that's exactly the packaging pain flagged earlier for javafx-web. mvn javafx:run from a cloned repo is simpler, portable, and is almost certainly what your UserGuide setup steps should say.
CI scope. Since .github/ already exists — keep the workflow to mvn test against logic/model/storage/render only. Don't try to launch JavaFX in headless CI; that's the WebView-in-CI problem again, and it's exactly what keeping Core pure-Java was for.
