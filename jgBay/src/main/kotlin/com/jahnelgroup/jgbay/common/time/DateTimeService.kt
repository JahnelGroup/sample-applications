package com.jahnelgroup.jgbay.common.time

import java.time.ZonedDateTime

interface DateTimeService {

    fun getCurrentDateAndTime() : ZonedDateTime

}