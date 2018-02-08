package com.jahnelgroup.jgbay.core.data.auction.category

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface CategoryRepo : CrudRepository<Category, Long> {

}