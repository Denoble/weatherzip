package com.example.weatherzip.ui.screen1

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherzip.R

class ListAdapter(
    private val lists: List<String>, val context: Context,
    val onClickListener: ListAdapter.OnClickListener
) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return ListViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val zip = lists[position]
        holder.itemView.setOnClickListener {
            onClickListener.onClick(zip)
        }
        holder.bind(zip)
    }
    class ListViewHolder(v: View) :
        RecyclerView.ViewHolder(v) {
        private val view: View = v
        val zipCode: TextView = view.findViewById(R.id.zip_code)
        fun bind(postalCode: String) {
            zipCode.text = postalCode
        }

    }

    class OnClickListener(val clickListener: (zip: String) -> Unit) {
        fun onClick(curentZip: String) = clickListener(curentZip)
    }



    override fun getItemCount(): Int {
        return lists.size
    }
}
