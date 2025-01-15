package org.ballistic.dreamjournalai.shared.dream_tools.domain.event

sealed class ToolsEvent {
        data object LoadDreams : ToolsEvent()
        data object LoadStatistics : ToolsEvent()
        data object LoadDictionary : ToolsEvent()
        data object ChooseRandomDream : ToolsEvent()
}