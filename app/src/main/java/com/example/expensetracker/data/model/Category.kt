package com.example.expensetracker.data.model

import androidx.compose.ui.graphics.Color

/**
 * Fixed set of spending categories. Each carries its own accent color plus a two-letter monogram
 * (the design renders a colored rounded-square badge with the monogram instead of an icon) and a
 * short label used in tight layouts such as the keypad grid.
 */
enum class Category(
    val label: String,
    val mono: String,
    val short: String,
    val color: Color,
    val type: TransactionType = TransactionType.EXPENSE
) {
    GROCERY("Grocery", "Gr", "Grocery", Color(0xFF4F8A5B)),
    FRESH_PRODUCT("Fresh Product", "Fp", "Fresh", Color(0xFF6FA86B)),
    FRUITS("Fruits", "Fr", "Fruits", Color(0xFFE08A3C)),
    MEAT("Meat", "Mt", "Meat", Color(0xFFB0463C)),
    MEDICINE("Medicine", "Rx", "Medicine", Color(0xFFC2453E)),
    EDUCATION("Education", "Ed", "Education", Color(0xFF3D6FA8)),
    TRAINING("Training", "Tr", "Training", Color(0xFF5B5FC7)),
    HOUSE_RENT("House Rent", "Re", "Rent", Color(0xFF5E6B7A)),
    ELECTRICITY("Electricity", "El", "Power", Color(0xFFD89A22)),
    GAS("Gas", "Ga", "Gas", Color(0xFF3E8E8A)),
    UTILITY("Utility", "Ut", "Utility", Color(0xFF8A8578)),
    CLOTHING("Clothing", "Cl", "Clothing", Color(0xFFC2618E)),
    ELECTRONICS("Electronics & Gadgets", "Ge", "Gadgets", Color(0xFF7A5BC7)),
    TRANSPORTATION("Transportation", "Tx", "Transport", Color(0xFF2F95B5)),
    GIFT_DONATION("Gift & Donation", "Gi", "Gifts", Color(0xFFD06A86)),
    HOSPITALITY("Hospitality", "Ho", "Hosting", Color(0xFFB07A47));

    companion object {
        fun forType(type: TransactionType): List<Category> = entries.filter { it.type == type }
    }
}
