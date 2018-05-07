package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.media.Image
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class InOutFragment : Fragment() {
    val inOutItems = ArrayList<Int>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_in_out, container, false)
    }
    override fun onStart() {
        super.onStart()
        val inOutSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_in_out)
        val itemsRV = activity.findViewById<RecyclerView>(R.id.fragment_in_out_items_rv_items)
        var data = object : Data(){override fun onComplete() {
            if(isAdded){
                val r = Random()
                items.forEach { if(r.nextInt(100)>55)inOutItems.add(it._id) }
                itemsRV.layoutManager = GridLayoutManager(activity, inOutItems.size, GridLayoutManager.HORIZONTAL, false)
                inOutSpn.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, locations.map { String.format("%1\$s: %2\$s", it.code, it.position) }.toList())
            }
        }}
        itemsRV.adapter = InOutFragmentRVAdapter(activity, data)
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
            val inOutSpn = activity.findViewById<Spinner>(R.id.fragment_in_out_spn_in_out)
            val item: Item = data.items.single { it._id == inOutItems[position] }
            val unitFrom: Unit = data.units.single { it._id == item.unit_id }
            val unitAvail = data.units.filter { it.measure == unitFrom.measure }
            var unitTo: Unit = unitFrom
            val sign = 1
            holder.nameTV.text = item.itemName
            holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                    DecimalFormat("0.#").format((item.stocks[inOutSpn.selectedItemPosition] / unitFrom.value)),
                    DecimalFormat("0.#").format((item.stocks[inOutSpn.selectedItemPosition] / unitFrom.value)),
                    data.units.find { it._id == item.unit_id }!!.unit_name
            )
            holder.thumbnailIV.setImageResource(R.drawable::class.java.getField(item.thumbnail).getInt(null))
            holder.signTV.text = if (sign == 1) "+" else "-"
            holder.amountNP.minValue = 0
            holder.amountNP.maxValue = 999
            val values = arrayOfNulls<String>(1000)
            for (i in 0..999) values[i] = String.format("%1\$s",
                    DecimalFormat("0.#").format(i.toDouble() * data.units.find { it._id == item.unit_id }!!.increment),
                    data.units.find { it._id == item.unit_id }!!.unit_name
            )
            holder.amountNP.displayedValues = values
            holder.amountNP.setOnValueChangedListener { numberPicker, _, _ ->
                holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                        DecimalFormat("0.#").format((item.stocks[inOutSpn.selectedItemPosition] / unitFrom.value)),
                        DecimalFormat("0.#").format(((item.stocks[inOutSpn.selectedItemPosition] / unitFrom.value) + (sign * numberPicker.value * unitTo.value / unitFrom.value))),
                        unitFrom.unit_name
                )
            }
            holder.unitSpn.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, unitAvail.map { it.unit_name })
            holder.unitSpn.setSelection(unitAvail.indexOf(unitTo))
            holder.unitSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    unitTo = unitAvail[p2]
                    holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                            DecimalFormat("0.#").format((item.stocks[inOutSpn.selectedItemPosition] / unitFrom.value)),
                            DecimalFormat("0.#").format(((item.stocks[inOutSpn.selectedItemPosition] / unitFrom.value) + (sign * holder.amountNP.value * unitTo.value / unitFrom.value))),
                            unitFrom.unit_name
                    )
                }
            }
        }
        override fun getItemCount() : Int{
            return inOutItems.size
        }
    }
}