package com.hortopan.seloinfo.domain.usecases


import com.hortopan.seloinfo.domain.repository.Repository

class CheckAuthorizationUseCase(private val repository: Repository) {
    suspend operator fun invoke(): Boolean = repository.isAuthorized()
}