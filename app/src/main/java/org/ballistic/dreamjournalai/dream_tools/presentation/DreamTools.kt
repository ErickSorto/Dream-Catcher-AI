package org.ballistic.dreamjournalai.dream_tools.presentation

import org.ballistic.dreamjournalai.R
import kotlin.reflect.KClass

//dream tools
enum class DreamTools(val title: String, val icon: Int, val description: String, val route: String) {
    AnalyzeDreams("Interpret Multiple Dreams", R.drawable.interpret_vector, "Interpret multiple dreams at once using AI", "analyze"),
    AnalyzeStatistics("Analyze Statistics", R.drawable.outline_analytics_24, "Analyze your dream statistics using AI", "statistics"),
    RandomDreamPicker("Random Dream Picker", R.drawable.baseline_auto_fix_high_24, "Pick a random dream from your dream journal", "randomDreamPicker"),
    RealityCheckReminder("Reality Check Reminder", R.drawable.baseline_mood_24, "Set a reminder to do a reality check", "realityCheckReminder"),
    DreamJournalReminder("Dream Journal Reminder", R.drawable.baseline_lightbulb_24, "Set a reminder to write in your dream journal", "dreamJournalReminder"),
}