package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.util.Hex
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest
import java.util.*

class MainModal : BottomSheetDialogFragment() {
    class CommitDialog : YesNoDialog(){
        override fun onYesClicked() {}
        override fun onNoClicked() {}
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
                newItemBtn.setOnClickListener { Toast.makeText(activity, "NAH NEW ITEM", Toast.LENGTH_SHORT).show() }
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
                    val intent = Intent(activity, ItemActivity::class.java).putExtra("OPERATION", "In")
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
                commitBtn.setOnClickListener {
                    // get value dari semua yg ada di sananya trus setvalue ke firebase
                    // tapi caranya gimana gw mau set nilai dari ItemActivity aja masih bingung caranya
                    // atau gwnya yg bego harusnya ambil nilai ID tp gw malah manggil namanya
                    // hmmmm susah bener ini
                    // ntar paling minta diajarin ya yan hahahahaha
                    // mungkin karena udh malem aja makanya agak rada2

//                    val data = object : Data(){ override fun onComplete() {
//                        if(isAdded) {
//                            InOutFragment().loadTransaction()
//                            val account = accounts.single { it.email == pref.getString("EMAIL", "") }
//                            val salt = account.salt
//                            val newSalt = UUID.randomUUID().toString().substring(25, 30)
//                            val oldPassword = Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((currPassET.text.toString() + salt).toByteArray()))
//                            if (account.password == oldPassword/* && newPassET.text == confirmNewPassET.text*/) {
//                                currPassErrorTV.visibility = View.GONE
//                                Snackbar.make(activity.findViewById(android.R.id.content), "Password successfully changed", Snackbar.LENGTH_LONG).show()
//                                dismissAllowingStateLoss()
//                                val db = FirebaseDatabase.getInstance().reference.child("accounts")
//                                db.child(account._id.toString()).child("salt").setValue(newSalt)
//                                db.child(account._id.toString()).child("password")
//                                        .setValue(Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((newPassET.text.toString() + newSalt).toByteArray())))
//                            } else { currPassErrorTV.visibility = View.VISIBLE }
//                        }
//                    }}
                    Toast.makeText(activity, "NAH COMMIT", Toast.LENGTH_SHORT).show()
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