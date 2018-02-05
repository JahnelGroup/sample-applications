package com.jahnelgroup.jgbay.context

import com.jahnelgroup.jgbay.data.user.User
import com.jahnelgroup.jgbay.data.user.UserRepo
import com.jahnelgroup.jgbay.security.AuthenticatedUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SpringSecurityUserContext(private var userRepo: UserRepo): UserContextService {

    /**
     * Returns the current authenticated User by looking it up from
     * the database as an attached Entity.
     */
    override fun getCurrentUser(): User? =
        userRepo.findOne(getCurrentUserId()!!)

    /**
     * Returns the current authenticated User username provided
     * by Spring Security and the SecurityContextHolder.
     */
    override fun getCurrentUsername() : String? =
            SecurityContextHolder.getContext().authentication.name.orEmpty()

    /**
     * Returns the current authenticated User primary database ID provided
     * by Spring Security and the SecurityContextHolder.
     */
    override fun getCurrentUserId(): Long? {
        var p = SecurityContextHolder.getContext().authentication.principal
        if(p is AuthenticatedUser){
            return p.getUser().id
        }
        return null
    }

}