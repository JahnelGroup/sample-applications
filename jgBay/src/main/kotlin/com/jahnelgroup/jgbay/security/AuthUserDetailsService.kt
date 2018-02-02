package com.jahnelgroup.jgbay.security

import com.jahnelgroup.jgbay.data.user.UserRepo
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService(
    val userRepo : UserRepo
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userRepo.findByUsername(username).map {
            AuthenticatedUser(it, it.username, "password", emptySet())
        }.orElseThrow({ UsernameNotFoundException("Username not found") })
}