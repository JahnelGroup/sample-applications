package com.jahnelgroup.jgbay.context

import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class CurrentTimeDateTimeService : DateTimeService {

    override fun getCurrentDateAndTime(): ZonedDateTime {
        return ZonedDateTime.now()
    }

}