package com.crypto.prices.view.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.crypto.prices.BuildConfig
import com.crypto.prices.R
import com.crypto.prices.model.Coin
import com.crypto.prices.model.User
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.activity.ProFeaturesActivity
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.startapp.sdk.adsbase.adlisteners.VideoListener
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.metadata.MetaData
import kotlinx.android.synthetic.main.fragment_earn.*

class FragmentEarn : Fragment(), View.OnClickListener, VideoListener, IUnityAdsListener {
    private val TAG = FragmentEarn::class.java.simpleName
    private var mUser: User? = null
    private var mUid: String? = null
    private lateinit var mStartAppAd: StartAppAd
    private lateinit var mProgressDialog: ProgressDialog
    private val testMode = false
    private val surfacingId = "Rewarded_Video"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater!!.inflate(R.layout.fragment_earn, container, false)
    }

    companion object {
        fun newInstance(): FragmentEarn = FragmentEarn()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // fetch uid of user
        mUid = activity?.intent?.getStringExtra("uid")
        // set coin balance from firebase database
        setData()

        // initialize startapp sdk
        StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);
        initStartAppAd()

        // Initialize the SDK:
        UnityAds.initialize(activity, getString(R.string.unity_ad_id), testMode)
        val metaData = MetaData(requireContext())
        metaData.set("privacy.mode", "mixed") // This is a mixed audience game.
        metaData.commit()
        UnityAds.addListener(this)

        // on click listeners
        buttonStartMining.setOnClickListener(this)
        buttonStartMiningPro.setOnClickListener(this)
        layoutReferApp.setOnClickListener(this)
        layoutReferApp1.setOnClickListener(this)
        imageViewQuiz.setOnClickListener(this)
    }

    private fun initStartAppAd() {
        mStartAppAd = StartAppAd(requireContext())
        mStartAppAd.setVideoListener(this)
    }

    private fun setData() {
        val databaseRef = Firebase.database.reference.child(TableManagement.USERS).child(mUid!!)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                mUser = snapshot.getValue(User::class.java)

                try {
                    // show user's coin balance
                    textViewCoinBalance.text = mUser?.coin?.currentCoins.toString() + " SHIB"
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Getting Post failed, log a message
                print(error.toException())
            }
        }
        databaseRef.addValueEventListener(postListener)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonStartMining -> {
                //showVideoAd()
                loadUnityAd()
            }
            R.id.buttonStartMiningPro -> {
                if(isPro()) {
                    loadUnityAd()
                } else {
                    val intent = Intent(requireActivity(), ProFeaturesActivity::class.java)
                    requireActivity().startActivity(intent)
                }

                /*Toast.makeText(
                    context,
                    "Work under process. Coming Soon !!!",
                    Toast.LENGTH_SHORT
                ).show()*/

                /*val uri: Uri =
                    Uri.parse("https://forms.gle/PUKX43X6sGfS5NCv5")

                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)*/
            }
            R.id.layoutReferApp1 -> {
                Utility.openOtherAppInPlayStore(requireContext())
            }
            R.id.layoutReferApp -> {
                Utility.openOtherAppInPlayStore(requireContext())
            }
            R.id.imageViewQuiz -> {
                Utility.openChromeCustomTabUrl(requireContext(), "https://423.live.qureka.com/")
            }
            else -> {
            }
        }
    }

    private fun showVideoAd() {
        showProgressDialog()
        // check if video is available
        /*mStartAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                dismissProgressDialog()

                // start showing ad
                mStartAppAd.showAd()
                showRewardToast()
            }

            override fun onFailedToReceiveAd(ad: Ad) {
                // Can't show rewarded video ad from start app
                Utility.showToast(context, "Unable to load Ad, Please try again.")
            }
        })*/
    }

    // Implement a function to display an ad if the Ad Unit or Placement is ready:
    private fun loadUnityAd() {
        if (UnityAds.isReady(surfacingId)) {
            UnityAds.show(requireActivity(), surfacingId);
        } else {
            Utility.showToast(context, "Unable to load Ad, Please try again.")
        }
    }

    private fun rewardUser() {
        try {
            var currentCoins: Double? = mUser?.coin?.currentCoins
            var rewardCoins: Double = CoinManagement.getRewardedVideoCoins()
            if (isPro()) {
                rewardCoins = 2 * rewardCoins
            }
            currentCoins = currentCoins!! + rewardCoins

            var lifetimeCoins: Double? = mUser?.coin?.lifetimeCoins
            lifetimeCoins = lifetimeCoins!! + rewardCoins

            // Write it to the database
            val userData =
                User(mUser?.email, Coin(lifetimeCoins, mUser?.coin?.withdrawnCoins, currentCoins))
            Firebase.database.reference.child(TableManagement.USERS).child(mUid!!).setValue(userData)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun showRewardDialog() {
        try {
            var rewardCoins = CoinManagement.getRewardedVideoCoins()
            if (isPro()) {
                rewardCoins = 2 * rewardCoins
            }
            if (activity != null) {
                if (!requireActivity().isFinishing()) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                    builder.setTitle(resources.getString(R.string.rewarded_dialog_header))
                    builder.setMessage(
                        resources.getString(
                            R.string.rewarded_dialog_message,
                            rewardCoins.toString()
                        )
                    )
                    builder.setCancelable(false)
                    val dialog = builder.create()
                    dialog.show()
                }
            } else {
                Utility.showLongToast(
                    requireContext(), getString(
                        R.string.rewarded_dialog_message,
                        rewardCoins.toString()
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun showRewardToast() {
        var rewardCoins = CoinManagement.getRewardedVideoCoins()
        if (isPro()) {
            rewardCoins = 2 * rewardCoins
        }
        Utility.showLongToast(
            context,
            resources.getString(
                R.string.rewarded_dialog_message,
                rewardCoins.toString()
            )
        )
    }

    override fun onVideoCompleted() {
        // on video completion, reward user
        rewardUser()
    }

    fun showProgressDialog() {
        if (activity != null) {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog.setMessage("Starting the process. Please wait...")
            mProgressDialog.setCancelable(true)
            mProgressDialog.show()
        }
    }

    fun dismissProgressDialog() {
        if (mProgressDialog.isShowing && !requireActivity().isFinishing)
            mProgressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnityAdsReady(p0: String?) {
    }

    override fun onUnityAdsStart(p0: String?) {
    }

    override fun onUnityAdsFinish(surfacingId: String?, finishState: UnityAds.FinishState?) {
        if (finishState!!.equals(UnityAds.FinishState.COMPLETED)) {
            rewardUser()
            showRewardDialog()
        } else if (finishState!!.equals(UnityAds.FinishState.ERROR)) {
            Utility.showToast(context, "Please watch complete video for reward.")
        }
    }

    override fun onUnityAdsError(p0: UnityAds.UnityAdsError?, p1: String?) {
        // Utility.showToast(context, "Unable to load Ad, Please try again.")
        showVideoAd()
    }

    /*override fun onResume() {
        super.onResume()
        // track screen event
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Earn")
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }*/

    // check if user is PRO
    fun isPro(): Boolean {
        return MySharedPrefs.getInstance(requireContext()).getBoolean(Utility.isPro)
    }
}