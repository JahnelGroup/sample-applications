package com.jahnelgroup.jgbay.core.initializer

import com.jahnelgroup.jgbay.core.data.user.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * Helper component that sends existing data found in the database
 * over to the search module upon start-up.
 *
 * This is useful for data that is loaded into the sample application
 * from SQL initialization scripts.
 */
@Component
class SearchInitializer(var userRepo: UserRepo) : ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    lateinit var appEventPub: ApplicationEventPublisher

    @Value("\${service.search.load-all-on-boot}")
    var loadOnBoot = false

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        if( loadOnBoot ){
            userRepo.findAll().forEach({
                appEventPub.publishEvent(SearchInitEvent(it))
            })
        }
    }



}