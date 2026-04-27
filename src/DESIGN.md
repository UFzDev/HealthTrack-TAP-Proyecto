# Design System Strategy: The Focused Architect

## 1. Overview & Creative North Star
The Creative North Star for this design system is **"The Focused Architect."** In a world of cluttered productivity tools, this system rejects the "spreadsheet" aesthetic in favor of high-end editorial clarity. It is designed to feel like a premium, custom-tailored workspace where every task is given room to breathe.

The system breaks the traditional "template" look by utilizing **intentional asymmetry** and **tonal depth**. Rather than boxing information into rigid grids with harsh borders, we use expansive white space and subtle shifts in surface values to guide the user's focus. The result is a productivity tool that feels less like a chore list and more like a high-performance dashboard for the mind.

---

## 2. Colors & Surface Logic
The palette is rooted in deep professional blues and sophisticated grays, utilizing a Material-inspired layering system to communicate importance and state.

### The "No-Line" Rule
To achieve a premium, custom feel, designers are **prohibited from using 1px solid borders** for sectioning or layout containment. Boundaries must be defined solely through background color shifts. For example:
*   A task detail panel (`surface_container_low`) should sit against the main workspace (`surface`) without a stroke.
*   The distinction between a sidebar and a main view is created by the transition from `surface_dim` to `surface_bright`.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers. Use the `surface_container` tiers to create "nested" depth:
*   **Base Layer:** `background` (#f8f9fa).
*   **Secondary Context:** `surface_container` (#eaeff1).
*   **Active Interaction/Cards:** `surface_container_lowest` (#ffffff).
*   **High-Priority Overlays:** `surface_container_highest` (#dbe4e7).

### The "Glass & Gradient" Rule
To move beyond a "generic" blue application, main action points and floating elements should utilize:
*   **Glassmorphism:** Use `surface_container_lowest` at 80% opacity with a `backdrop-blur-md` for floating navigation bars or modal headers.
*   **Signature Textures:** Apply a subtle linear gradient (135° from `primary` to `primary_dim`) on main CTAs. This adds "soul" and visual weight that flat colors lack.

---

## 3. Typography: The Editorial Scale
We employ a dual-font strategy to balance character with utility.

*   **The Voice (Manrope):** Used for `display` and `headline` roles. Manrope’s geometric yet warm proportions provide an authoritative, editorial feel. Use `headline-lg` for daily goals to create a sense of importance.
*   **The Utility (Inter):** Used for `title`, `body`, and `label` roles. Inter is the workhorse of the system, chosen for its extreme legibility at small sizes. 

**Hierarchy Intent:** Use a dramatic contrast between `display-sm` (for empty state headers) and `label-sm` (for metadata). High-contrast typography scales are the hallmark of high-end digital experiences; never let your font sizes get too close to one another.

---

## 4. Elevation & Depth
In this design system, depth is a functional tool, not just an aesthetic choice.

*   **Tonal Layering:** Avoid shadows for static elements. Instead, place a `surface_container_lowest` card on a `surface_container_low` background. This "soft lift" is more sophisticated than a drop shadow.
*   **Ambient Shadows:** When an element must float (e.g., a "Create Task" FAB), use an extra-diffused shadow: `offset-y: 12px`, `blur: 32px`, `color: on_surface` at 6% opacity. This mimics natural light.
*   **The Ghost Border Fallback:** If a border is required for extreme accessibility cases, use a "Ghost Border": the `outline_variant` token at **15% opacity**. Never use 100% opaque lines.

---

## 5. Components

### Buttons
*   **Primary:** Gradient of `primary` to `primary_dim`. Roundedness: `md` (0.375rem). Text: `label-md` in `on_primary`.
*   **Secondary:** `surface_container_highest` background with `on_secondary_container` text. No border.
*   **Tertiary:** Transparent background, `primary` text. Use for low-emphasis actions like "Cancel."

### Input Fields
*   **Structure:** Minimalist. No box container. Use a `surface_container_low` background with a `md` (0.375rem) corner radius.
*   **States:** On focus, the background shifts to `surface_container_lowest` with a subtle `primary` "Ghost Border" (20% opacity).

### Task Cards & Lists
*   **No Dividers:** Prohibit the use of horizontal rules. Separate tasks using the Spacing Scale `3` (0.6rem) or `4` (0.9rem).
*   **Content:** Use `title-sm` for the task name and `body-sm` with `on_surface_variant` for subtasks or notes.
*   **Interactive State:** Upon hover, a task card should shift from `surface` to `surface_container_low`.

### Chips (Priority/Category Tags)
*   **Visuals:** Use `secondary_container` with `on_secondary_container` text. Roundedness: `full`.
*   **Editorial Touch:** Keep labels in `label-sm` uppercase with 0.05em letter spacing for a "pro" look.

---

## 6. Do’s and Don’ts

### Do:
*   **Embrace Asymmetry:** Align high-level stats to the left and actions to the right, using Spacing `16` to create distinct visual zones.
*   **Use Tonal Transitions:** Transition background colors when a user moves from "To Do" to "Completed" to provide subconscious feedback.
*   **Prioritize White Space:** Use Spacing `10` and `12` generously between major sections to prevent cognitive overload.

### Don’t:
*   **Don't use 1px Dividers:** It makes the app look like a legacy system. Use white space.
*   **Don't use Pure Black:** Always use `on_surface` (#2b3437) for text to maintain the professional, soft-minimalist tone.
*   **Don't Over-Shadow:** If more than two elements have shadows on a screen, the design is too heavy. Revert to tonal layering.
*   **Don't use Standard "Blue":** Always stick to the `primary` (#005db5) which has been curated for professional depth.