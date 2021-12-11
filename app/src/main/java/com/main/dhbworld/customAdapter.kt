package com.main.dhbworld

import android.content.Context
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import java.util.*

data class MyEvent(
        val id: Long,
        val title: String,
        val startTime: Calendar,
        val endTime: Calendar
)

class MyCustomSimpleAdapter : WeekView.SimpleAdapter<MyEvent>() {

    fun onCreateEntity(
            context: Context,
            item: MyEvent
    ): WeekViewEntity {
        return WeekViewEntity.Event.Builder(item)
                .setId(item.id)
                .setTitle(item.title)
                .setStartTime(item.startTime)
                .setEndTime(item.endTime)
                .build()
    }
}