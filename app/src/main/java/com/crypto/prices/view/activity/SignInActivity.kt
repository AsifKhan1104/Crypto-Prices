package com.crypto.prices.view.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.crypto.prices.R
import com.crypto.prices.model.Coin
import com.crypto.prices.model.SpinData
import com.crypto.prices.model.User
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.ui.NavDrawerMainActivity
import kotlinx.android.synthetic.main.activity_signin.buttonLogin
import kotlinx.android.synthetic.main.activity_signin.buttonSignUp
import kotlinx.android.synthetic.main.activity_signin.checkbox
import kotlinx.android.synthetic.main.activity_signin.editTextEmailLogIn
import kotlinx.android.synthetic.main.activity_signin.editTextEmailSignUp
import kotlinx.android.synthetic.main.activity_signin.editTextName
import kotlinx.android.synthetic.main.activity_signin.editTextPwdLogIn
import kotlinx.android.synthetic.main.activity_signin.editTextPwdSignUp
import kotlinx.android.synthetic.main.activity_signin.textViewResetPwd
import kotlinx.android.synthetic.main.activity_signin.textViewTnC
import kotlinx.android.synthetic.main.activity_signin_revamped.*


class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mProgressDialog: ProgressDialog
    private var isReferral = false
    val RC_SIGN_IN: Int = 786
    val TAG = "SignInActivity"

    val authErrors = mapOf(
        "ERROR_INVALID_CREDENTIAL" to R.string.error_login_credential_malformed_or_expired,
        "ERROR_INVALID_EMAIL" to R.string.error_login_invalid_email,
        "ERROR_WRONG_PASSWORD" to R.string.error_login_wrong_password,
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" to R.string.error_login_accounts_exits_with_different_credential,
        "ERROR_EMAIL_ALREADY_IN_USE" to R.string.error_login_email_already_in_use,
        "ERROR_CREDENTIAL_ALREADY_IN_USE" to R.string.error_login_credential_already_in_use,
        "ERROR_USER_NOT_FOUND" to R.string.error_login_user_not_found,
        "ERROR_WEAK_PASSWORD" to R.string.error_login_password_is_weak
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_revamped)

        isReferral = intent.getBooleanExtra("referralUser", false)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn()
        //createUser()

        // tnc
        textViewTnC.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@SignInActivity, TnCActivity::class.java)
                startActivity(intent)
            }
        })

        // reset pwd
        textViewResetPwd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@SignInActivity, ResetPwdActivity::class.java)
                startActivity(intent)
            }

        })

        // sign in with google
        sign_in_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        try {
            if (isReferral) {
                auth.currentUser!!
                    .linkWithCredential(credential)
                    .addOnSuccessListener {
                        // now grant invitee the reward
                        rewardInvitee()
                        // Complete any post sign-up tasks here.
                        var name: String?
                        if (auth?.currentUser?.displayName != null) {
                            name = auth?.currentUser?.displayName
                        } else {
                            var temp = auth?.currentUser?.email!!
                            name = temp.substring(0, temp.indexOf("@"))
                        }
                        signUpProcess(name!!, intent.getBooleanExtra("referralUser", false))
                    }
            } else {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = auth.currentUser
                            var dpName: String? = user?.displayName
                            // Sign in success, start sign up process
                            if (dpName == null) {
                                dpName = user?.email!!.substring(0, user?.email!!.indexOf("@"))
                            }
                            // check if user exists in firebase DB or it's new user
                            Firebase.database.reference.child(TableManagement.USERS)
                                .child(user!!.uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // user exists
                                            signUpProcess(dpName, false, false)
                                        } else {
                                            // User does not exist.
                                            signUpProcess(dpName, false, true)
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            try {
                                // If sign in fails, display a message to the user.
                                val errorCode =
                                    (task.exception as FirebaseAuthException).errorCode
                                val errorMessage =
                                    authErrors[errorCode] ?: R.string.error_login_default_error
                                Utility.showToast(this@SignInActivity, getString(errorMessage))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun signIn() {
        buttonLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email = editTextEmailLogIn.text.toString().trim()
                val pwd = editTextPwdLogIn.text.toString().trim()

                if (email.isBlank()) {
                    editTextEmailLogIn.setError("Email is blank")
                } else if (!Utility.validateEmail(email)) {
                    editTextEmailLogIn.setError("Please enter valid email")
                } else if (pwd.isBlank()) {
                    editTextPwdLogIn.setError("Password is blank")
                } else {
                    showProgressDialog()
                    auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener { task ->
                        dismissProgressDialog()
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            //  move to MainActivity
                            val intent =
                                Intent(applicationContext, NavDrawerMainActivity::class.java)
                            intent.putExtra("uid", user?.uid)
                            intent.putExtra("email", user?.email)
                            intent.putExtra("name", user?.displayName)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                val errorCode = (task.exception as FirebaseAuthException).errorCode
                                val errorMessage =
                                    authErrors[errorCode] ?: R.string.error_login_default_error
                                Utility.showToast(this@SignInActivity, getString(errorMessage))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun createUser() {
        buttonSignUp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                editTextName.visibility = View.VISIBLE

                val name = editTextName.text.toString().trim()
                val email = editTextEmailSignUp.text.toString().trim()
                val pwd = editTextPwdSignUp.text.toString().trim()

                if (name.isBlank()) {
                    editTextName.setError("Name is blank")
                } else if (email.isBlank()) {
                    editTextEmailSignUp.setError("Email is blank")
                } else if (!Utility.validateEmail(email)) {
                    editTextEmailSignUp.setError("Please enter valid email")
                } else if (pwd.isBlank()) {
                    editTextPwdSignUp.setError("Password is blank")
                } else if (!checkbox.isChecked) {
                    Utility.showToast(this@SignInActivity, "Please accept Terms & Conditions.")
                } else {
                    showProgressDialog()
                    if (intent.getBooleanExtra("referralUser", false)) {
                        auth.currentUser!!
                            .linkWithCredential(getCredential(email, pwd))
                            .addOnSuccessListener {
                                dismissProgressDialog()
                                // now grant invitee the reward
                                rewardInvitee()
                                // Complete any post sign-up tasks here.
                                signUpProcess(name, intent.getBooleanExtra("referralUser", false))
                            }
                    } else {
                        auth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener { task ->
                                dismissProgressDialog()
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    signUpProcess(name, false)
                                } else {
                                    try {
                                        // If sign in fails, display a message to the user.
                                        val errorCode =
                                            (task.exception as FirebaseAuthException).errorCode
                                        val errorMessage =
                                            authErrors[errorCode]
                                                ?: R.string.error_login_default_error
                                        Utility.showToast(
                                            this@SignInActivity,
                                            getString(errorMessage)
                                        )
                                    } catch (ex: Exception) {
                                        ex.printStackTrace()
                                    }
                                }
                            }
                    }
                }
            }
        })
    }

    private fun rewardInvitee() {
        val databaseRef =
            Firebase.database.reference.child(TableManagement.USERS)
                .child(intent.getStringExtra("referrerUid").toString())

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val invitee = snapshot.getValue(User::class.java)
                val rewardUser = User(
                    invitee?.email,
                    Coin(
                        invitee?.coin?.lifetimeCoins?.plus(CoinManagement.getReferralCoins()),
                        0.0,
                        invitee?.coin?.currentCoins?.plus(CoinManagement.getReferralCoins())
                    )
                )
                databaseRef.setValue(rewardUser)
            }

            override fun onCancelled(error: DatabaseError) {
                // Getting Post failed, log a message
                print(error.toException())
            }
        }
        databaseRef.addListenerForSingleValueEvent(postListener)
    }

    private fun signUpProcess(name: String, referralStatus: Boolean, newUser: Boolean) {
        // set display name of user in firebase auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
        auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // nothing to do
            }
        }
        val user = auth.currentUser
        if (newUser) {
            // gift user 10 SHIB as account creation reward
            // Write it to the database
            val userData =
                User(
                    user?.email,
                    Coin(CoinManagement.getSignUpCoins(), 0.0, CoinManagement.getSignUpCoins())
                )
            Firebase.database.reference.child(TableManagement.USERS).child(user!!.uid)
                .setValue(
                    userData
                )

            // create default data to spin data table
            val spinData = SpinData(user.email, 0.0)
            Firebase.database.reference.child(TableManagement.SPIN_EARNING).child(user.uid)
                .setValue(spinData)
        }

        //  move to MainActivity
        val intent =
            Intent(applicationContext, NavDrawerMainActivity::class.java)
        intent.putExtra("uid", user?.uid)
        intent.putExtra("email", user?.email)
        intent.putExtra("name", name)
        intent.putExtra("newUser", newUser)
        intent.putExtra("referralUser", referralStatus)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun signUpProcess(name: String, referralStatus: Boolean) {
        // set display name of user in firebase auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
        auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // nothing to do
            }
        }
        // gift user 10 SHIB as account creation reward
        // Write it to the database
        val user = auth.currentUser
        val userData =
            User(
                user?.email,
                Coin(CoinManagement.getSignUpCoins(), 0.0, CoinManagement.getSignUpCoins())
            )
        Firebase.database.reference.child(TableManagement.USERS).child(user!!.uid)
            .setValue(
                userData
            )

        //  move to MainActivity
        val intent =
            Intent(applicationContext, NavDrawerMainActivity::class.java)
        intent.putExtra("uid", user?.uid)
        intent.putExtra("email", user?.email)
        intent.putExtra("name", name)
        intent.putExtra("newUser", true)
        intent.putExtra("referralUser", referralStatus)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun getCredential(email: String, password: String): AuthCredential {
        return EmailAuthProvider.getCredential(email, password)
    }

    fun showProgressDialog() {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Loading. Please wait...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
    }

    fun dismissProgressDialog() {
        if (mProgressDialog.isShowing && !this.isFinishing)
            mProgressDialog.dismiss()
    }
}