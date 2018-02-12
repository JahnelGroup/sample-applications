package com.jahnelgroup.jgbay.core.context

import java.time.ZonedDateTime

interface DateTimeService {

    fun getCurrentDateAndTime() : ZonedDateTime

}