package org.ballistic.dreamjournalai.dream_tools.presentation

import org.ballistic.dreamjournalai.R
import kotlin.reflect.KClass

//dream tools
enum class DreamTools(val title: String, val icon: Int, val description: String, val route: String) {
    AnalyzeDreams("Interpret Multiple Dreams",
        R.drawable.mass_dream_interpretation_icon, "Interpret multiple dreams at once using AI", "analyze_multiple_dream"),
    AnalyzeStatistics("Analyze Statistics", R.drawable.outline_analytics_24, "Analyze your dream statistics using AI", "analyzeStatistics"),
    RandomDreamPicker("Random Dream Picker", R.drawable.baseline_casino_24, "Pick a random dream from your dream journal", "random_dream_picker"),
    RealityCheckReminder("Reality Check Reminder", R.drawable.reality_check_reminder_icon, "Set a reminder to do a reality check", "realityCheckReminder"),
    DreamJournalReminder("Dream Journal Reminder", R.drawable.baseline_lightbulb_24, "Set a reminder to write in your dream journal", "dreamJournalReminder"),
}