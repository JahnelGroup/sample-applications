package com.jahnelgroup.jgbay.common.search

/**
 * The primary mapper to transform an Entity to its corresponding @Document resource
 * in the search service.
 */
interface SearchableTransformer<from, out To>{

    fun from(from: Any): To

}