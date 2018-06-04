package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainModal : BottomSheetDialogFragment() {
    var privilege : String = ""
    var accountID : String = ""
    var locationID : String = ""
    var thirdPartyID : String = ""
    class CommitDialog : YesNoDialog(){
        var incrementStock = 0
        val history = mutableMapOf<String, Any>()
        override fun onYesClicked() {
            val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)
            val itemRaw = pref.getString("ITEMS", "").split(";")
            if(!itemRaw.contains("")) itemRaw.forEach {
                var bool = true
                val itemSpec = it.split(",").map { it.toInt() }
                val data = object : Data(){
                    override fun onComplete() {
                        if (bool){
                            bool = false

                            val item = itemsActive.single { it._id==itemSpec[0] }
                            val unitFrom = unitsActive.single { it._id==item.unitId }
                            val unitTo = unitsActive.single { it._id==itemSpec[2] }
                            if(value == "outgoing") incrementStock = itemSpec[1] * (-1)
                            else if(value == "incoming") incrementStock = itemSpec[1]
                            val updateValue = item.stocks[0] + (incrementStock * unitTo.value * unitTo.increment)
                            history[itemSpec[0].toString()] = (incrementStock * unitTo.value * unitTo.increment)

                            val db = FirebaseDatabase.getInstance().reference.child("items")
                            db.runTransaction(object : Transaction.Handler{
                                override fun doTransaction(p0: MutableData?): Transaction.Result {
                                    p0!!.child(itemSpec[0].toString()).child("stocks").child("0").value = updateValue.toString()
                                    return Transaction.success(p0)
                                }
                                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {  }
                            })

                            val db2 = FirebaseDatabase.getInstance().reference.child("inout")
                            val inoutHistory = mutableMapOf<String, Any>()
                            inoutHistory["io_time"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            inoutHistory["account_id"] = accountID
                            inoutHistory["location_id"] = locationID
                            inoutHistory["third_party_id"] = thirdPartyID
                            inoutHistory["io_detail"] = history
                            val transactionID = if (transactions.count() == 0) 1 else transactions.last()._id + 1 // masih ngaco buat add new ID
                            db2.child(transactionID.toString()).setValue(inoutHistory)
                        }
                    }
                }
            }

            Snackbar.make( activity.findViewById(android.R.id.content), "Changes have been committed", Snackbar.LENGTH_LONG).show()
            activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit().clear().apply()
            (activity as MainActivity).inOutFragment.loadTransaction()
        }
        override fun onNoClicked() { Snackbar.make( activity.findViewById(android.R.id.content), "No changes have been made", Snackbar.LENGTH_LONG).show() }
    }
    class QuitDialog : YesNoDialog(){
        override fun onYesClicked() { activity.moveTaskToBack(true) }
        override fun onNoClicked() {}
    }
    class LogOutDialog : YesNoDialog(){
        override fun onYesClicked() { activity.finish(); startActivity(Intent(activity, LoginActivity::class.java)) }
        override fun onNoClicked() {}
    }
    private fun disableButton(btn: Button){
        btn.isEnabled = false
        btn.compoundDrawablesRelative.forEach { it?.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP) }
        btn.setTextColor(Color.LTGRAY)
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
                val manageThirdPartiesBtn = v.findViewById<Button>(R.id.modal_main_manage_btn_manage_third_parties)
                if (privilege == "User"){
                    disableButton(newItemBtn)
                    disableButton(manageUnitsBtn)
                    disableButton(manageLocationsBtn)
                    disableButton(manageThirdPartiesBtn)
                }
                newItemBtn.setOnClickListener {
                    val dialog = ListDialog()
                    dialog.content = "ITEM"
                    dialog.show(activity!!.fragmentManager, dialog.tag)
                    dismissAllowingStateLoss()
                }
                manageUnitsBtn.setOnClickListener {
                    val dialog = ListDialog()
                    dialog.content = "UNITS"
                    dialog.show(activity!!.fragmentManager, dialog.tag)
                    dismissAllowingStateLoss()
                }
                manageLocationsBtn.setOnClickListener {
                    val dialog = ListDialog()
                    dialog.content = "LOCATIONS"
                    dialog.show(activity!!.fragmentManager, dialog.tag)
                    dismissAllowingStateLoss()
                }
                manageThirdPartiesBtn.setOnClickListener {
                    val dialog = ListDialog()
                    dialog.content = "THIRD PARTIES"
                    dialog.show(activity!!.fragmentManager, dialog.tag)
                    dismissAllowingStateLoss()
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
                    (activity as MainActivity).inOutFragment.saveTransaction()
                    val dialog = ListDialog()
                    dialog.content = "IN OUT ITEM"
                    dialog.show(activity!!.fragmentManager, dialog.tag)
                    dismissAllowingStateLoss()
                }
                commitBtn.setOnClickListener {
                    (activity as MainActivity).inOutFragment.saveTransaction()
                    val pref = activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE) // function onComplete jadi infinite loop
                    val itemRaw = pref.getString("ITEMS", "").split(";")
                    if(!itemRaw.contains("")){
                        val dialog = CommitDialog()
                        dialog.isCancelable = false
                        dialog.heading = "Commit Changes"
                        dialog.message = "Are you sure you want to commit?"
                        dialog.value = activity!!.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).getString("ACTION","incoming")
                        dialog.accountID = accountID
                        dialog.locationID = locationID
                        dialog.thirdPartyID = thirdPartyID
                        dialog.highlight = dialog.HIGHLIGHT_NO
                        dialog.show(activity!!.fragmentManager, "Dialog Yes No")
                    }
                    else Toast.makeText(activity, "No items to commit", Toast.LENGTH_SHORT).show()
                    dismissAllowingStateLoss()
                }
            }
            2 -> {
                v = inflater.inflate(R.layout.modal_main_report, container, false)!!
                val editCompanyBtn = v.findViewById<Button>(R.id.modal_main_report_btn_edit_company)
                val trackIncomingBtn = v.findViewById<Button>(R.id.modal_main_report_btn_track_incoming)
                val trackOutgoingBtn = v.findViewById<Button>(R.id.modal_main_report_btn_track_outgoing)
                val outOfStockBtn = v.findViewById<Button>(R.id.modal_main_report_btn_out_of_stock)
                if (privilege == "User") disableButton(editCompanyBtn)
            }
            3 -> {
                v = inflater.inflate(R.layout.modal_main_user, container, false)!!
                val newUserBtn = v.findViewById<Button>(R.id.modal_main_user_btn_new_user)
                val settingsBtn = v.findViewById<Button>(R.id.modal_main_user_btn_settings)
                val logoutBtn = v.findViewById<Button>(R.id.modal_main_user_btn_logout)
                val quitBtn = v.findViewById<Button>(R.id.modal_main_user_btn_quit)
                if (privilege == "User") disableButton(newUserBtn)
                newUserBtn.setOnClickListener {
                    val intent = Intent(activity, UserActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
                settingsBtn.setOnClickListener {
                    startActivity(Intent(activity, SettingsActivity::class.java))
                }
                logoutBtn.setOnClickListener {
                    val dialog = LogOutDialog()
                    dialog.isCancelable = false
                    dialog.heading = "Log Out"
                    dialog.message = "Are you sure you want to log out?"
                    dialog.highlight = dialog.HIGHLIGHT_NO
                    dialog.show(activity!!.fragmentManager, "Dialog Yes No")
                    dismissAllowingStateLoss()
                }
                quitBtn.setOnClickListener {
                    val dialog = QuitDialog()
                    dialog.isCancelable = false
                    dialog.heading = "Exit"
                    dialog.message = "Are you sure you want to quit?"
                    dialog.highlight = dialog.HIGHLIGHT_NO
                    dialog.show(activity!!.fragmentManager, "Dialog Yes No")
                    dismissAllowingStateLoss()
                }
            }
        }
        return v
    }

}