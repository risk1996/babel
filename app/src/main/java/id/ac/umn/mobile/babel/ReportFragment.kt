package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


//==================================================================================================
// Report Fragment
//==================================================================================================
//
// Part of Main Activity (first tab), contain about report about item  ,
// moreover, admin can also read about item report about item
//
//--------------------------------------------------------------------------------------------------


class ReportFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }
    override fun onStart() {
        super.onStart()
        val compIconIV = activity.findViewById<ImageView>(R.id.fragment_report_iv_comp_icon)
        val compNameTV = activity.findViewById<TextView>(R.id.fragment_report_tv_comp_name)
        val compStatusTV = activity.findViewById<TextView>(R.id.fragment_report_tv_comp_status)
        val compSiteTV = activity.findViewById<TextView>(R.id.fragment_report_tv_comp_site)
        val compOfficeMainTV = activity.findViewById<TextView>(R.id.fragment_report_tv_comp_office_main)
        val compOfficeSecondaryTV = activity.findViewById<TextView>(R.id.fragment_report_tv_comp_office_secondary)
        val itemNumTV = activity.findViewById<TextView>(R.id.fragment_report_tv_item_num)
        val itemNumLocTL = activity.findViewById<TableLayout>(R.id.fragment_report_tl_item_num_loc)
        val itemShortageTV = activity.findViewById<TextView>(R.id.fragment_report_tv_item_shortage)
        val itemShortageTL = activity.findViewById<TableLayout>(R.id.fragment_report_tl_item_shortage_loc)
        val itemStorageTL = activity.findViewById<TableLayout>(R.id.fragment_report_tl_item_storage)
        val data = object : Data(){
            override fun onComplete() {
                if(isAdded){
                    compIconIV.setImageResource(R.drawable::class.java.getField(company.logo).getInt(null))
                    compNameTV.text = String.format("%s %s", company.typeShort, company.name)
                    compStatusTV.text = company.type
                    compSiteTV.text = company.site
                    compOfficeMainTV.text = company.officeMain
                    compOfficeSecondaryTV.text = company.officeSecondary

                    itemNumTV.text = itemsActive.filter { it.stocks.none { it == .0 } }.count().toString()
                    locationsActive.forEach {
                        val row = TableRow(activity)
                        val labelTV = TextView(activity)
                        val valueTV = TextView(activity)
                        val value = it
                        labelTV.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
                        labelTV.text = String.format("- %s", value.code)
                        row.addView(labelTV)
                        valueTV.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
                        valueTV.text = itemsActive.filter { it.stocks[locationsActive.indexOf(value)] > 0 }.count().toString()
                        valueTV.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                        valueTV.setTextColor(ContextCompat.getColor(activity, android.R.color.black))
                        row.addView(valueTV)
                        itemNumLocTL.addView(row)
                    }
                    itemShortageTV.text = itemsActive.filter { item -> item.stocks.count { it < item.safetyStock } > 0 }.size.toString()
                    locationsActive.forEach {
                        val row = TableRow(activity)
                        val labelTV = TextView(activity)
                        val valueTV = TextView(activity)
                        val value = it
                        labelTV.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
                        labelTV.text = String.format("- %s", value.code)
                        row.addView(labelTV)
                        valueTV.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
                        valueTV.text = itemsActive.filter { it.stocks[locationsActive.indexOf(value)] < it.safetyStock }.count().toString()
                        valueTV.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                        valueTV.setTextColor(ContextCompat.getColor(activity, android.R.color.black))
                        row.addView(valueTV)
                        itemShortageTL.addView(row)
                    }
//                    units.filter { it.value == 1.0 }.distinctBy { it.measure }.forEach {
//                        val row = TableRow(activity)
//                        val labelTV = TextView(activity)
//                        val valueTV = TextView(activity)
//                        val value = it
//                        labelTV.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
//                        labelTV.text = String.format("- %s", it.measure)
//                        row.addView(labelTV)
//                        valueTV.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
//                        valueTV.text = String.format("%s %s",
//                                items.filter { i -> units.filter { value.measure==it.measure }.singleOrNull { i.unit_id==it._id } != null }
//                                        .sumByDouble { i -> i.stock/units.single { it._id==i.unit_id }.value }.toString(),
//                                it.unit_name
//                        )
//                        valueTV.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
//                        valueTV.setTextColor(ContextCompat.getColor(activity, android.R.color.black))
//                        row.addView(valueTV)
//                        itemStorageTL.addView(row)
//                    }
                }
            }
        }
    }
}
