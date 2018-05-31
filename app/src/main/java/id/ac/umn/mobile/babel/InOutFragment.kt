package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class InOutFragment : Fragment() {
    class TransactionItems(val itemId: Int, var amount: Int, var unitId: Int)
    var inOutItems = ArrayList<TransactionItems>()
    var listener : SharedPreferences.OnSharedPreferenceChangeListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_in_out, container, false)
    }
    override fun onStart() {
        super.onStart()
        val locationsSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_locations)
        val directionTV = activity.findViewById<TextView>(R.id.fragment_in_out_tv_direction)
        val purposeSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_purpose)
        val itemsRV = activity.findViewById<RecyclerView>(R.id.fragment_in_out_items_rv_items)

        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)

        val data = object : Data(){override fun onComplete() {
            if(isAdded){
                loadTransaction()
                if(!inOutItems.isEmpty()) itemsRV.layoutManager = GridLayoutManager(activity, inOutItems.size, GridLayoutManager.HORIZONTAL, false)
                locationsSpn.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, locationsActive.map { it.code })
                val act = pref.getString("ACTION","incoming")
                directionTV.text = if(act == "incoming") "←" else "→"
                purposeSpn.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item,
                        thirdPartiesActive.filter { act=="incoming" && it.role=="S" || act=="outgoing" && it.role=="C" }.map { it.tpName })

                (activity as MainActivity).locationID = locationsSpn.selectedItemId.toString()
                (activity as MainActivity).thirdPartyID = purposeSpn.selectedItemId.toString()
            }
        }}
        listener = SharedPreferences.OnSharedPreferenceChangeListener{ _, _ -> itemsRV.adapter.notifyDataSetChanged(); data.onComplete() }
        pref.registerOnSharedPreferenceChangeListener(listener)
        itemsRV.adapter = InOutFragmentRVAdapter(activity, data)
    }
    override fun onPause() {
        super.onPause()
        saveTransaction()
    }
    override fun onResume() {
        super.onResume()
        loadTransaction()
    }
    fun saveTransaction(){
        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit()
        pref.putString("ITEMS", inOutItems.joinToString(";") { String.format("%d,%d,%d", it.itemId, it.amount, it.unitId) })
        pref.apply()
    }
    fun loadTransaction(){
        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)
        val itemRaw = pref.getString("ITEMS", "").split(";")
        inOutItems.clear()
        if(!itemRaw.contains("")) itemRaw.forEach {
            val itemSpec = it.split(",").map { it.toInt() }
            inOutItems.add(TransactionItems(itemSpec[0], itemSpec[1], itemSpec[2]))
        }
        activity.findViewById<RecyclerView>(R.id.fragment_in_out_items_rv_items).adapter.notifyDataSetChanged()
    }
    inner class InOutFragmentRVAdapter(private val context : Context, private val data : Data) : RecyclerView.Adapter<InOutFragmentRVAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var removeBtn : Button      = itemView.findViewById(R.id.fragment_in_out_btn_remove)
            var thumbnailIV : ImageView = itemView.findViewById(R.id.fragment_in_out_recycler_view_iv_thumbnail)
            var nameTV: TextView        = itemView.findViewById(R.id.fragment_in_out_recycler_view_tv_name)
            var stockTV: TextView       = itemView.findViewById(R.id.fragment_in_out_recycler_view_tv_stock)
            var signTV : TextView       = itemView.findViewById(R.id.fragment_in_out_tv_sign)
            var amountNP : NumberPicker = itemView.findViewById(R.id.fragment_in_out_np_amount)
            var unitSpn : Spinner       = itemView.findViewById(R.id.fragment_in_out_spn_unit)
        }
        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : InOutFragmentRVAdapter.ViewHolder{
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_in_out_recycler_view_item, parent, false)
            val card = view.findViewById(R.id.fragment_in_out_recycler_view_cv_item) as CardView
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder : InOutFragmentRVAdapter.ViewHolder, position : Int) {
            val locationsSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_locations)
            val item: Item = data.itemsActive.single { it._id == inOutItems[position].itemId }
            val unitFrom: Unit = data.unitsActive.single { it._id == item.unitId }
            val unitAvail = data.unitsActive.filter { it.measure == unitFrom.measure }
            var unitTo: Unit = data.unitsActive.single { it._id == inOutItems[position].unitId }
            val sign = if(activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).getString("ACTION", "incoming") == "incoming") 1 else -1
            val df = DecimalFormat((activity as MainActivity).globalPref!!.getString("global_item_precision", "0.##"))
            holder.removeBtn.setOnClickListener {
                inOutItems.removeAt(position)
                saveTransaction()
                notifyDataSetChanged()
            }
            holder.nameTV.text = item.itemName
            holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                    df.format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                    df.format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                    unitFrom.unitName
            )
            holder.thumbnailIV.setImageResource(R.drawable::class.java.getField(item.thumbnail).getInt(null))
            holder.signTV.text = if (sign == 1) "+" else "-"
            holder.amountNP.minValue = 0
            holder.amountNP.maxValue =
                    if (sign == 1) (activity as MainActivity).globalPref!!.getString("in_out_incoming_max", "999").toInt()
                    else (item.stocks[locationsSpn.selectedItemPosition] / unitTo.value).toInt()
            holder.amountNP.value = inOutItems[position].amount
            inOutItems[position].amount = holder.amountNP.value
            holder.amountNP.setFormatter { df.format(it.toDouble() * unitTo.increment) }
            holder.amountNP.setOnValueChangedListener { numberPicker, _, _ ->
                inOutItems[position].amount = numberPicker.value
                holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                        df.format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                        df.format(((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value) + (sign * numberPicker.value * unitTo.value / unitFrom.value) * unitTo.increment)),
                        unitFrom.unitName
                )
            }
            holder.unitSpn.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, unitAvail.map { it.unitName })
            holder.unitSpn.setSelection(unitAvail.indexOf(unitTo))
            holder.unitSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    unitTo = unitAvail[p2]
                    inOutItems[position].unitId = unitTo._id
                    holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                            df.format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                            df.format(((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value) + (sign * holder.amountNP.value * unitTo.value / unitFrom.value) * unitTo.increment)),
                            unitFrom.unitName
                    )
                    val inOutIncomingMax = (activity as MainActivity).globalPref!!.getString("in_out_incoming_max", "999").toDouble()
                    holder.amountNP.maxValue =
                            if (sign == 1) inOutIncomingMax.toInt()
                            else Math.min(item.stocks[locationsSpn.selectedItemPosition] / unitTo.increment / unitTo.value, inOutIncomingMax).toInt()
                }
            }
        }
        override fun getItemCount() : Int = inOutItems.size
    }
}