package com.crypto.prices.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivitySplashBinding
import com.crypto.prices.utils.TableManagement
/*import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK*/

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var referralUser: Boolean = false
    private var _binding: ActivitySplashBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        // set remote config
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600*24 // refreshes config every 24 hrs
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        // initialize start app sdk
        /*StartAppSDK.init(this, getString(R.string.startapp_app_id), false)
        StartAppAd.disableSplash()*/

        // Initialize Firebase Auth
        auth = Firebase.auth

        // check dynamic link for referral
        /*Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    // Grant the reward to the invitee via its uid
                    if (deepLink!!.getBooleanQueryParameter("invitedby", false)) {
                        val referrerUid = deepLink.getQueryParameter("invitedby")
                        referralUser = true
                        createAnonymousAccountWithReferrerInfo(referrerUid)
                    }
                } else {
                    startSplashTask()
                }
            }
            .addOnFailureListener {
                print(it.localizedMessage)
                startSplashTask()
            }*/
        startSplashTask()
    }

    private fun createAnonymousAccountWithReferrerInfo(referrerUid: String?) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                // Keep track of the referrer in the RTDB.
                val user = Firebase.auth.currentUser
                val userRecord = Firebase.database.reference
                    .child(TableManagement.USERS)
                    .child(user!!.uid)
                userRecord.child("referred_by").setValue(referrerUid)

                // move to sign in activity
                val intent = Intent(this, SignInActivity::class.java)
                intent.putExtra("referralUser", referralUser)
                intent.putExtra("referrerUid", referrerUid)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                print(it.localizedMessage)
                print(it.message)
            }
    }

    private fun startSplashTask() {
        Handler().postDelayed(Runnable {
            // Check if user is signed in (non-null) and update UI accordingly.
            /*val currentUser = auth.currentUser
            if (currentUser != null) {
                // move to MainActivity
                val intent = Intent(this, NavDrawerMainActivity::class.java)
                intent.putExtra("uid", currentUser.uid)
                intent.putExtra("email", currentUser.email)
                intent.putExtra("name", currentUser.displayName)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // move to sign in activity
                val intent = Intent(this, SignInActivity::class.java)
                intent.putExtra("referralUser", referralUser)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }*/
            // move to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }, 3000)
    }
}