package id.ac.umn.mobile.babel

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackActivity : AppCompatActivity() {
    val filterInOut = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)
        val titleTV = findViewById<TextView>(R.id.activity_track_tv_title)
        val locationSpn = findViewById<Spinner>(R.id.activity_track_spn_location)
        val thirdPartySpn = findViewById<Spinner>(R.id.activity_track_spn_third_party)
        val startDateTV = findViewById<TextView>(R.id.activity_track_tv_start_date)
        val endDateTV = findViewById<TextView>(R.id.activity_track_tv_end_date)
        val activitiesRV = findViewById<RecyclerView>(R.id.activity_track_rv_activities)
        val op = intent.getStringExtra("OPERATION")
        titleTV.text = String.format("TRACK %s", op)

        val data = object : Data(){
            override fun onComplete() {
                val locs = locationsActive.map { it.code }.toMutableList()
                locs.add(0, "ALL")
                locationSpn.adapter = ArrayAdapter(this@TrackActivity, android.R.layout.simple_spinner_dropdown_item, locs)
                locationSpn.setSelection(0)
                val tps = thirdPartiesActive.filter { if(op=="INCOMING")it.role=="S" else it.role=="C" }.map { it.tpName }.toMutableList()
                tps.add(0, "ALL")
                thirdPartySpn.adapter = ArrayAdapter(this@TrackActivity, android.R.layout.simple_spinner_dropdown_item, tps)
                thirdPartySpn.setSelection(0)
                val startCalendar = GregorianCalendar.getInstance()
                startCalendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH))
                startDateTV.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(startCalendar.time)
                val endCalendar = GregorianCalendar.getInstance()
                endCalendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH))
                endDateTV.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(endCalendar.time)
                val spinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        repopulateFilter(activitiesRV, transactions, op,
                            if(locationSpn.selectedItemPosition == 0) null else locationsAll.single { it.code == locationSpn.selectedItem.toString() },
                            if(thirdPartySpn.selectedItemPosition == 0) null else thirdPartiesAll.single { it.tpName == thirdPartySpn.selectedItem.toString() },
                            startCalendar.time, endCalendar.time
                        )
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) { }
                }
                locationSpn.onItemSelectedListener = spinnerItemSelectedListener
                thirdPartySpn.onItemSelectedListener = spinnerItemSelectedListener
                startDateTV.setOnClickListener {
                    val dialog = DatePickerDialog(this@TrackActivity, DatePickerDialog.OnDateSetListener { _, i, j, k ->
                        startCalendar.set(i, j, k, 0, 0, 0)
                        startDateTV.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(startCalendar.time)
                        repopulateFilter(activitiesRV, transactions, op,
                            if(locationSpn.selectedItemPosition == 0) null else locationsAll.single { it.code == locationSpn.selectedItem.toString() },
                            if(thirdPartySpn.selectedItemPosition == 0) null else thirdPartiesAll.single { it.tpName == thirdPartySpn.selectedItem.toString() },
                            startCalendar.time, endCalendar.time
                        )
                    }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE))
                    dialog.datePicker.maxDate = endCalendar.time.time
                    dialog.show()
                }
                endDateTV.setOnClickListener{
                    val dialog = DatePickerDialog(this@TrackActivity, DatePickerDialog.OnDateSetListener { _, i, j, k ->
                        endCalendar.set(i, j, k, 23, 59, 59)
                        endDateTV.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(endCalendar.time)
                        repopulateFilter(activitiesRV, transactions, op,
                            if(locationSpn.selectedItemPosition == 0) null else locationsAll.single { it.code == locationSpn.selectedItem.toString() },
                            if(thirdPartySpn.selectedItemPosition == 0) null else thirdPartiesAll.single { it.tpName == thirdPartySpn.selectedItem.toString() },
                            startCalendar.time, endCalendar.time
                        )
                    }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DATE))
                    dialog.datePicker.minDate = startCalendar.time.time
                    dialog.show()
                }
            }
        }
        activitiesRV.adapter = TrackActivityRVAdapter(this, data)
    }
    fun repopulateFilter(recycler: RecyclerView, transactions: ArrayList<InOut>, op: String, location: Location?, thirdParty: ThirdParty?, start: Date, end: Date){
        var shownInOut = transactions.filter { it.inOutTime in start..end }
        if(op == "INCOMING") shownInOut = shownInOut.filter { it.inOutDetail.sumByDouble { it.second } > 0 }
        else shownInOut = shownInOut.filter { it.inOutDetail.sumByDouble { it.second } < 0 }
        (recycler.adapter as TrackActivityRVAdapter).locationSelected = location != null
        if(location != null) shownInOut = shownInOut.filter { it.locationId == location._id }
        (recycler.adapter as TrackActivityRVAdapter).thirdPartySelected = thirdParty != null
        if(thirdParty != null) shownInOut = shownInOut.filter { it.thirdPartyId == thirdParty._id }
        filterInOut.clear()
        shownInOut.map { it._id }.forEach { filterInOut.add(it) }
        if(shownInOut.size > 1)filterInOut.add(-1)
        recycler.adapter.notifyDataSetChanged()
        recycler.layoutManager = GridLayoutManager(this, if(shownInOut.isNotEmpty())filterInOut.size else 1, GridLayoutManager.HORIZONTAL, false)
    }
    inner class TrackActivityRVAdapter(private val context : Context, private val data : Data) : RecyclerView.Adapter<TrackActivityRVAdapter.ViewHolder>(){
        var locationSelected: Boolean = false
        var thirdPartySelected: Boolean = false
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var dateTV : TextView        = itemView.findViewById(R.id.activity_track_recycler_view_item_tv_date)
            var locationTV : TextView    = itemView.findViewById(R.id.activity_track_recycler_view_item_tv_location)
            var purposeTV : TextView     = itemView.findViewById(R.id.activity_track_recycler_view_item_tv_purpose)
            var accountableTV : TextView = itemView.findViewById(R.id.activity_track_recycler_view_item_tv_accountable)
            val detailsTL : TableLayout  = itemView.findViewById(R.id.activity_track_recycler_view_item_tl_details)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.activity_track_recycler_view_item, parent, false)
            val card = view.findViewById(R.id.activity_track_recycler_view_item_cv_item) as CardView
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if(filterInOut[position]>0){
                val inOut = data.transactions.single { it._id == filterInOut[position] }
                holder.dateTV.text = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(inOut.inOutTime)
                holder.locationTV.text = data.locationsAll.single { it._id == inOut.locationId }.code
                holder.locationTV.visibility = if(locationSelected) View.GONE else View.VISIBLE
                holder.purposeTV.text = data.thirdPartiesAll.single { it._id == inOut.thirdPartyId }.tpName
                holder.purposeTV.visibility = if(thirdPartySelected) View.GONE else View.VISIBLE
                holder.accountableTV.text = data.accountsAll.single { it._id == inOut.accountId }.name
                holder.detailsTL.removeAllViews()
                inOut.inOutDetail.forEach { detail ->
                    val item = data.itemsAll.single { it._id == detail.first }
                    val unit = data.unitsAll.single { it._id == item.unitId }
                    val labelTV = TextView(this@TrackActivity)
                    val valueTV = TextView(this@TrackActivity)
                    val rowTR = TableRow(this@TrackActivity)
                    labelTV.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    labelTV.text = String.format("▪ %s", item.itemName)
                    labelTV.maxLines = 1
                    labelTV.ellipsize = TextUtils.TruncateAt.END
                    rowTR.addView(labelTV)
                    valueTV.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                    valueTV.text = String.format("%1\$s %2\$s", DecimalFormat("0.##").format(detail.second / unit.value), unit.unitName)
                    valueTV.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                    rowTR.addView(valueTV)
                    holder.detailsTL.addView(rowTR)
                }
            } else {
                holder.dateTV.text = "TOTAL"
                holder.locationTV.visibility = View.GONE
                holder.purposeTV.visibility = View.GONE
                holder.accountableTV.visibility = View.GONE
                val itemIndex = ArrayList<Int>()
                val values = ArrayList<Double>()
                data.transactions.filter { filterInOut.contains(it._id) }.forEach {
                    it.inOutDetail.forEach { detail ->
                        if(itemIndex.any { it == detail.first }) values[itemIndex.indexOf(detail.first)] += detail.second
                        else { itemIndex.add(detail.first); values.add(detail.second) }
                    }
                }
                holder.detailsTL.removeAllViews()
                val accumulativeTotal = itemIndex.mapIndexed { index, i -> Pair(i, values[index]) }
                accumulativeTotal.forEach { detail ->
                    val item = data.itemsAll.single { it._id == detail.first }
                    val unit = data.unitsAll.single { it._id == item.unitId }
                    val labelTV = TextView(this@TrackActivity)
                    val valueTV = TextView(this@TrackActivity)
                    val rowTR = TableRow(this@TrackActivity)
                    labelTV.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    labelTV.text = String.format("▪ %s", item.itemName)
                    labelTV.maxLines = 1
                    labelTV.ellipsize = TextUtils.TruncateAt.END
                    rowTR.addView(labelTV)
                    valueTV.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                    valueTV.text = String.format("%1\$s %2\$s", DecimalFormat("0.##").format(detail.second / unit.value), unit.unitName)
                    valueTV.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                    rowTR.addView(valueTV)
                    holder.detailsTL.addView(rowTR)
                }
            }
        }
        override fun getItemCount(): Int = filterInOut.size
    }
}
