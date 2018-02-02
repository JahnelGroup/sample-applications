package com.jahnelgroup.jgbay.auditing

import com.jahnelgroup.jgbay.common.context.UserContextService
import com.jahnelgroup.jgbay.common.time.DateTimeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "userContextProvider", dateTimeProviderRef = "dateTimeProvider")
class AuditConfig(
    private var userContextService: UserContextService,
    private var dateTimeService: DateTimeService
) {

    @Bean
    fun userContextProvider() = AuditorAware<Long> {
        userContextService.getCurrentUserId()
    }

    @Bean
    fun dateTimeProvider() = DateTimeProvider {
        GregorianCalendar.from(dateTimeService.getCurrentDateAndTime())
    }
}