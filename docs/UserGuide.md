# User Guide: Markdown Desktop Notebook[cite: 2]

The **Markdown Desktop Notebook** is a local-first, distraction-free desktop application designed for fast note-taking, tag-based organization, and real-time Markdown previewing[cite: 2]. Notes are stored as plain `.md` files directly on your local filesystem[cite: 2, 3].

---

## 1. System Requirements & Setup

### Prerequisites
* **Java**: JDK/JRE 25 or higher[cite: 4].
* **Operating System**: macOS (Apple Silicon / Intel), Windows (x64), or Linux (x64)[cite: 4].

### Launching the Application
1. Package the executable shaded JAR using Maven[cite: 4]:
   ```bash
   mvn clean package
   ```
2. Run the generated all-in-one JAR file[cite: 4]:
   ```bash
   java -jar target/mp1-notebook-1.0-all.jar
   ```
3. By default, your notes are stored locally on your machine at[cite: 2]:
   ```text
   ~/.markdown-notebook/notes/
   ```

---

## 2. Interface Overview

The application features a responsive three-pane layout designed for focused writing and live previewing[cite: 2, 3]:

* **Left (Navigation Pane)**: Contains the search input bar, note creation (`+ Note`) and deletion (`Delete`) buttons, and the list of available notes[cite: 1, 3].
* **Center (Editor Pane)**: Plain-text input field where you write raw CommonMark content[cite: 1].
* **Right (Preview Pane)**: Synchronized WebKit live preview rendering your formatted Markdown into HTML in real time[cite: 1, 2, 3].
* **Bottom (Status Bar)**: Real-time document statistics tracking your active writing session[cite: 1, 2].

---

## 3. Key Features & Daily Usage

### Creating and Deleting Notes
* **Create Note**: Click the **`+ Note`** button at the top of the sidebar[cite: 1]. A new note (e.g., `Untitled Note`) is created and loaded into the editor immediately[cite: 1].
* **Delete Note**: Select a note from the sidebar list and click **`Delete`**[cite: 1]. The file is removed from your storage folder and cleared from the editor[cite: 1, 3].

### Markdown Editing & Live Preview
Type your notes using standard Markdown[cite: 2]. Changes are debounced at 250ms, auto-saved to disk, and rendered into the preview panel simultaneously[cite: 1, 2, 3]. Supported syntax includes[cite: 2]:
* Headings (`# H1`, `## H2`, `### H3`)[cite: 1]
* Typography: `**bold**`, `*italics*`, `***bold italics***`, and `~~strikethrough~~`[cite: 1]
* Inline code blocks (`` `code` ``) and multi-line fenced code blocks[cite: 1]
* Bulleted lists (`* item`) and numbered lists (`1. item`)[cite: 1]
* Blockquotes (`> quote`)[cite: 1]
* GitHub-Flavored Markdown (GFM) tables[cite: 1, 2, 3]

### Formatting Keyboard Shortcuts
Format selected text quickly in the editor without typing symbols manually[cite: 2]:
* **`Ctrl+B`** (or **`Cmd+B`** on macOS): Wraps selection in `**bold**`[cite: 2].
* **`Ctrl+I`** (or **`Cmd+I`** on macOS): Wraps selection in `*italics*`[cite: 2].
* **`Ctrl+K`** (or **`Cmd+K`** on macOS): Wraps selection in `` `inline code` ``[cite: 2].

> **Tip**: If no text is selected when pressing the shortcut, the markdown tokens are inserted and the cursor is placed automatically between them[cite: 2].

### Tagging System
Tag notes easily by typing inline hashtags anywhere in the note title or body using the `#tagname` format (e.g., `#cs3227`, `#project`, `#meeting_notes`)[cite: 2, 3].
* Tags can contain alphanumeric characters, underscores (`_`), and hyphens (`-`)[cite: 3].
* All tags are indexed instantly for search filtering[cite: 1, 3].

### Instant Search & Filtering
The search bar at the top of the navigation pane allows you to quickly locate notes[cite: 1, 2]:
* **Keyword Search**: Type any word or phrase to filter notes matching titles or content case-insensitively[cite: 1, 3].
* **Tag Search**: Type `#` followed by the tag name (e.g., `#tag`) to filter strictly by tag index[cite: 1, 3].

### Real-Time Document Statistics
The status bar at the bottom displays real-time document metrics for the currently opened note[cite: 1, 2]:
* **Words**: Total count of words separated by whitespace[cite: 1].
* **Chars**: Total character count[cite: 1].
* **Lines**: Total line count in the active editor buffer[cite: 1].
* **Est. Reading**: Estimated reading time based on a standard pace of 200 words per minute[cite: 1, 2].

---

## 4. File Storage & Management

* **Transparent Local Files**: Notes are persisted directly as `.md` files under `~/.markdown-notebook/notes/` using their sanitized titles (e.g., `My Note.md`)[cite: 2, 3].
* **Collision Protection**: If a note title duplicates an existing file, the application appends an incremental counter (e.g., `Untitled Note 1.md`) to prevent overwriting existing data[cite: 1, 3].
* **Backups**: You can back up or sync your notebook by copying or tracking the storage folder with tools like Git or cloud drives[cite: 2].

---

## 5. Peer Testing & Verification Guide

Use the following step-by-step checklist to test the product:

| Test Case | Steps | Expected Outcome |
| :--- | :--- | :--- |
| **Note Creation** | Click `+ Note`.[cite: 1] | A note appears in the list, is selected, and opens in the editor.[cite: 1] |
| **Markdown Rendering** | Type `# Heading`, `**bold**`, `~~strike~~`, and a table.[cite: 1] | The preview pane renders all elements accurately within 250ms.[cite: 1, 3] |
| **Keyboard Shortcuts** | Highlight a word and press `Ctrl+B` (or `Cmd+B`).[cite: 2] | The word is wrapped in `**` and renders bold in preview.[cite: 2] |
| **Tag Search** | Add `#cs3227` to a note. Search `#cs3227` in the search bar.[cite: 2, 3] | Only notes containing `#cs3227` remain in the list.[cite: 3] |
| **File Collision** | Click `+ Note` twice without renaming.[cite: 1] | Second note is safely persisted as `Untitled Note 1.md`.[cite: 1, 3] |
| **Auto-Save Verification** | Type content, wait 300ms, close, and re-launch the application.[cite: 1] | All typed content is retained upon reload.[cite: 3] |

---

## 6. Troubleshooting & FAQ

* **Notes not updating in preview**: Ensure the content uses valid Markdown syntax[cite: 1]. The editor debounces updates by 250ms to keep typing responsive[cite: 1, 3].
* **Permission denied on startup**: Ensure your user profile has read and write permissions to the `~/.markdown-notebook/` directory[cite: 2].