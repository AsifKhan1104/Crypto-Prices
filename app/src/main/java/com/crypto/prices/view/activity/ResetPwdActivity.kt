package com.crypto.prices.view.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.crypto.prices.R
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.activity_reset_pwd.*
import kotlinx.android.synthetic.main.activity_reset_pwd.editTextEmailLogIn
import kotlinx.android.synthetic.main.activity_signin.*

class ResetPwdActivity : AppCompatActivity() {
    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pwd)

        // Initialize Firebase Auth
        val auth: FirebaseAuth = Firebase.auth

        buttonReset.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email = editTextEmailLogIn.text.toString()
                if (email.isBlank()) {
                    editTextEmailLogIn.setError("Email is blank")
                } else if (!Utility.validateEmail(email)) {
                    editTextEmailSignUp.setError("Please enter valid email")
                } else {
                    // send passowrd reset mail
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                textViewResetSent.visibility = View.VISIBLE
                                Utility.showLongToast(this@ResetPwdActivity, "A password reset email is sent to you. Kindly check your email id for further actions.")
                                finish()
                            } else {
                                textViewResetSent.visibility = View.VISIBLE
                                textViewResetSent.text = task.exception?.localizedMessage
                            }
                        }
                }
            }

        })
    }
}