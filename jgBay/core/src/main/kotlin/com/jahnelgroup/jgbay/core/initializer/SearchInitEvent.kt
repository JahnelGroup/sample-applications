package com.jahnelgroup.jgbay.core.initializer

import com.jahnelgroup.jgbay.common.search.integration.event.SearchCreateEvent
import org.springframework.context.ApplicationEvent

class SearchInitEvent(any: Any) : ApplicationEvent(any), SearchCreateEvent