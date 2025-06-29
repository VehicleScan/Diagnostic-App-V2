package com.example.diagnostic333

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class DtcsAdapter : ListAdapter<DtcsMsg, DtcsAdapter.DtcsViewHolder>(DtcsDiffCallback()) {

    class DtcsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.dtcNameTextView)
        private val descriptionPreviewTextView: TextView = itemView.findViewById(R.id.dtcDescriptionPreviewTextView)

        fun bind(dtcsMsg: DtcsMsg, onClick: (DtcsMsg) -> Unit) {
            nameTextView.text = dtcsMsg.name
            //descriptionPreviewTextView.text = dtcsMsg.description
            itemView.setOnClickListener { onClick(dtcsMsg) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DtcsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dtcs, parent, false)
        return DtcsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DtcsViewHolder, position: Int) {
        val dtcsMsg = getItem(position)
        holder.bind(dtcsMsg) { dtcs ->
            AlertDialog.Builder(holder.itemView.context, R.style.AppDialogTheme)
                .setTitle(dtcs.name)
                .setMessage(dtcs.description)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}

class DtcsDiffCallback : DiffUtil.ItemCallback<DtcsMsg>() {
    override fun areItemsTheSame(oldItem: DtcsMsg, newItem: DtcsMsg): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DtcsMsg, newItem: DtcsMsg): Boolean {
        return oldItem == newItem
    }
}