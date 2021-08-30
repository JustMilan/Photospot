package com.example.photospot.autocomplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.photospot.R


class AutocompleteAdapter(
    private val context: Context,
    private val autoCompleteItems: ArrayList<AutocompleteItemData>
) : BaseAdapter() {
    private lateinit var primaryText: TextView
    private lateinit var secondaryText: TextView
    private lateinit var placeId: TextView

    override fun getCount(): Int {
        return autoCompleteItems.size
    }

    override fun getItem(position: Int): AutocompleteItemData {
        return autoCompleteItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView1 = convertView

        convertView1 =
            LayoutInflater.from(context).inflate(R.layout.autocomplete_list_item, parent, false)

        primaryText = convertView1!!.findViewById(R.id.primaryText)
        primaryText.text = autoCompleteItems[position].primaryText

        secondaryText = convertView1.findViewById(R.id.secondaryText)
        secondaryText.text = autoCompleteItems[position].secondaryText

        return convertView1
    }
}