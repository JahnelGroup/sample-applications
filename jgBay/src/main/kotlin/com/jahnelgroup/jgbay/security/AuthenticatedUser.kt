package com.jahnelgroup.jgbay.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class AuthenticatedUser(
    private var user: com.jahnelgroup.jgbay.data.user.User,
    username: String,
    password: String,
    authorities: Set<out GrantedAuthority>
) : User(username, password, authorities) {

    fun getUser(): com.jahnelgroup.jgbay.data.user.User = user

}