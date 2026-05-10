# Design System Strategy: HealthTrack Community

## 1. Overview & Creative North Star
The HealthTrack Community design system is built to prioritize clarity, calm and trust — qualities important for healthcare-focused applications. The Creative North Star is **"Trusted Clarity."** The visual language aims to reduce cognitive load for users managing chronic conditions by using softer tones, generous space and clear information hierarchy.

This system favors approachable, clinical-friendly aesthetics over generic productivity motifs. We use intentional asymmetry and tonal layering to guide attention while keeping the interface warm and reassuring. The goal is to support consistent monitoring and interpretation of health data in a way that feels professional and empathetic.

---

## 2. Colors & Surface Logic
The palette is oriented toward health and wellbeing: teals and soft greens communicate growth, reliability and clinical trust. Grays are used for neutral surfaces while accent tones convey status (success, warning, neutral). This helps users quickly parse health information without visual noise.

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
To create a friendly, modern clinical UI, main action points and floating elements may use:
*   **Glassmorphism:** Use `surface_container_lowest` at 80% opacity with a `backdrop-blur-md` for floating headers or callouts.
*   **Signature Gradients:** Apply a subtle linear gradient (135°) from `primary_dim` to `primary` on main CTAs to create warmth and emphasis without aggressive contrast.

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
*   **Don't use generic saturated blues:** Prefer the HealthTrack palette (see tokens below) built around `primary` teal/green for brand consistency.

## 7. Core Color Tokens (HealthTrack)
* primary: #0B9A7B (brand teal)
* primary_dim: #12B58A
* primary_dark: #08745E
* background: #F6FAF8
* surface_container: #EEF6F3
* surface_container_lowest: #FFFFFF
* on_primary: #FFFFFF
* on_surface: #2B3437

These tokens should be used across CSS for buttons, highlights and accents. Avoid returning to the original deep-blue tokens to keep the product identity aligned with healthcare and wellbeing.
