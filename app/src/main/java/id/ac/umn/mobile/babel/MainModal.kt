package id.ac.umn.mobile.babel

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class MainModal : BottomSheetDialogFragment() {
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
                incomingBtn.setOnClickListener { Toast.makeText(activity, "NAH INCOMING", Toast.LENGTH_SHORT).show() }
                outgoingBtn.setOnClickListener { Toast.makeText(activity, "NAH OUTGOING", Toast.LENGTH_SHORT).show() }
                addItemBtn.setOnClickListener { Toast.makeText(activity, "NAH ADD ITEM", Toast.LENGTH_SHORT).show() }
                commitBtn.setOnClickListener { Toast.makeText(activity, "NAH COMMIT", Toast.LENGTH_SHORT).show() }
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