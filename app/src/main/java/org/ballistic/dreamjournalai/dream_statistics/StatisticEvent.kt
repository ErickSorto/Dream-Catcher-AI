package org.ballistic.dreamjournalai.dream_statistics

import android.content.Context

sealed class StatisticEvent {
    data object LoadDreams : StatisticEvent()
    data object LoadStatistics : StatisticEvent()
    data object LoadDictionary : StatisticEvent()
}