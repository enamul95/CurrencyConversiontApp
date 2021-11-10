package com.currency.app.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.currency.app.R
import com.currency.app.model.CurrencyAdapterModel
import java.util.ArrayList

class CurrecnyGridAdapter(
    private var activity: Activity,
    private var currencyList: ArrayList<CurrencyAdapterModel>
) : BaseAdapter() {


    private class ViewHolder(row: View) {
        var tvCurrencyCode: TextView? = null

        init {
            this.tvCurrencyCode = row?.findViewById(R.id.tvCurrencyCode)
        }
    }

    override fun getCount(): Int {
        return currencyList.size
    }

    override fun getItem(position: Int): Any {
        return currencyList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.row_currecny, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var currency = currencyList[position]
        viewHolder.tvCurrencyCode?.text = currency.conversionCurrency

        return view as View
    }
}