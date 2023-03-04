package com.hortopan.seloinfo.domain.usecases

import com.hortopan.seloinfo.domain.repository.Repository

class CheckUserRegistrationUseCase(private val repository: Repository) {
    suspend operator fun invoke(): UserRegistrationStatus {
        return if (repository.isAuthorized()) {
            val userData = repository.getUserData()
            val userId = userData.id
            val isUserInUsersCollection = repository.getUsersDocumentsID().contains(userId)

            when {
                isUserInUsersCollection -> UserRegistrationStatus.Registered
                else -> UserRegistrationStatus.NotRegistered
            }
        } else {
            UserRegistrationStatus.NotGoogleAuthenticated
        }
    }
}

enum class UserRegistrationStatus {
    Registered,
    NotRegistered,
    NotGoogleAuthenticated
}