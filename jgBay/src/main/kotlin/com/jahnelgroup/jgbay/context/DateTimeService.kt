package com.jahnelgroup.jgbay.context

import java.time.ZonedDateTime

interface DateTimeService {

    fun getCurrentDateAndTime() : ZonedDateTime

}