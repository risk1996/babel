package id.ac.umn.mobile.babel

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class RVAdapter(private val context : Context, private val list : List<Item>) : RecyclerView.Adapter<RVAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    override fun onCreateViewHolder(parent : ViewGroup, type : Int) : RVAdapter.ViewHolder{
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_manage_recycler_view_item, parent, false)
        val card = view.findViewById(R.id.fragment_manage_recycler_view_cv_item) as CardView
        card.maxCardElevation = 2.0F
        card.radius = 5.0F
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder : RVAdapter.ViewHolder, position : Int){
        var item : Item = list.get(position)
        holder.nameTV.text = item.itemName
        holder.stockTV.text = item.stock.toString()
        holder.thumbnailIV.setImageResource(Resources.getSystem().getIdentifier(item.thumbnail,"drawable", context.packageName))
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
        return list.size
    }

}
