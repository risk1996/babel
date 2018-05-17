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
        override fun onYesClicked() {
            val data = object : Data(){
                override fun onComplete() {
                    val pref = activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)
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
                        db.child(itemSpec[0].toString()).child("stocks").child("0")
                                .setValue( (((item.stocks[0] / unitFrom.value ) + (itemSpec[1] / unitTo.value * unitFrom.value)) * unitFrom.value).toString() )
                    }
                    db.onDisconnect()
                }
            }
            Snackbar.make( activity.findViewById(android.R.id.content), "Changes have been committed", Snackbar.LENGTH_LONG).show()
        }
        override fun onNoClicked() {
            Snackbar.make( activity.findViewById(android.R.id.content), "No changes have been made", Snackbar.LENGTH_LONG).show()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v : View? = null
        val tab = activity!!.findViewById<TabLayout>(R.id.activity_main_tl_tabs).selectedTabPosition
        when(tab){
            0 -> {
                v = inflater.inflate(R.layout.modal_main_manage, container, false)!!
                val newItemBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_new_item)
                val ediItemsBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_edit_items)
                val deleteItemsBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_delete_items)
                val newUnitBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_new_unit)
                val editUnitsBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_edit_units)
                val deleteUnitsBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_delete_units)
                newItemBtn.setOnClickListener {
                    val intent = Intent(activity, ItemActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                }
                ediItemsBtn.setOnClickListener { Toast.makeText(activity, "NAH EDIT ITEM", Toast.LENGTH_SHORT).show() }
                deleteItemsBtn.setOnClickListener { Toast.makeText(activity, "NAH DELETE ITEM", Toast.LENGTH_SHORT).show() }
                newUnitBtn.setOnClickListener { Toast.makeText(activity, "NAH NEW UNIT", Toast.LENGTH_SHORT).show() }
                editUnitsBtn.setOnClickListener { Toast.makeText(activity, "NAH EDIT UNIT", Toast.LENGTH_SHORT).show() }
                deleteUnitsBtn.setOnClickListener { Toast.makeText(activity, "NAH DELETE UNIT", Toast.LENGTH_SHORT).show() }
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
                    dialog.highlight = dialog.HIGHLIGHT_NO
                    dialog.show(activity!!.fragmentManager, "Dialog Yes No")
                    dismissAllowingStateLoss()
                }
            }
            2 -> {
                v = inflater.inflate(R.layout.modal_main_report, container, false)!!
                val editCompanyBtn = v.findViewById<Button>(R.id.modal_main_report_btn_edit_company)
                val printReportBtn = v.findViewById<Button>(R.id.modal_main_report_btn_print_report)
                val exportReportBtn = v.findViewById<Button>(R.id.modal_main_report_btn_export_report)
                val outOfStockBtn = v.findViewById<Button>(R.id.modal_main_report_btn_out_of_stock)
                editCompanyBtn.setOnClickListener { Toast.makeText(activity, "NAH EDIT COMPANY", Toast.LENGTH_SHORT).show() }
                printReportBtn.setOnClickListener { Toast.makeText(activity, "NAH PRINT REPORT", Toast.LENGTH_SHORT).show() }
                exportReportBtn.setOnClickListener { Toast.makeText(activity, "NAH EXPORT REPORT", Toast.LENGTH_SHORT).show() }
                outOfStockBtn.setOnClickListener { Toast.makeText(activity, "NAH OUT OF STOCK", Toast.LENGTH_SHORT).show() }
            }
            3 -> {
                v = inflater.inflate(R.layout.modal_main_user, container, false)!!
                val newUserBtn = v.findViewById<Button>(R.id.modal_main_user_btn_new_user)
                val editUsersBtn = v.findViewById<Button>(R.id.modal_main_user_btn_edit_users)
                val deleteUsersBtn = v.findViewById<Button>(R.id.modal_main_user_btn_delete_users)
                newUserBtn.setOnClickListener { Toast.makeText(activity, "NAH NEW USER", Toast.LENGTH_SHORT).show() }
                editUsersBtn.setOnClickListener { Toast.makeText(activity, "NAH EDIT USERS", Toast.LENGTH_SHORT).show() }
                deleteUsersBtn.setOnClickListener { Toast.makeText(activity, "NAH DELETE USERS", Toast.LENGTH_SHORT).show() }
            }
        }
        return v
    }

}