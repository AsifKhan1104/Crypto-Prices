package com.crypto.prices.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crypto.prices.R
import com.crypto.prices.model.WithdrawReq
import kotlinx.android.synthetic.main.item_withdraw_history.view.*

class WithdrawReqOldAdapter(var requests: ArrayList<WithdrawReq>) :
    RecyclerView.Adapter<WithdrawReqOldAdapter.WithdrawViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = WithdrawViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_withdraw_history, parent, false)
    )

    override fun getItemCount() = requests.size

    override fun onBindViewHolder(holder: WithdrawViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    class WithdrawViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textViewName = view.textViewName
        private val textViewWithdrawBal = view.textViewWithdrawBal
        private val textViewStatus = view.textViewStatus
        private val textViewRemark = view.textViewRemark
        private val holderView = view

        fun bind(req: WithdrawReq) {
            if (req.name.isNullOrBlank()) {
                textViewName.text = "User"
            } else {
                textViewName.text = req.name
            }
            textViewWithdrawBal.text = req.withdrawCoins.toString()
            textViewStatus.text = req.status
            textViewRemark.text = req.remark

            // change view color as per status
            /*if(req.status.equals("completed", true)){
                holderView.setBackgroundColor(Color.parseColor("#FFFF00"))
            } else {
               holderView.setBackgroundColor(Color.parseColor("#FF0000"))
            }*/
        }
    }

    fun update(newReqs: List<WithdrawReq>) {
        requests.clear()
        requests.addAll(newReqs)
        notifyDataSetChanged()
    }
}