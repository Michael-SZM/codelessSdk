package com.iyx.codeless.net

import android.support.annotation.StringDef

@StringDef(value = [EventType.CLICK, EventType.INPUT, EventType.PAGEVIEW])
@Retention(AnnotationRetention.SOURCE)
internal annotation class EventType{
    companion object{
        const val CLICK = "click"
        const val INPUT = "input"
        const val PAGEVIEW = "pageview"
    }
}

