package com.crypto.prices.view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bluehomestudio.luckywheel.WheelItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.crypto.prices.BuildConfig
import com.crypto.prices.R
import com.crypto.prices.model.Coin
import com.crypto.prices.model.SpinData
import com.crypto.prices.model.User
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.fragment_refer.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FragmentRefer : Fragment(), View.OnClickListener {
    private var mUid: String? = null
    private var mUser: User? = null
    private var mSpinData: SpinData? = null
    private var mInvitationUrl: Uri? = null
    private var randomNumber: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.fragment_refer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textViewReferMsg.text =
            getString(R.string.refer_msg_detail, CoinManagement.getReferralCoins().toString())
        // fetch uid of user
        mUid = activity?.intent?.getStringExtra("uid")
        getData()
        luckyWheelSetUp()

        // on click listener
        buttonInvite.setOnClickListener(this)
        imageViewGameZop.setOnClickListener(this)
        buttonSpin.setOnClickListener(this)
    }

    private fun luckyWheelSetUp() {
        val wheelItems: MutableList<WheelItem> = ArrayList()
        wheelItems.add(
            WheelItem(
                Color.GRAY,
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "10 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.parseColor("#89CFF0"),
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "20 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.RED,
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "30 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.GRAY,
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(), "0 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.parseColor("#89CFF0"),
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "10 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.RED,
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "20 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.GRAY,
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "30 SHIB"
            )
        )

        wheelItems.add(
            WheelItem(
                Color.parseColor("#89CFF0"),
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_card_giftcard_24
                )?.toBitmap(),
                "0 SHIB"
            )
        )

        luckyWheelView.addWheelItems(wheelItems)
        luckyWheelView.setLuckyWheelReachTheTarget {
            rewardUser(coinsWon())
            showRewardDialog()
        }
    }

    companion object {
        fun newInstance(): FragmentRefer = FragmentRefer()
        val domain = "https://shibminer.page.link"
        val baseUrl =
            Uri.parse("https://play.google.com/store/apps/details?id=com.miner.shib_miner")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonInvite -> {
                createLink()
            }
            R.id.imageViewGameZop -> {
                Utility.openChromeCustomTabUrl(requireContext(), getString(R.string.gamezop_url))
            }
            R.id.buttonSpin -> {
                if (past24Hours()) {
                    randomNumber = getRandomNumber(8)
                    luckyWheelView.rotateWheelTo(randomNumber)
                } else {
                    Utility.showLongToast(
                        requireContext(),
                        "Lucky Wheel will be unlocked after 24 hours. Please come again."
                    )
                }
            }
            else -> {
            }
        }
    }

    fun createLink() {
        val invitationLink = "https://shibminer.com/?invitedby=$mUid"

        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(invitationLink)
            domainUriPrefix = domain
            androidParameters(BuildConfig.APPLICATION_ID) {
                minimumVersion = 125
            }
        }.addOnSuccessListener { shortDynamicLink ->
            mInvitationUrl = shortDynamicLink.shortLink
            sendInvitation()
        }
    }

    fun sendInvitation() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, Check out this amazing app which provides free Shiba Inu coins !! Use my referrer link: " + mInvitationUrl.toString()
        )

        startActivity(Intent.createChooser(intent, "Share Link"))
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
        return sdf.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /*override fun onResume() {
        super.onResume()
        // track screen event
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Refer")
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }*/
    private fun getRandomNumber(range: Int): Int {
        return (1..range).random()
    }

    // get data on fragment load
    private fun getData() {
        val databaseRef = Firebase.database.reference.child(TableManagement.USERS).child(mUid!!)
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                mUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                // Getting Post failed, log a message
                print(error.toException())
            }
        }
        databaseRef.addValueEventListener(postListener)

        val databaseRefSpin =
            Firebase.database.reference.child(TableManagement.SPIN_EARNING).child(mUid!!)
        val postListenerSpin = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                mSpinData = snapshot.getValue(SpinData::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                // Getting Post failed, log a message
                print(error.toException())
            }
        }
        databaseRefSpin.addValueEventListener(postListenerSpin)
    }

    private fun past24Hours(): Boolean {
        var savedTime = MySharedPrefs.getInstance(requireContext()).getLong(Utility.spinWheelTime)
        if (savedTime != 0L) {
            var timePassed =
                TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - savedTime!!)

            if (timePassed > 24) {
                return true
            }
            return false
        }
        return true
    }

    private fun rewardUser(rewardedCoins: Int) {
        try {
            var currentCoins: Double? = mUser?.coin?.currentCoins
            var rewardCoins: Double = rewardedCoins.toDouble()

            currentCoins = currentCoins!! + rewardCoins

            var lifetimeCoins: Double? = mUser?.coin?.lifetimeCoins
            lifetimeCoins = lifetimeCoins!! + rewardCoins

            // Write it to the database
            val userData =
                User(mUser?.email, Coin(lifetimeCoins, mUser?.coin?.withdrawnCoins, currentCoins))
            Firebase.database.reference.child(TableManagement.USERS).child(mUid!!)
                .setValue(userData)

            // add reward coins to spin data table also
            val spinData = SpinData(mSpinData?.email, mSpinData?.coins?.plus(rewardCoins))
            Firebase.database.reference.child(TableManagement.SPIN_EARNING).child(mUid!!)
                .setValue(spinData)

            // save user's current time
            MySharedPrefs.getInstance(requireContext())
                .saveLong(Utility.spinWheelTime, System.currentTimeMillis())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun showRewardDialog() {
        try {
            if (activity != null) {
                if (!requireActivity().isFinishing()) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                    builder.setTitle(resources.getString(R.string.rewarded_dialog_header))
                    builder.setMessage(
                        resources.getString(
                            R.string.spin_rewarded_dialog_message,
                            coinsWon().toString()
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
                        coinsWon().toString()
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun coinsWon(): Int {
        when (randomNumber) {
            1, 5 -> {
                return 10
            }
            2, 6 -> {
                return 20
            }
            3, 7 -> {
                return 30
            }
            else -> {
                return 0
            }
        }
    }
}