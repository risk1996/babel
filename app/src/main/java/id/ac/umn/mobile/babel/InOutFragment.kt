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
        inOutSpn.adapter = ArrayAdapter.createFromResource(activity, R.array.spinner_in_out, android.R.layout.simple_spinner_item)
        var data = object : Data(){override fun onComplete() {
            if(isAdded){
                val r = Random()
                items.forEach { if(r.nextInt(100)>55)inOutItems.add(it._id) }
                itemsRV.layoutManager = GridLayoutManager(activity, inOutItems.size, GridLayoutManager.HORIZONTAL, false)
            }
        }}
        itemsRV.adapter = InOutFragmentRVAdapter(activity, data)
    }
    inner class InOutFragmentRVAdapter(private val context : Context, private val data : Data) : RecyclerView.Adapter<InOutFragmentRVAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var removeBtn : Button
            var thumbnailIV : ImageView
            var nameTV: TextView
            var stockTV: TextView
            var signTV : TextView
            var amountNP : NumberPicker
            var unitSpn : Spinner

            init {
                removeBtn = itemView.findViewById(R.id.fragment_in_out_btn_remove)
                thumbnailIV = itemView.findViewById(R.id.fragment_in_out_recycler_view_iv_thumbnail)
                nameTV = itemView.findViewById(R.id.fragment_in_out_recycler_view_tv_name)
                stockTV = itemView.findViewById(R.id.fragment_in_out_recycler_view_tv_stock)
                signTV = itemView.findViewById(R.id.fragment_in_out_tv_sign)
                amountNP = itemView.findViewById(R.id.fragment_in_out_np_amount)
                unitSpn = itemView.findViewById(R.id.fragment_in_out_spn_unit)
            }
        }
        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : InOutFragmentRVAdapter.ViewHolder{
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_in_out_recycler_view_item, parent, false)
            val card = view.findViewById(R.id.fragment_in_out_recycler_view_cv_item) as CardView
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder : InOutFragmentRVAdapter.ViewHolder, position : Int){
            Log.d("POSITION", position.toString())
            var item : Item = data.items.single { it._id==inOutItems[position] }
            var unit : Unit = data.units.single { it._id==item.unit_id }
            holder.nameTV.text = item.itemName
            holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                    DecimalFormat("0.#").format((item.stock / unit.value)),
                    DecimalFormat("0.#").format((item.stock / unit.value)),
                    data.units.find { it._id==item.unit_id }!!.unit_name
            )
            holder.thumbnailIV.setImageResource(R.drawable::class.java.getField(item.thumbnail).getInt(null))
            holder.amountNP.minValue = 0
            holder.amountNP.maxValue = 999
            val values = arrayOfNulls<String>(1000)
            for(i in 0..999)values[i] = String.format("%1\$s",
                    DecimalFormat("0.#").format(i.toDouble() * data.units.find { it._id==item.unit_id }!!.increment),
                    data.units.find { it._id==item.unit_id }!!.unit_name
            )
            holder.amountNP.displayedValues = values
            holder.amountNP.setOnValueChangedListener { numberPicker, _, _ ->
                holder.stockTV.text = String.format("%1\$s → %2\$s  %3\$s",
                        DecimalFormat("0.#").format((item.stock / unit.value)),
                        DecimalFormat("0.#").format((item.stock / unit.value) + numberPicker.value),
                        unit.unit_name
                )
            }
            val units = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item)
            data.units.filter { it.measure==data.units.single { it._id==item.unit_id }.measure }.forEach { units.add(it.unit_name) }
            holder.unitSpn.adapter = units
            holder.unitSpn.setSelection(units.getPosition(data.units.single { it._id==item.unit_id }.unit_name))
        }
        override fun getItemCount() : Int{
            return inOutItems.size
        }
    }
}