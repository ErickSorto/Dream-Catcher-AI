package org.ballistic.dreamjournalai.dream_tools.domain

import org.ballistic.dreamjournalai.R

//dream tools
enum class DreamTools(val title: String, val icon: Int, val description: String, val route: String, val enabled: Boolean) {
    AnalyzeDreams("Interpret Multiple Dreams",
        R.drawable.mass_interpretation_tool, "Interpret multiple dreams at once using AI", "analyze_multiple_dream_details", true),
    RandomDreamPicker("Random Dream Picker", R.drawable.dicetool, "Pick a random dream from your dream journal", "random_dream_picker", true),
    AnalyzeStatistics("Analyze Statistics", R.drawable.dream_statistic_analyzer_tool, "Analyze your dream statistics using AI", "analyzeStatistics", false), //Analyze Statistics
    RealityCheckReminder("Reality Check Reminder", R.drawable.reality_check_reminder_tool, "Set a reminder to do a reality check", "realityCheckReminder", false), //Reality Check Reminder
    DreamJournalReminder("Dream Journal Reminder", R.drawable.dream_journal_reminder_tool, "Set a reminder to write in your dream journal", "dreamJournalReminder", false), //Dream Journal Reminder
    DREAM_WORLD("Dream World", R.drawable.dream_world_painter_tool, "Dream World Painter", "dream_world", false), //Dream World Painter
}