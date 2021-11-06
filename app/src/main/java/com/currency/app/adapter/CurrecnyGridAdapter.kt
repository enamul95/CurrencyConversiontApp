package com.currency.app.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.currency.app.R

class CurrecnyGridAdapter(private var activity: Activity, private var currencyCodes: Array<String>) :  BaseAdapter() {


    private class ViewHolder(row: View) {
        var tvCurrencyCode: TextView? = null
        init {
            this.tvCurrencyCode = row?.findViewById(R.id.tvCurrencyCode)
        }
    }

    override fun getCount(): Int {
      return  currencyCodes.size
    }

    override fun getItem(position: Int): Any {
         return  currencyCodes.size
    }

    override fun getItemId(position: Int): Long {
      return  position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.row_currecny, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

       // var item = currencyCodes[position]
        viewHolder.tvCurrencyCode?.text =  currencyCodes[position]

        return view as View
    }
}