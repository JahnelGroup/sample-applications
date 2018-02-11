package com.jahnelgroup.jgbay.core.search.initializer

import com.jahnelgroup.jgbay.core.search.integration.SearchUpdateEvent
import org.springframework.context.ApplicationEvent

class SearchInitEvent(any: Any) : ApplicationEvent(any), SearchUpdateEvent