package org.ballistic.dreamjournalai.dream_statistics

sealed class StatisticEvent {
    data object LoadDreams : StatisticEvent()
    data object LoadStatistics : StatisticEvent()
}