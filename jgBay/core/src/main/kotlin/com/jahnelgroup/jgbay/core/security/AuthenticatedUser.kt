package com.jahnelgroup.jgbay.core.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class AuthenticatedUser(
        private var user: com.jahnelgroup.jgbay.core.data.user.User,
        username: String,
        password: String,
        authorities: Set<out GrantedAuthority>
) : User(username, password, authorities) {

    fun getUser(): com.jahnelgroup.jgbay.core.data.user.User = user

}