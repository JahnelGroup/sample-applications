package com.jahnelgroup.jgbay.common.search

interface SearchableTransformer<in From>{

    fun from(from: From): Any

}