package com.crypto.prices.view.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.crypto.prices.R
import com.crypto.prices.model.Coin
import com.crypto.prices.model.User
import com.crypto.prices.model.WithdrawReq
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.fragment_withdraw.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class FragmentWithdraw : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    private var mUid: String? = null
    private var mUser: User? = null
    private val mStatus: String? = "Pending"
    private val mRemark: String? = ""
    private var mName: String? = null
    private var mMinWithdrawCoins: Double = 10000.00

    private var referLink = Utility.binanceReferLink
    private var radioBtnChecked: Int? = 0
    private val modes = arrayOf("Email", "Phone", "PayId")
    private var spinnerSelectedPos = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.fragment_withdraw, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // fetch uid of user
        mUid = activity?.intent?.getStringExtra("uid")
        mName = activity?.intent?.getStringExtra("name")
        mMinWithdrawCoins = Utility.getMinWithdrawCoinsFromSharedPrefs(requireActivity())
        setLabelData(mMinWithdrawCoins)
        setSpinnerData()
        // set data from database
        setCoinData()
        // on click listener
        textViewHelp.setOnClickListener(this)
        textViewRefer.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

        radioBtnChecked = R.id.radioBinance
        constraintGroup.visibility = View.GONE
        // radio button check listener
        radioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBinance -> {
                    radioBtnChecked = R.id.radioBinance
                    editTextWalletAddress.setText("")
                    editTextWalletAddress.setHint(R.string.binace_hint_text)
                    spanWithdrawIssueNote(getString(R.string.withdraw_issue_note_binace))
                    textViewNote3.text = getString(R.string.binace_refer_text)
                    referLink = Utility.binanceReferLink
                    constraintGroup.visibility = View.GONE
                    spinner.visibility = View.VISIBLE
                    if (spinnerSelectedPos == 1) {
                        editTextCountryCode.visibility = View.VISIBLE
                    } else {
                        editTextCountryCode.visibility = View.GONE
                    }
                }
                R.id.radioOther -> {
                    radioBtnChecked = R.id.radioOther
                    editTextWalletAddress.setText(retrieveFromSharedPrefs())
                    editTextWalletAddress.setHint(R.string.wallet_hint_text)
                    spanWithdrawIssueNote(getString(R.string.withdraw_issue_note))
                    textViewNote3.text = getString(R.string.wallet_refer_text)
                    referLink = Utility.bitBnsReferLink
                    constraintGroup.visibility = View.VISIBLE
                    spinner.visibility = View.GONE
                    editTextCountryCode.visibility = View.GONE
                }
                else -> {

                }
            }
        })
    }

    private fun setSpinnerData() {
        val adapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        // item selected listener
        spinner.setOnItemSelectedListener(this)
    }

    private fun setLabelData(minCoins: Double) {
        // min withdraw note
        val spannable1 = SpannableString(
            getString(
                R.string.min_withdraw_note,
                minCoins.toString()
            )
        )
        spannable1.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0, // start
            5, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable1.setSpan(
            StyleSpan(Typeface.BOLD),
            0, // start
            5, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textViewNote.text = spannable1

        // withdraw issue note
        spanWithdrawIssueNote(getString(R.string.withdraw_issue_note_binace))

        // help text
        val spannable3 = SpannableString(getString(R.string.help_text))
        spannable3.setSpan(
            UnderlineSpan(),
            0, // start
            spannable3.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textViewHelp.text = spannable3

        // refer text
        val spannable4 = SpannableString(getString(R.string.my_refer_link))
        spannable4.setSpan(
            UnderlineSpan(),
            0, // start
            spannable4.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textViewRefer.text = spannable4
    }

    private fun spanWithdrawIssueNote(withdrawIssueNote: String) {
        val spannable2 = SpannableString(withdrawIssueNote)
        spannable2.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0, // start
            5, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable2.setSpan(
            StyleSpan(Typeface.BOLD),
            0, // start
            5, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textViewNote2.text = spannable2
    }

    private fun setCoinData() {
        val databaseRef = Firebase.database.reference.child(TableManagement.USERS).child(mUid!!)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                mUser = snapshot.getValue(User::class.java)

                // show user's coin balance
                try {
                    textViewLifetimeCoins.text = formattedValue(mUser?.coin?.lifetimeCoins)
                    textViewWithdrawnCoins.text = formattedValue(mUser?.coin?.withdrawnCoins)
                    textViewCurrentCoins.text = formattedValue(mUser?.coin?.currentCoins)
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

    companion object {
        fun newInstance(): FragmentWithdraw = FragmentWithdraw()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewHelp -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://youtu.be/0OkpCYDhJ_Y")
                })
            }
            R.id.textViewRefer -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(referLink)
                })
            }
            R.id.buttonSubmit -> {
                if (editTextCoins.text.isNullOrBlank()) {
                    Utility.showToast(
                        context,
                        "Please enter number of coins to withdraw."
                    )
                    return
                }

                if (editTextWalletAddress.text.isNullOrBlank()) {
                    Utility.showToast(context, "Please enter wallet address")
                    return
                }

                var withdrawReqCoins = editTextCoins?.text.toString().trim().toDouble()
                var walletAddress = editTextWalletAddress?.text.toString().trim()
                var countryCode = editTextCountryCode?.text.toString().trim()

                // if not binance then check these conditions
                if (radioBtnChecked == R.id.radioOther) {
                    if (editTextWalletTag.text.isNullOrBlank()) {
                        Utility.showToast(context, "Please enter xrp wallet tag")
                        return
                    }

                    //if (!walletAddress.startsWith("ACT", true)) {
                    if (!walletAddress.startsWith("r", true)) {
                        Utility.showToast(
                            context,
                            "Invalid Wallet address. Wallet address should start with 'r'"
                        )
                        return
                    }
                    //}
                }

                if (withdrawReqCoins < mMinWithdrawCoins) {
                    val min = mMinWithdrawCoins
                    Utility.showToast(
                        context,
                        "Min withdrawable coins are $min SHIB."
                    )
                } else if ((withdrawReqCoins.compareTo(mUser?.coin?.currentCoins!!.toDouble()) > 0)) {
                    Utility.showToast(context, "Your Coin Balance is lower than entered quantity.")
                } else {
                    // take withdraw request
                    val randomId = Random.nextLong(1, 100000000)
                    // save wallet address for later
                    saveInSharedPrefs(walletAddress)

                    // if using binance
                    var req: WithdrawReq
                    if (radioBtnChecked == R.id.radioBinance) {
                        // basic checks
                        if (validateEmailPhone()) {
                            req = WithdrawReq(
                                mUid,
                                mName,
                                walletAddress + ", Binance " + countryCode,
                                getCurrentDateTime(),
                                mUser?.coin?.currentCoins,
                                withdrawReqCoins,
                                mStatus, mRemark, isPro()
                            )
                            editTextCountryCode.setText("")
                        } else {
                            return
                        }
                    } else {
                        req = WithdrawReq(
                            mUid,
                            mName,
                            walletAddress + "," + editTextWalletTag.text.toString(),
                            getCurrentDateTime(),
                            mUser?.coin?.currentCoins,
                            withdrawReqCoins,
                            mStatus, mRemark, isPro()
                        )
                    }

                    Firebase.database.reference.child(TableManagement.WITHDRAW_REQ_2022)
                        .child(mUid!!)
                        .child(randomId.toString())
                        .setValue(req)

                    // we need to substract these coins from current bal of user
                    var withdrawnCoinsNow = mUser?.coin?.withdrawnCoins?.plus(withdrawReqCoins)
                    var currentBalNow = mUser?.coin?.currentCoins?.minus(withdrawReqCoins)

                    val userData = User(
                        mUser?.email,
                        Coin(mUser?.coin?.lifetimeCoins, withdrawnCoinsNow, currentBalNow)
                    )
                    Firebase.database.reference.child(TableManagement.USERS).child(mUid!!)
                        .setValue(userData)

                    Utility.showToast(
                        context,
                        "Bingo !!! Your withdraw request submitted successfully."
                    )
                }
            }
            else -> {
            }
        }
    }

    private fun validateEmailPhone(): Boolean {
        when (spinnerSelectedPos) {
            0 -> {
                return if (Patterns.EMAIL_ADDRESS.matcher(editTextWalletAddress.text.toString().trim())
                        .matches()
                ) {
                    true
                } else {
                    editTextWalletAddress.setError("invalid email address")
                    false
                }
            }
            1 -> {
                return if (Patterns.PHONE.matcher(editTextWalletAddress.text.toString().trim())
                        .matches()
                ) {
                    return if (editTextCountryCode.text.toString()
                            .isBlank() || editTextCountryCode.text.toString().isEmpty()
                    ) {
                        editTextCountryCode.setError("Please enter country code")
                        return false
                    } else {
                        return true
                    }
                } else {
                    editTextWalletAddress.setError("invalid phone number")
                    false
                }
            }
            2 -> {
                var onlyDigits = true
                for (i in 0 until editTextWalletAddress.text.toString().trim().length) {
                    if (!Character.isDigit(editTextWalletAddress.text.toString().trim().get(i))) {
                        onlyDigits = false
                        break
                    }
                }
                if (!onlyDigits)
                    editTextWalletAddress.setError("invalid Pay Id")
                return onlyDigits
            }
            else -> {
            }
        }
        return true
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
        return sdf.format(Date())
    }

    fun saveInSharedPrefs(walletAddress: String) {
        val sharedPreference =
            requireActivity().getSharedPreferences("WALLET_PREFERENCE", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("walletAddress", walletAddress)
        editor.commit()
    }

    fun retrieveFromSharedPrefs(): String? {
        val sharedPreference =
            requireActivity().getSharedPreferences("WALLET_PREFERENCE", Context.MODE_PRIVATE)
        return sharedPreference.getString("walletAddress", "")
    }

    /*override fun onResume() {
        super.onResume()
        // track screen event
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Withdraw")
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }*/

    // check if user is PRO
    fun isPro(): Boolean {
        return MySharedPrefs.getInstance(requireContext()).getBoolean(Utility.isPro)
    }

    // format value to 2 decimal places
    fun formattedValue(value: Double?): String {
        return String.format("%.1f", value) + " SHIB"
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (pos) {
            0 -> {
                editTextCountryCode.visibility = View.GONE
                spinnerSelectedPos = 0
                editTextWalletAddress.setHint(getString(R.string.binace_email))
            }
            1 -> {
                editTextCountryCode.visibility = View.VISIBLE
                spinnerSelectedPos = 1
                editTextWalletAddress.setHint(getString(R.string.binace_phone))
            }
            2 -> {
                editTextCountryCode.visibility = View.GONE
                spinnerSelectedPos = 2
                editTextWalletAddress.setHint(getString(R.string.binace_payid))
            }
            else -> {
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}