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

Here is an example of a long chat repeatedly forgetting simple context even after I corrected it multiple times

# Project Context & Agent Handoff

## 1. System Overview
* Domain: Clinic Management Web Application (Project Sabai - Computing Wing).
* Purpose: Operational management of rural mobile healthcare clinics in Cambodia (patient intake, consult tracking, clinical domains).
* Core Architecture: Full-stack TypeScript application built around type-safe client-server interfaces.

## 2. Tech Stack
* Framework: Next.js (App Router / React)
* API Layer: tRPC (end-to-end type safety between frontend and backend routers)
* Validation: Zod schemas for input/output payloads and mutation arguments
* Database & ORM: Drizzle ORM managing relational schemas and query generation
* Testing: Vitest for unit and router-level integration tests (utilizing vi.fn, vi.mock)
* UI & Layout: Tailwind CSS / CSS Grid / modern responsive components

## 3. Key Completed Modules & Subsystems
* Puberty Tracking Module: Implemented database schema and tRPC router procedures for screening, milestone monitoring, and patient updates (PR closed Issue #140).
* Navigation & Layout: Responsive sidebar integration and layout structural updates for clinic role-based workflows.
* Data Layer Integrations: Type-safe router procedures validated via Zod, integrated directly with Drizzle models.

## 4. Engineering Guardrails for Incoming Agent
* Context Verification First: Before refactoring, writing queries, or extending routers, explicitly request the existing schema files, router files, or component implementations to avoid assumptions and prevent regressions.
* Type Safety: Maintain strict end-to-end typing via tRPC and Zod; avoid unvalidated payloads or raw casts.
* Mock Verification: When writing router tests in Vitest, mock database dependencies accurately to verify mutation side-effects and error handling.

I am doing a java project on maven, and it is giving me context for a completely unrelated project.
And just before this, it gave me perfect code and tests (I could not find any mistakes, maybe I didnt see them properly?)