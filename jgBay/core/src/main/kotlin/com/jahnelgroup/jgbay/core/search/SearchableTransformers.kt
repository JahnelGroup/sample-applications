package com.jahnelgroup.jgbay.core.search

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.integration.transformer.AbstractTransformer
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class SearchableTransformers : AbstractTransformer() {

    @Autowired
    lateinit var appContext: ApplicationContext

    override fun doTransform(message: Message<*>): Any? {
        var from = message.payload
        var transformer = appContext.getBean(from.javaClass.getAnnotation(Searchable::class.java).transformRef)
        return with(transformer as SearchableTransformer<Any, Any>){
            transformer.from(from)!!
        }
    }



}