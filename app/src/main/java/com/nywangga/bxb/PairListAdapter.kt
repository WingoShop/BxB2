package com.nywangga.bxb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PairListAdapter(private val context: Context, private val allPairs: MutableList<String>) :
    RecyclerView.Adapter<PairListAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pair_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = allPairs.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPair = itemView.findViewById<TextView>(R.id.tvPair)
        fun bind(position: Int) {
            tvPair.text = allPairs[position]
        }

    }

}