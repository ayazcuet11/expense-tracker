# Proposal: fix-add-amount-wrap

## Why

On the Add expense screen, entering a 9th digit made the last digit render on a second line overlapping the ৳ sign. Root cause (established experimentally during implementation): the amount `Text`'s `letterSpacing = (-1).sp` corrupts Android's line-width computation across the font-fallback boundary between the Bengali ৳ glyph and the digit run, forcing the final glyph onto a phantom line — regardless of available screen width, and even with wrapping explicitly disabled. Wide digit glyphs (4, 7, 9) accumulate the error faster than narrow ones, which is why the bug appeared tied to those digits.

## What Changes

- The negative letter-spacing is removed from the amount display (the actual fix; visually imperceptible).
- The amount display becomes strictly single-line (`maxLines = 1`, `softWrap = false`) as defense in depth.
- The font size steps down as the amount grows, so the full 9-digit maximum (plus optional decimal point) fits comfortably, including at larger font scales.
- No change to input rules: the existing 9-digit / two-decimal cap in `AddTransactionViewModel.pressDigit` stays as-is.

## Capabilities

### New Capabilities

- `add-amount-display`: How the Add/Edit screen renders the in-progress amount — single-line guarantee and size adaptation for long values.

### Modified Capabilities

None — existing specs (`activity-day-totals`, `transaction-amount-display`) are unaffected.

## Impact

- `ui/screens/add/AddTransactionScreen.kt` — amount `Text` loses `letterSpacing = (-1).sp` and gains a length-based font size, `maxLines = 1`, and `softWrap = false`.
- No ViewModel, data, navigation, or theme changes. Compose BOM stays at 2024.06.00 (the newer auto-size Text API is unavailable there, hence the deterministic step-down approach).
