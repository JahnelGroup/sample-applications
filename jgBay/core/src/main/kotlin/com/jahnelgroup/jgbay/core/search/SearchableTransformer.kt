package com.jahnelgroup.jgbay.core.search

interface SearchableTransformer<from, out To>{

    fun from(from: Any): To

}