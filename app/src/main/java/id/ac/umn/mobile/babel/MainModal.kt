package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.util.Hex
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest
import java.util.*

class MainModal : BottomSheetDialogFragment() {
    class CommitDialog : YesNoDialog(){
        var incrementStock = 0
//      val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE) // NullPointerException
        override fun onYesClicked() {
//            val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE) // function onComplete jadi infinite loop
            val data = object : Data(){
                override fun onComplete() {
                    val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE) // KotlinNullPointerException, bisa dicurangin pake try catch tp kadang dia increment dua kali
                    val itemRaw = pref.getString("ITEMS", "").split(";")
                    val db = FirebaseDatabase.getInstance().reference.child("items")
                    if(!itemRaw.contains("")) itemRaw.forEach {
                        val itemSpec = it.split(",").map { it.toInt() }
                        val item = items.single { it._id==itemSpec[0] }
                        val unitFrom = units.single { it._id==item.unit_id }
                        val unitTo = units.single { it._id==itemSpec[2] }
                        Log.d("", "itemSpec[0]    : " + itemSpec[0])
                        Log.d("", "itemSpec[1]    : " + itemSpec[1])
                        Log.d("", "itemSpec[2]    : " + itemSpec[2])
                        Log.d("", "item.stocks[0] : " + item.stocks[0])
                        Log.d("", "item.unit_id   : " + item.unit_id)
                        Log.d("", "unitFrom.value : " + unitFrom.value)
                        Log.d("", "unitTo.value   : " + unitTo.value)
                        if(value == "outgoing") incrementStock = itemSpec[1]*(-1)
                        else if(value == "incoming") incrementStock = itemSpec[1]
                        db.child(itemSpec[0].toString()).child("stocks").child("0")
                                .setValue( (((item.stocks[0] / unitFrom.value ) + (incrementStock.toDouble() / unitTo.value * unitFrom.value)) * unitFrom.value).toDouble().toString() )
                    }
                    Log.d("", "itemSpec[0]   lalalalalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                }
            }
            Snackbar.make( activity.findViewById(android.R.id.content), "Changes have been committed", Snackbar.LENGTH_LONG).show()
        }
        override fun onNoClicked() { Snackbar.make( activity.findViewById(android.R.id.content), "No changes have been made", Snackbar.LENGTH_LONG).show() }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v : View? = null
        val tab = activity!!.findViewById<TabLayout>(R.id.activity_main_tl_tabs).selectedTabPosition
        when(tab){
            0 -> {
                v = inflater.inflate(R.layout.modal_main_manage, container, false)!!
                val newItemBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_new_item)
                val manageUnitsBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_manage_units)
                val manageLocationsBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_manage_locations)
                val managethirdPartiesBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_manage_third_parties)
                newItemBtn.setOnClickListener {
                    val intent = Intent(activity, ItemActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                }
            }
            1 -> {
                v = inflater.inflate(R.layout.modal_main_in_out, container, false)!!
                val incomingBtn = v.findViewById<Button>(R.id.modal_main_in_out_btn_incoming)
                val outgoingBtn = v.findViewById<Button>(R.id.modal_main_in_out_btn_outgoing)
                val addItemBtn = v.findViewById<Button>(R.id.modal_main_in_out_btn_add_item)
                val commitBtn = v.findViewById<Button>(R.id.modal_main_in_out_btn_commit)
                incomingBtn.setOnClickListener {
                    val act = activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).getString("ACTION","incoming")
                    var snack = "Activity changed to incoming item(s)"
                    if(act == "incoming") snack = "Activity did not change"
                    else activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit().putString("ACTION", "incoming").apply()
                    Snackbar.make(activity!!.findViewById(android.R.id.content), snack, Snackbar.LENGTH_LONG).show()
                    dismissAllowingStateLoss()
                }
                outgoingBtn.setOnClickListener {
                    val act = activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).getString("ACTION","incoming")
                    var snack = "Activity changed to outgoing item(s)"
                    if(act == "outgoing") snack = "Activity did not change"
                    else activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit().putString("ACTION", "outgoing").apply()
                    Snackbar.make(activity!!.findViewById(android.R.id.content), snack, Snackbar.LENGTH_LONG).show()
                    dismissAllowingStateLoss()
                }
                addItemBtn.setOnClickListener {
                    val dialog = AddInOutDialog()
                    dialog.show(activity!!.fragmentManager, dialog.tag)
                    dismissAllowingStateLoss()
                }
                commitBtn.setOnClickListener {
                    val dialog = CommitDialog()
                    dialog.isCancelable = false
                    dialog.heading = "Commit Changes"
                    dialog.message = "Are you sure you want to commit?"
                    dialog.value = activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).getString("ACTION","incoming")
                    dialog.highlight = dialog.HIGHLIGHT_NO
                    dialog.show(activity!!.fragmentManager, "Dialog Yes No")
                    dismissAllowingStateLoss()
                }
            }
            2 -> {
                v = inflater.inflate(R.layout.modal_main_report, container, false)!!
                val editCompanyBtn = v.findViewById<Button>(R.id.modal_main_report_btn_edit_company)
                val trackIncomingBtn = v.findViewById<Button>(R.id.modal_main_report_btn_track_incoming)
                val trackOutgoingBtn = v.findViewById<Button>(R.id.modal_main_report_btn_track_outgoing)
                val outOfStockBtn = v.findViewById<Button>(R.id.modal_main_report_btn_out_of_stock)
            }
            3 -> {
                v = inflater.inflate(R.layout.modal_main_user, container, false)!!
                val newUserBtn = v.findViewById<Button>(R.id.modal_main_user_btn_new_user)
                val settingsBtn = v.findViewById<Button>(R.id.modal_main_user_btn_settings)
                val logoutBtn = v.findViewById<Button>(R.id.modal_main_user_btn_logout)
                val quitBtn = v.findViewById<Button>(R.id.modal_main_user_btn_quit)
            }
        }
        return v
    }

}