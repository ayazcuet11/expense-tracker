# Design: fix-add-amount-wrap

## Context

`AddTransactionScreen.kt` renders `state.displayAmount` ("৳" + typed digits) at a hardcoded `fontSize = 52.sp`. Entering a 9th digit made the last digit render on a second line overlapping the ৳ sign.

**Root cause (found during implementation, not what was originally hypothesized):** the amount `Text` used `letterSpacing = (-1).sp`. Negative letter-spacing across a font-fallback boundary — ৳ (U+09F3) comes from the Bengali fallback font while digits come from the default font — makes Android's computed line width fractionally smaller than the painted width. At 9 digits the accumulated error exceeds the threshold and the layout breaks the last glyph onto a phantom second line. This is why the artifact:
- appeared at exactly 9 digits regardless of which digit was typed,
- appeared "sooner" with wide glyphs (4/7/9) than narrow ones (1) in the user's experience,
- **survived `maxLines = 1` + `softWrap = false`** (verified on emulator: identical APK checksums, restarted process, artifact persisted),
- was *not* fixed by inserting U+200C between ৳ and digits (Bengali-shaper contextual-forms hypothesis, tested and rejected).

Removing the negative letter-spacing eliminated the artifact immediately.

## Goals / Non-Goals

**Goals:**
- The amount display never wraps or overlaps, for every reachable input (up to 9 digits + decimal point).
- Deterministic rendering — no layout-feedback loops or flicker.

**Non-Goals:**
- No change to input validation or the 9-digit cap.
- No Compose BOM upgrade (stays 2024.06.00).

## Decisions

1. **Remove `letterSpacing = (-1).sp` from the amount Text.** This is the actual fix. Visual impact on short amounts is a ~2% width increase — imperceptible at 52.sp. Alternative — keeping the tracking and working around the measurement bug — rejected: every workaround tested (single-line constraints, ZWNJ) failed; the tracking is a subtle style nicety not worth a rendering bug.

2. **Keep `maxLines = 1` + `softWrap = false`.** Added during investigation; retained as belt-and-braces so any future regression degrades to edge clipping instead of the two-line overlap.

3. **Keep the length-based font step-down** (display length ≤ 9 → 52.sp, 10 → 46.sp, ≥ 11 → 42.sp). With the letter-spacing gone, 9 digits would fit at 52.sp on this device, but the step-down keeps the max-length string comfortably inside the width, including at larger accessibility font scales. Deterministic (pure function of string length), commented with its coupling to `pressDigit`'s 9-digit cap.

4. **`displayAmount` unchanged** ("৳" + digits). The ZWNJ experiment was reverted — it had no effect and would have polluted the string length used by the step-down.

## Risks / Trade-offs

- [Short amounts lose the -1.sp tracking] → ~2% wider; visually indistinguishable at this size.
- [Very large accessibility font scales could still exceed width] → degrades to edge clipping (never overlap) thanks to `maxLines = 1` + `softWrap = false`.

## Migration Plan

UI-only; no data or API impact. Rollback = revert.

## Open Questions

None.
