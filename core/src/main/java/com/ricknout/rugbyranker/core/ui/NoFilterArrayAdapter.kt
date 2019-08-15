package com.ricknout.rugbyranker.core.ui

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class NoFilterArrayAdapter<T> : ArrayAdapter<T> {

    constructor(context: Context, resource: Int) : super(context, resource)

    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(context, resource, textViewResourceId)

    constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects)

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: Array<T>) : super(context, resource, textViewResourceId, objects)

    constructor(context: Context, resource: Int, objects: List<T>) : super(context, resource, objects)

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>) : super(context, resource, textViewResourceId, objects)

    override fun getFilter() = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults? = null

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        }
    }
}
