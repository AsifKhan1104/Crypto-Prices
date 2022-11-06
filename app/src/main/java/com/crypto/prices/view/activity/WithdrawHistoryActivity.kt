package com.crypto.prices.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.crypto.prices.R
import com.crypto.prices.model.WithdrawReq
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.view.adapter.WithdrawReqAdapter
import kotlinx.android.synthetic.main.activity_withdraw_history.*


class WithdrawHistoryActivity : AppCompatActivity() {
    private var mUid: String? = null
    private var mName: String? = null
    private var mRequestList: ArrayList<WithdrawReq> = ArrayList()
    private var mAdapter: WithdrawReqAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_history)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        // get data from intent
        mUid = intent.getStringExtra("uid")
        mName = intent.getStringExtra("name")

        fetchData(mUid)

        mAdapter = WithdrawReqAdapter(mRequestList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        // on click listener
        fab.setOnClickListener { view ->
            val intent =
                Intent(applicationContext, WithdrawHistoryOldActivity::class.java)
            intent.putExtra("uid", mUid)
            intent.putExtra("name", mName)
            startActivity(intent)
        }
    }

    // fetch data from firebase database
    private fun fetchData(uid: String?) {
        val databaseRef =
            Firebase.database.reference.child(TableManagement.WITHDRAW_REQ_2022)/*.child(uid!!)*/

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                var historyList: ArrayList<WithdrawReq> = ArrayList()
                for (postSnapshot in snapshot.children) {
                    for (childSnapshot in postSnapshot.children) {
                        val history: WithdrawReq? = childSnapshot.getValue(WithdrawReq::class.java)
                        historyList.add(history!!)
                    }
                }

                // update the recycler view with new list, sorted by date
                mAdapter?.update(historyList.sortedByDescending { it.currentDataTime })
            }

            override fun onCancelled(error: DatabaseError) {
                // Getting Post failed, log a message
                print(error.toException())
            }
        }
        databaseRef.addValueEventListener(postListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}