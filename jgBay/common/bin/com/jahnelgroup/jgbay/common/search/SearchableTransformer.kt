package com.jahnelgroup.jgbay.common.search

interface SearchableTransformer<from, out To>{

    fun from(from: Any): To

}