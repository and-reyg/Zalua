package com.hortopan.seloinfo.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.hortopan.seloinfo.R
import com.hortopan.seloinfo.data.RepositoryImpl
import com.hortopan.seloinfo.databinding.ActivityAuthorisationBinding
import com.hortopan.seloinfo.domain.repository.Repository
import com.hortopan.seloinfo.domain.usecases.CheckAuthorizationUseCase
import com.hortopan.seloinfo.domain.usecases.CheckUserRegistrationUseCase
import com.hortopan.seloinfo.domain.usecases.SignUpByGoogleUseCase
import com.hortopan.seloinfo.domain.usecases.UserRegistrationStatus
import kotlinx.coroutines.*

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorisationBinding
    private lateinit var repository: Repository
    private lateinit var checkAuthorizationUseCase: CheckAuthorizationUseCase
    private lateinit var checkUserRegistrationUseCase: CheckUserRegistrationUseCase
    private lateinit var signUpByGoogleUseCase: SignUpByGoogleUseCase
    private lateinit var googleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorisationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dependencies
        repository = RepositoryImpl()
        checkAuthorizationUseCase = CheckAuthorizationUseCase(repository)
        checkUserRegistrationUseCase = CheckUserRegistrationUseCase(repository)
        signUpByGoogleUseCase = SignUpByGoogleUseCase(repository)

        // Create GoogleSignInOptions
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Show logo for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Check user registration status
            checkUserRegistrationStatus()
        }, 2000)
    }

    private fun checkUserRegistrationStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userRegistrationStatus = withContext(Dispatchers.IO) {
                    checkUserRegistrationUseCase()
                }

                when (userRegistrationStatus) {
                    UserRegistrationStatus.Registered -> {
                        Log.d("TAG", "Зарегестрирован")
                        openMainActivity()
                    }
                    UserRegistrationStatus.NotRegistered -> {
                        Log.d("TAG", "Не Зарегестрирован Открыть Выбор Села")
                        openChooseLocationActivity()
                    }
                    UserRegistrationStatus.NotGoogleAuthenticated -> {
                        Log.d("TAG", "Не авторизован")
                        openRegistrationForm()
                    }
                }
            } catch (exception: Exception) {
                Log.e("TAG", "Error checking user registration status: $exception")
            }
        }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openChooseLocationActivity() {
        startActivity(Intent(this, ChooseLocationActivity::class.java))
        finish()
    }

    private fun openRegistrationForm() {
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                CoroutineScope(Dispatchers.IO).launch {
                    signUpByGoogleUseCase(account)
                    openChooseLocationActivity()
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
