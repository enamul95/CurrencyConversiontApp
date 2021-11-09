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

class CurrencySpinnerAdapter(
    private var activity: Activity,
    private var currencyList: ArrayList<CurrencyAdapterModel>
) : BaseAdapter() {

    private class ViewHolder(row: View) {
        var tv_currecy_code: TextView? = null

        init {
            this.tv_currecy_code = row?.findViewById(R.id.tv_currecy_code)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.row_spinner_currency, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var item = currencyList[position]
        viewHolder.tv_currecy_code?.text = item.conversionCurrency.toString()

        return view as View
    }

    override fun getItem(i: Int): CurrencyAdapterModel {
        return currencyList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return currencyList.size
    }
}