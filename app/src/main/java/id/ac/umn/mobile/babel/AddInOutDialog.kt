package id.ac.umn.mobile.babel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import org.w3c.dom.Text

class AddInOutDialog : DialogFragment() {
    class TransactionItems(val itemId: Int, var amount: Int, var unitId: Int)
    var inOutItems = ArrayList<TransactionItems>()
    var isPressed = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_add_in_out, container, false)
        val headingTV = view.findViewById<TextView>(R.id.dialog_add_in_out_tv_heading)
        val searchACTV = view.findViewById<AutoCompleteTextView>(R.id.dialog_in_out_items_actv_search)
        val itemsLV = view.findViewById<ListView>(R.id.dialog_in_out_lv_filtered_search)
        val searchTR = view.findViewById<TableRow>(R.id.dialog_add_in_out_tr1)
        val itemsTR = view.findViewById<TableRow>(R.id.dialog_add_in_out_tr2)
        val itemDetailsTR = view.findViewById<TableRow>(R.id.dialog_add_in_out_tr3)
        var stringOfID = ""

        headingTV.text = "ADD ITEM"
        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)
        val itemRaw = pref.getString("ITEMS", "").split(";")
        inOutItems.clear()
        if(!itemRaw.contains("")) itemRaw.forEach {
            val itemSpec = it.split(",").map { it.toInt() }
            inOutItems.add(TransactionItems(itemSpec[0], itemSpec[1], itemSpec[2]))
            stringOfID = stringOfID + itemSpec[0].toString() + " "
        }
        val data = object : Data(){
            override fun onComplete() {
                val filteredData = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item)
                items.filter {
                    it.itemName.toLowerCase().contains(searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) &&
                            !stringOfID.contains(it._id.toString())
                }.forEach { filteredData.add(it.itemName) }
                itemsLV.adapter = filteredData
            }
        }
        searchACTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filteredData = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item)
                data.items.filter {
                    it.itemName.toLowerCase().contains(searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) &&
                            !stringOfID.contains(it._id.toString())
                }.forEach { filteredData.add(it.itemName) }
                itemsLV.adapter = filteredData
            }
        })
        itemsLV.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = data.items.single { it.itemName == itemsLV.getItemAtPosition(position).toString() }
            inOutItems.add(TransactionItems(selectedItem._id, 0, selectedItem.unit_id))
            isPressed = true
            dismiss()
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit()
        pref.putString("ITEMS", inOutItems.joinToString(";") { String.format("%d,%d,%d", it.itemId, it.amount, it.unitId) })
        pref.apply()
        if(isPressed) Snackbar.make( activity.findViewById(android.R.id.content), "Item has been added", Snackbar.LENGTH_LONG).show()
    }
}
