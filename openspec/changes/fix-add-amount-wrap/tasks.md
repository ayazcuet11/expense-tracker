# Tasks: fix-add-amount-wrap

## 1. Amount display fix

- [x] 1.1 In `ui/screens/add/AddTransactionScreen.kt`, compute the amount font size from `state.displayAmount.length` (≤9 → 52.sp, 10 → 46.sp, ≥11 → 42.sp) with a comment noting the coupling to `pressDigit`'s 9-digit cap, and apply it to the amount `Text`.
- [x] 1.2 Add `maxLines = 1` and `softWrap = false` to the same `Text` as the hard single-line guarantee.
- [x] 1.3 Remove `letterSpacing = (-1).sp` from the amount `Text` — the actual root cause: negative tracking across the Bengali-fallback ৳ + digit run corrupts line-width measurement and breaks the last glyph onto a phantom line even with wrapping disabled. (Discovered during verification; ZWNJ-between-৳-and-digits was tested as an alternative and reverted as ineffective.)

## 2. Verification

- [x] 2.1 Build: `./gradlew :app:assembleDebug`.
- [x] 2.2 On emulator (`:app:installDebug`): enter `479999999` — full amount renders on one line, no wrap/overlap; enter `4799999.99` — fits on one line; a short amount like `479` still renders at the original 52.sp size.
