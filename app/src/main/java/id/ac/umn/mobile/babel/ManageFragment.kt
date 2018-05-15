package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat

class ManageFragment : Fragment() {
    val filterItems = ArrayList<Int>()
//    menampilkan layout dari class manage fragments
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }
    override fun onStart() {
//        start activity
        super.onStart()
        val itemsRV = activity.findViewById<RecyclerView>(R.id.fragment_manage_items_rv_items)
        val searchET = activity.findViewById<EditText>(R.id.fragment_manage_items_et_search)

        val data = object : Data(){
            override fun onComplete() {
                filterItems.clear()
                items.filter { it.itemName.toLowerCase().contains(searchET.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach { filterItems.add(it._id) }
                itemsRV.layoutManager = GridLayoutManager(activity, if(filterItems.size>0)filterItems.size else 1, GridLayoutManager.HORIZONTAL, false)
            }
        }
        itemsRV.adapter = ManageFragmentRVAdapter(activity, data)
        searchET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterItems.clear()
                data.items.filter { it.itemName.toLowerCase().contains(searchET.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach { filterItems.add(it._id) }
                itemsRV.layoutManager = GridLayoutManager(activity, if(filterItems.size>0)filterItems.size else 1, GridLayoutManager.HORIZONTAL, false)
            }
        })
    }
    class AreYouSureAboutThatDialog : YesNoDialog(){
        override fun onYesClicked() {
            val data = object : Data() {
                override fun onComplete() {
                    if (isAdded){
                        val itemToDelete = items.single { it.itemName == value }
                        val db = FirebaseDatabase.getInstance().reference.child("items")
                        db.child(itemToDelete._id.toString()).removeValue()
                    }
                }
            }
            Snackbar.make(activity.findViewById(android.R.id.content), "Item successfully deleted", Snackbar.LENGTH_LONG).show()
        }
        override fun onNoClicked() {
            Snackbar.make(activity.findViewById(android.R.id.content), "Item is not deleted", Snackbar.LENGTH_LONG).show()
        }
    }
    class DeleteDialog : YesNoDialog(){
        override fun onYesClicked() {
            val dialog = AreYouSureAboutThatDialog()
            dialog.isCancelable = false
            dialog.heading = "Are you really sure?"
            dialog.message = "This process is irreversible"
            dialog.value = value
            dialog.highlight = dialog.HIGHLIGHT_NO
            dialog.show(fragmentManager, "Dialog Yes No")
        }
        override fun onNoClicked() {
            Snackbar.make(activity.findViewById(android.R.id.content), "Item is not deleted", Snackbar.LENGTH_LONG).show()
        }
    }
    inner class ManageFragmentRVAdapter(private val context : Context, private val data : Data) : RecyclerView.Adapter<ManageFragmentRVAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var thumbnailIV : ImageView         = itemView.findViewById(R.id.fragment_manage_recycler_view_iv_thumbnail)
            var nameTV: TextView                = itemView.findViewById(R.id.fragment_manage_recycler_view_tv_name)
            var itemLocationsTL : TableLayout   = itemView.findViewById(R.id.fragment_manage_recycler_view_tl_items_locations)
            var itemContextTb : Toolbar         = itemView.findViewById(R.id.fragment_manage_recycler_view_tb_item_context)
        }
        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : ManageFragmentRVAdapter.ViewHolder{
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_manage_recycler_view_item, parent, false)
            val card = view.findViewById(R.id.fragment_manage_recycler_view_cv_item) as CardView
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder : ManageFragmentRVAdapter.ViewHolder, position : Int){
            val item : Item = data.items.single { it._id==filterItems[position] }
            val unit : Unit = data.units.find { it._id==item.unit_id }!!
            val rowTR = TableRow(activity)
            val locTV = TextView(activity)
            val stkTV = TextView(activity)
            val oosIV = ImageView(activity)
            val param = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            holder.nameTV.text = item.itemName
            holder.itemLocationsTL.removeViews(1, holder.itemLocationsTL.childCount-1)
            data.locations.forEach {
                locTV.layoutParams = param
                locTV.text = String.format("%1\$s    : ", it.code)
                locTV.setPadding(40, 0, 10, 5)
                rowTR.addView(locTV)
                stkTV.layoutParams = param
                stkTV.text = String.format("%1\$s %2\$s", DecimalFormat("0.##").format(item.stocks[it._id] / unit.value), unit.unit_name)
                stkTV.setPadding(40, 0, 10, 5)
                rowTR.addView(stkTV)
                oosIV.setImageResource(R.drawable.icons8_error_24)
                oosIV.setColorFilter(Color.rgb(255, 0, 0))
                if(item.stocks[it._id] >= item.safetyStock) oosIV.visibility = View.INVISIBLE
                oosIV.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0f)
                rowTR.addView(oosIV)
                holder.itemLocationsTL.addView(rowTR)
            }
            holder.thumbnailIV.setImageResource(R.drawable::class.java.getField(item.thumbnail).getInt(null))
            holder.itemContextTb.menu.clear()
            holder.itemContextTb.inflateMenu(R.menu.fragment_manage_menu_item)
            holder.itemContextTb.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_item_act_view -> {
//                        Toast.makeText(context, "CIE VIEW", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, ItemActivity::class.java)
                        intent.putExtra("OPERATION", "VIEW")
                        intent.putExtra("ITEM_ID", item._id)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_item_act_edit -> {
                        val intent = Intent(activity, ItemActivity::class.java)
                        intent.putExtra("OPERATION", "EDIT")
                        intent.putExtra("ITEM_ID", item._id)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_item_act_delete -> {
                        val dialog = DeleteDialog()
                        dialog.isCancelable = false
                        dialog.heading = "Delete Item"
                        dialog.message = "Are you sure you want to delete this item?"
                        dialog.value = holder.nameTV.text.toString()
                        dialog.highlight = dialog.HIGHLIGHT_NO
                        dialog.show(fragmentManager, "Dialog Yes No")
                        true
                    }
                    else -> {true }
                }
            }
        }
//        fungsi itemcount
        override fun getItemCount() : Int{
            return filterItems.size
        }
    }
}