package com.jahnelgroup.jgbay.core.context

import com.jahnelgroup.jgbay.core.data.user.User

interface UserContextService {

    fun getCurrentUser() : User?
    fun getCurrentUserId() : Long?
    fun getCurrentUsername() : String?

}