package com.nywangga.bxb

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class PostHistoryAdapter(
    private val context: Context,
    private val postHistoryList: List<Post>,
    private val realEmail: String
) :
    RecyclerView.Adapter<PostHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / 3
        val view = LayoutInflater.from(context).inflate(R.layout.post_history, parent, false)
        val layoutParams1 = view.findViewById<TextView>(R.id.tvAmountHist)
        val layoutParams2 = view.findViewById<TextView>(R.id.tvRemarkHist)
        val layoutParams3 = view.findViewById<TextView>(R.id.tvDateHist)
        layoutParams1.width = cardWidth
        layoutParams2.width = cardWidth
        layoutParams3.width = cardWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return postHistoryList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmountHist = itemView.findViewById<TextView>(R.id.tvAmountHist)
        private val tvRemarkHist = itemView.findViewById<TextView>(R.id.tvRemarkHist)
        private val tvDateHist = itemView.findViewById<TextView>(R.id.tvDateHist)
        private val cardView = itemView.findViewById<CardView>(R.id.cardView)
        private val layoutParams =  cardView.layoutParams as MarginLayoutParams


        fun bind(position: Int) {
            val post = postHistoryList[position]
            tvAmountHist.text = post.amount.toString()
            tvRemarkHist.text = post.remarks.toString()
            if (post.created_by != realEmail) {
                cardView.setCardBackgroundColor(Color.parseColor("#78A2CC"))
                layoutParams.setMargins(16, 16, 150, 16)
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#b23a48"))
                layoutParams.setMargins(150, 16, 16, 16)
            }


            val netDate = SimpleDateFormat("dd MMMM, HH:mm", Locale.ENGLISH)
            val testdate = netDate.format(post.created_date?.seconds!! * 1000L)
            tvDateHist.text = testdate
        }
    }
}
