package com.jahnelgroup.jgbay.data.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepo : CrudRepository<User, Long>{

    @Query("select u from User u where u.username = ?#{principal.username}")
    fun findMe(): Optional<User>

    fun findByUsername(@Param("username") username : String) : Optional<User>

}