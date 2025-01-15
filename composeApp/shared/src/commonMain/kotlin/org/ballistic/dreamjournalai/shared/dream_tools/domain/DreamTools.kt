package org.ballistic.dreamjournalai.shared.dream_tools.domain

import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.dicetool
import dreamjournalai.composeapp.shared.generated.resources.dream_journal_reminder_tool
import dreamjournalai.composeapp.shared.generated.resources.dream_statistic_analyzer_tool
import dreamjournalai.composeapp.shared.generated.resources.dream_world_painter_tool
import dreamjournalai.composeapp.shared.generated.resources.mass_interpretation_tool
import dreamjournalai.composeapp.shared.generated.resources.reality_check_reminder_tool
import org.jetbrains.compose.resources.DrawableResource

//dream tools
enum class DreamTools(val title: String, val icon: DrawableResource, val description: String, val route: String, val enabled: Boolean) {
    AnalyzeDreams("Interpret Multiple Dreams",
        Res.drawable.mass_interpretation_tool, "Interpret multiple dreams at once using AI", "analyze_multiple_dream_details", true),
    RandomDreamPicker("Random Dream Picker", Res.drawable.dicetool, "Pick a random dream from your dream journal", "random_dream_picker", true),
    AnalyzeStatistics("Analyze Statistics", Res.drawable.dream_statistic_analyzer_tool, "Analyze your dream statistics using AI", "analyzeStatistics", false), //Analyze Statistics
    RealityCheckReminder("Reality Check Reminder", Res.drawable.reality_check_reminder_tool, "Set a reminder to do a reality check", "realityCheckReminder", false), //Reality Check Reminder
    DreamJournalReminder("Dream Journal Reminder", Res.drawable.dream_journal_reminder_tool, "Set a reminder to write in your dream journal", "dreamJournalReminder", false), //Dream Journal Reminder
    DREAM_WORLD("Dream World", Res.drawable.dream_world_painter_tool, "Dream World Painter", "dream_world", false), //Dream World Painter
}