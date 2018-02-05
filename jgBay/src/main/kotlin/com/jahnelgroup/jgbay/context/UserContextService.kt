package com.jahnelgroup.jgbay.context

import com.jahnelgroup.jgbay.data.user.User

interface UserContextService {

    fun getCurrentUser() : User?
    fun getCurrentUserId() : Long?
    fun getCurrentUsername() : String?

}