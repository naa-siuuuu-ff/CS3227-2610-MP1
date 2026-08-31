# User Guide: Markdown Desktop Notebook

## 1. Introduction
* Overview of the Markdown Desktop Notebook utility.
* Key design priorities: distraction-free writing, instant search, local-first privacy.

## 2. Quick Start
* System requirements (Java 17 runtime).
* Launching the application.
* Default storage location (`~/.markdown-notebook/notes/`).

## 3. Core Features
* **Three-Pane Layout**: Navigation sidebar, active Markdown editor, synchronized live preview.
* **Tagging System**: Using inline `#tags` in note bodies and headers.
* **Instant Search**: Filtering notes by keyword, title, or `#tag`.
* **Auto-Save**: Background persistence with dirty-state indicator.
* **Markdown Support**: Headers, lists, code blocks, GitHub-Flavored Markdown tables, and strikethroughs.
* **Real-time Document Statistics**: The status bar at the bottom displays real-time metrics for the active note, including total word count, character count, line count, and estimated reading time (calculated at 200 words/minute).
* **Formatting Shortcuts**: Format content rapidly in the editor using native keyboard shortcuts:
  * `Ctrl+B` (or `Cmd+B` on macOS): Wraps selected text in `**bold**`.
  * `Ctrl+I` (or `Cmd+I` on macOS): Wraps selected text in `*italics*`.
  * `Ctrl+K` (or `Cmd+K` on macOS): Wraps selected text in `` `inline code` ``.
  * If no text is selected, the formatting markers are inserted and the cursor is automatically positioned between them.

## 4. File Management & Local Storage
* How notes map to `.md` files on disk.
* File naming conventions and conflict handling.
* Backing up and migrating your notebook directory.

## 5. Troubleshooting & FAQ
* Handling file permission issues.
* Recovering external edits.