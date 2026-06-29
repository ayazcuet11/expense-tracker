package com.example.expensetracker.ui.navigation

/** Top-level navigation graph routes. */
object Routes {
    const val HOME = "home"
    const val ACTIVITY = "activity"
    const val STATS = "stats"
    const val INSIGHTS = "insights"
    const val ADD = "add"
    const val ADD_WITH_ID = "add?id={id}"

    fun add(id: Long? = null) = if (id == null) "add" else "add?id=$id"

    /** Routes that show the bottom bar (everything except the add overlay). */
    val mainRoutes = setOf(HOME, ACTIVITY, STATS, INSIGHTS)
}
