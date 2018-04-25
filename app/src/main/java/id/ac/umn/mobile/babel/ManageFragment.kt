package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class ManageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }
    override fun onStart() {
        super.onStart()

        val itemsRV = activity.findViewById<RecyclerView>(R.id.fragment_manage_items_rv_items)
        var data = object : Data(){
            override fun onComplete() {
                itemsRV.layoutManager = GridLayoutManager(activity, items.size, GridLayoutManager.HORIZONTAL, false)
            }
        }
        itemsRV.adapter = ManageFragmentRVAdapter(activity, data)
    }
    inner class ManageFragmentRVAdapter(private val context : Context, private val data : Data) : RecyclerView.Adapter<ManageFragmentRVAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var thumbnailIV : ImageView
            var nameTV: TextView
            var stockTV: TextView
            var locationTV : TextView
            var itemContextTb : Toolbar

            init {
                thumbnailIV = itemView.findViewById(R.id.fragment_manage_recycler_view_iv_thumbnail)
                nameTV = itemView.findViewById(R.id.fragment_manage_recycler_view_tv_name)
                stockTV = itemView.findViewById(R.id.fragment_manage_recycler_view_tv_stock)
                locationTV = itemView.findViewById(R.id.fragment_manage_recycler_view_tv_location)
                itemContextTb = itemView.findViewById(R.id.fragment_manage_recycler_view_tb_item_context)
            }
        }
        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : ManageFragmentRVAdapter.ViewHolder{
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_manage_recycler_view_item, parent, false)
            val card = view.findViewById(R.id.fragment_manage_recycler_view_cv_item) as CardView
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder : ManageFragmentRVAdapter.ViewHolder, position : Int){
            var item : Item = data.items[position]
            holder.nameTV.text = item.itemName
            holder.stockTV.text = String.format("%1\$.2f %2\$s", (item.stock / data.units.find { it._id==item.unit_id }!!.value), data.units.find { it._id==item.unit_id }!!.unit_name)
            holder.locationTV.text = item.location
            holder.thumbnailIV.setImageResource(R.drawable::class.java.getField(item.thumbnail).getInt(null))
            holder.itemContextTb.inflateMenu(R.menu.fragment_manage_menu_item)
            holder.itemContextTb.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_item_act_view -> {
                        Toast.makeText(context, "CIE VIEW", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.menu_item_act_edit -> {
                        Toast.makeText(context, "CIE EDIT", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.menu_item_act_delete -> {
                        Toast.makeText(context, "CIE DELETE", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> {
                        Toast.makeText(context, "CIE GTW", Toast.LENGTH_SHORT).show()
                        true
                    }
                }
            }
        }
        override fun getItemCount() : Int{
            return data.items.size
        }
    }
}