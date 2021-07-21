package com.example.photospot.autocomplete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photospot.R

class AutocompleteAdapter(private val dataSet: List<List<String>>) :
    RecyclerView.Adapter<AutocompleteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val primaryText: TextView
        val secondaryText: TextView

        init {
            // Define click listener for the ViewHolder's View.
            primaryText = view.findViewById(R.id.primaryText)
            secondaryText = view.findViewById(R.id.secondaryText)
        }
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.autocomplete_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.primaryText.text = dataSet[position][0]
        viewHolder.secondaryText.text = dataSet[position][1]
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataSet.size
}