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
// Part of Main Activity (third tab), contain about report about item  ,
// moreover, admin can also read about item report about item
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
        val measureNumTV = activity.findViewById<TextView>(R.id.fragment_report_tv_measure_num)
        val unitNumTV = activity.findViewById<TextView>(R.id.fragment_report_tv_unit_num)
        val userNumTV = activity.findViewById<TextView>(R.id.fragment_report_tv_user_num)
        val userRoleTL = activity.findViewById<TableLayout>(R.id.fragment_report_tl_user_role)
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
                    itemNumLocTL.removeAllViews()
                    locationsActive.forEach { loc ->
                        tableRowAdd(itemNumLocTL, String.format("- %s", loc.code), itemsActive.filter { it.stocks[locationsActive.indexOf(loc)] > 0 }.count().toString())
                    }
                    itemShortageTV.text = itemsActive.filter { item -> item.stocks.count { it < item.safetyStock } > 0 }.size.toString()
                    itemShortageTL.removeAllViews()
                    locationsActive.forEach { loc ->
                        tableRowAdd(itemShortageTL, String.format("- %s", loc.code), itemsActive.filter { it.stocks[locationsActive.indexOf(loc)] < it.safetyStock }.count().toString())
                    }
                    itemStorageTL.removeAllViews()
                    locationsActive.forEach { loc ->
                        tableRowAdd(itemStorageTL, String.format("- %s", loc.code))
                        unitsActive.filter { it.value == 1.0 }.distinctBy { it.measure }.forEach { unit ->
                            val value = String.format("%s %s",
                                    itemsActive.filter { i -> unitsActive.filter { unit.measure==it.measure }.singleOrNull { i.unitId==it._id } != null }
                                            .sumByDouble { it.stocks[locationsAll.indexOf(loc)] }.toString(),
                                    unit.unitName)
                            tableRowAdd(itemStorageTL, String.format("   ▪ %s", unit.measure), value)
                        }
                    }
                    measureNumTV.text = unitsActive.distinctBy { it.measure }.size.toString()
                    unitNumTV.text = unitsActive.size.toString()
                    userNumTV.text = accountsActive.size.toString()
                    accountsActive.distinctBy { it.role }.forEach { acc ->
                        tableRowAdd(userRoleTL, String.format("- %s", acc.role), accountsActive.filter { it.role == acc.role }.size.toString())
                    }
                }
            }
        }
    }
    fun tableRowAdd(tl: TableLayout, label: String, value: String? = null){
        val row = TableRow(activity)
        val labelTV = TextView(activity)
        labelTV.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
        labelTV.text = label
        row.addView(labelTV)
        if(value != null){
            val valueTV = TextView(activity)
            valueTV.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            valueTV.text = value
            valueTV.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
            row.addView(valueTV)
        }
        tl.addView(row)
    }
}
