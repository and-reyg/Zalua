package com.hortopan.seloinfo.domain.usecases

import com.hortopan.seloinfo.domain.repository.Repository


import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class SignUpByGoogleUseCase(private val repository: Repository) {
    suspend operator fun invoke(account: GoogleSignInAccount) = repository.signUpByGoogle(account)
}