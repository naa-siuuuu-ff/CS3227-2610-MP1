First issue that needed to be fixed was a scrolling issue.

**Prompt Summary**

* **User Query:** The user clarified that the `WebView` markdown preview scrolling was laggy, describing a delay where the view moved slightly and continued floating after stopping, even on pre-rendered text without user typing. The user asked if the diagnosis held after sharing a snippet from an earlier project (`ozil.main.MainWindow`) where a native JavaFX `ScrollPane` scrolled smoothly.
* **Follow-up Request:** After verifying that filtering inertia events fixed the floating lag, the user requested adding deceleration back because removing inertia completely felt unnatural and abrupt, while keeping `styles.css` unchanged to avoid technical debt.

**Response Summary**

* **Diagnosis Verification:** Explained that `ScrollPane` manages native JavaFX scene graph nodes directly, whereas `WebView` runs an embedded WebKit engine. The lag was caused by macOS high-frequency trackpad inertia events flooding WebKit's internal event-translation queue and conflicting with WebKit's kinetic physics.
* **Physics-Based Deceleration Solution:**
* Updated `PreviewPane.java` with an event filter on `ScrollEvent.SCROLL` that consumes all events and drops synthetic OS inertia (`event.isInertia()`), bypassing WebKit’s internal gesture queue entirely.
* Implemented custom deceleration using a JavaFX `AnimationTimer` running at 60 FPS to apply an exponential decay formula ($v_t = v_{t-1} \times \text{FRICTION}$) with `FRICTION = 0.92`.
* Translated scrolling via JavaScript using `window.scrollBy(0, pixels)` with `Locale.US` formatting.
* Added a `VELOCITY_STOP_THRESHOLD = 0.2` to cleanly halt the `AnimationTimer` and prevent endless sub-pixel recalculations.
* Injected `scroll-behavior: auto !important;` into the HTML template to stop WebKit from competing with the frame-by-frame physics loop.


I consistently summarise a current chat and start a new chat, to prevent the context window from filling up, but for this particular chat, it completely went off topic, and I realised that the original assignment requirements had been forgotten
