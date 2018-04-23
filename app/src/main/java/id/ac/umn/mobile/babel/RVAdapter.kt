package id.ac.umn.mobile.babel

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class Item(val name : String, val info : String, val thumbnail : Int)
class RVAdapter(private val context : Context, private val list : List<Item>) : RecyclerView.Adapter<RVAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnailIV : ImageView
        var nameTV: TextView
        var infoTV: TextView
        var removeBim : ImageButton

        init {
            thumbnailIV = itemView.findViewById(R.id.recycler_view_iv_thumbnail)
            nameTV = itemView.findViewById(R.id.recycler_view_tv_name)
            infoTV = itemView.findViewById(R.id.recycler_view_tv_info)
            removeBim = itemView.findViewById(R.id.recycler_view_bim_remove)
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, type : Int) : RVAdapter.ViewHolder{
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false);
        val card = view.findViewById(R.id.recycler_view_cv_item) as CardView
        card.maxCardElevation = 2.0F
        card.radius = 5.0F
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder : RVAdapter.ViewHolder, position : Int){
        var item : Item = list.get(position)
        holder.nameTV.text = item.name
        holder.infoTV.text = item.info
        holder.thumbnailIV.setImageResource(item.thumbnail)
        holder.removeBim.setOnClickListener{
//            TODO("Tanya ke user confirm delete")
        }
    }
    override fun getItemCount() : Int{
        return list.size
    }

}
