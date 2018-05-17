package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Handler
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
//      mengoveride function pada saat onStart
        super.onStart()
//      value locationsSpn pada activity spinner dengan R.id.fragment_in_out_spn_locations
        val locationsSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_locations)
//      value directionTV pada activity spinner dengan R.id.fragment_in_out_tv_direction
        val directionTV = activity.findViewById<TextView>(R.id.fragment_in_out_tv_direction)
//      value purposeSpn pada activity spinner dengan R.id.fragment_in_out_spn_purpose
        val purposeSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_purpose)
//      value itemsRV pada activity spinner dengan R.id.fragment_in_out_items_rv_items
        val itemsRV = activity.findViewById<RecyclerView>(R.id.fragment_in_out_items_rv_items)

        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)

        val data = object : Data(){override fun onComplete() {
            if(isAdded){
                loadTransaction()
                if(inOutItems.isEmpty()){
//                    val r = Random()
//                    items.forEach {
//                        if(r.nextInt(100)>55)
//                            inOutItems.add(TransactionItems(it._id, 0, it.unit_id))
//                    }
                    Toast.makeText(activity, "There's nothing here yet", Toast.LENGTH_SHORT).show()
                }
                else
                    itemsRV.layoutManager = GridLayoutManager(activity, inOutItems.size, GridLayoutManager.HORIZONTAL, false)
                locationsSpn.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, locations.map { it.code })
                val act = pref.getString("ACTION","incoming")
                directionTV.text = if(act == "incoming") "←" else "→"
                purposeSpn.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item,
                        thirdParties.filter { it.tp_status=="active"&&(act=="incoming"&&it.tp_type=="S"||act=="outgoing"&&it.tp_type=="C") }.map { it.tp_name })
            }
        }}
        listener = SharedPreferences.OnSharedPreferenceChangeListener{ _, _ -> itemsRV.adapter.notifyDataSetChanged(); data.onComplete() }
        pref.registerOnSharedPreferenceChangeListener(listener)
        itemsRV.adapter = InOutFragmentRVAdapter(activity, data)
    }
//  mengoverride function pada saat activity tidak ditampilkan
    override fun onStop() {
        super.onStop()
        saveTransaction()
    }

//  mengoverride function pada saat dibuka kembali
    override fun onResume() {
        super.onResume()
        loadTransaction()
    }
//
    fun saveTransaction(){
//    nilai pref > getSharedPreference
        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit()
//
        pref.putString("ITEMS", inOutItems.joinToString(";") { String.format("%d,%d,%d", it.itemId, it.amount, it.unitId) })
        pref.apply()
    }
//
    fun loadTransaction(){
//
        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)
        val itemRaw = pref.getString("ITEMS", "").split(";")
//
        inOutItems.clear()
        if(!itemRaw.contains("")) itemRaw.forEach {
            val itemSpec = it.split(",").map { it.toInt() }
            inOutItems.add(TransactionItems(itemSpec[0], itemSpec[1], itemSpec[2]))
        }
    }
//  inner class inOutFragmentRVAdapter > inner class ViewHolder
    inner class InOutFragmentRVAdapter(private val context : Context, private val data : Data) : RecyclerView.Adapter<InOutFragmentRVAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//          var removeBtn dari button dengan R.id.fragment_in_out_btn_remove
            var removeBtn : Button      = itemView.findViewById(R.id.fragment_in_out_btn_remove)
//          var thumbnailIV dari ImageView dengan R.id.fragment_in_out_recycler_view_iv_thumbnail
            var thumbnailIV : ImageView = itemView.findViewById(R.id.fragment_in_out_recycler_view_iv_thumbnail)
//          var nameTV dari TextView dengan R.id.fragment_in_out_recycler_view_tv_name
            var nameTV: TextView        = itemView.findViewById(R.id.fragment_in_out_recycler_view_tv_name)
//          var stockTV dari TextViewdengan R.id.fragment_in_out_recycler_view_tv_stock
            var stockTV: TextView       = itemView.findViewById(R.id.fragment_in_out_recycler_view_tv_stock)
//          var signTV dari TextView dengan R.id.fragment_in_out_tv_sign
            var signTV : TextView       = itemView.findViewById(R.id.fragment_in_out_tv_sign)
//          var amountNP dari NumberPicker dengan R.id.fragment_in_out_np_amount
            var amountNP : NumberPicker = itemView.findViewById(R.id.fragment_in_out_np_amount)
//          var unitSpn dari Spinner dengan R.id.fragment_in_out_spn_unit
            var unitSpn : Spinner       = itemView.findViewById(R.id.fragment_in_out_spn_unit)
        }
//      mengoverride function ketika membuat supaya menampilkan fungsi dari parent viewGroup dengan tipe data integer
//
        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : InOutFragmentRVAdapter.ViewHolder{
//          layout tujuan fragment_in_out_recycler_view_item
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_in_out_recycler_view_item, parent, false)
//          value dari card berasal dari findViewById dari R.id.fragment_in_out_recycler_view_cv_item sebagai CardView
            val card = view.findViewById(R.id.fragment_in_out_recycler_view_cv_item) as CardView
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
//          kembali ke ViewHolder
            return ViewHolder(view)
        }
//      mengoverride function ketika mengisi data (InOutFragmentRVAdapter) pada posisi terntentu (position : Int)
//      semua data pada lokasi tertentu
//        val locationsSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_locations)
//        val item: Item = data.items.single { it._id == inOutItems[position].itemId }
//        val unitFrom: Unit = data.units.single { it._id == item.unit_id }
//        val unitAvail = data.units.filter { it.measure == unitFrom.measure }
//        var unitTo: Unit = data.units.single { it._id == inOutItems[position].unitId }
//       nilai posisi pada onBindViewHolder
        override fun onBindViewHolder(holder : InOutFragmentRVAdapter.ViewHolder, position : Int) {
            val locationsSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_locations)
            val item: Item = data.items.single { it._id == inOutItems[position].itemId }
            val unitFrom: Unit = data.units.single { it._id == item.unit_id }
            val unitAvail = data.units.filter { it.measure == unitFrom.measure }
            var unitTo: Unit = data.units.single { it._id == inOutItems[position].unitId }
            val sign = if(activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).getString("ACTION", "incoming") == "incoming") 1 else -1
            holder.removeBtn.setOnClickListener {
                inOutItems.removeAt(position)
                notifyDataSetChanged()
            }
            holder.nameTV.text = item.itemName
            holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                    DecimalFormat("0.##").format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                    DecimalFormat("0.##").format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                    unitFrom.unit_name
            )
            holder.thumbnailIV.setImageResource(R.drawable::class.java.getField(item.thumbnail).getInt(null))
            holder.signTV.text = if (sign == 1) "+" else "-"
            holder.amountNP.minValue = 0
            holder.amountNP.maxValue = if (sign == 1) 9999 else (item.stocks[locationsSpn.selectedItemPosition] / unitTo.value).toInt()
            holder.amountNP.value = inOutItems[position].amount
            inOutItems[position].amount = holder.amountNP.value
            holder.amountNP.setFormatter { DecimalFormat("0.##").format(it.toDouble() * unitFrom.increment) }
            holder.amountNP.setOnValueChangedListener { numberPicker, _, _ ->
                inOutItems[position].amount = numberPicker.value
                holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                        DecimalFormat("0.##").format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                        DecimalFormat("0.##").format(((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value) + (sign * numberPicker.value * unitTo.value / unitFrom.value))),
                        unitFrom.unit_name
                )
            }
            holder.unitSpn.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, unitAvail.map { it.unit_name })
            holder.unitSpn.setSelection(unitAvail.indexOf(unitTo))
            holder.unitSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    unitTo = unitAvail[p2]
                    inOutItems[position].unitId = unitTo._id
                    holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                            DecimalFormat("0.##").format((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value)),
                            DecimalFormat("0.##").format(((item.stocks[locationsSpn.selectedItemPosition] / unitFrom.value) + (sign * holder.amountNP.value * unitTo.value / unitFrom.value))),
                            unitFrom.unit_name
                    )
                    holder.amountNP.maxValue = if (sign == 1) 9999 else (item.stocks[locationsSpn.selectedItemPosition] / unitTo.value).toInt()
                }
            }
        }
        override fun getItemCount() : Int = inOutItems.size
    }
}