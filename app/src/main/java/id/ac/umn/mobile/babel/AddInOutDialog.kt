package id.ac.umn.mobile.babel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
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
//    function onCreateView > menaruh objek inflater: LayoutInflater, container: ViewGroup, SavedInstanceState
//    inflater: buat java object view dari layout tujuan
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_add_in_out, container, false)
//      layout inflater ditunjukan dialog_add_in_out
        val headingTV = view.findViewById<TextView>(R.id.dialog_add_in_out_tv_heading)
//      object id dialog_add_in_out_tv_heading
        val searchACTV = view.findViewById<AutoCompleteTextView>(R.id.dialog_in_out_items_actv_search)
//      object id dialog_in_out_items_actv_search
        val itemsLV = view.findViewById<ListView>(R.id.dialog_in_out_lv_filtered_search)
//      object id dialog_in_out_lv_filtered_search

        headingTV.text = "ADD ITEM"
//    value data
        val data = object : Data(){
//            fungsi onComplete dipanggil pada saat data selesai
//            override ketika fungsi aliran data selesai
            override fun onComplete() {
                val filteredData = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item)
                items.filter { it.itemName.toLowerCase().contains(searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach { filteredData.add(it.itemName) }
                itemsLV.adapter = filteredData
            }
        }
//        try {
//          searchACTV var buat ngambil nilai dari viewfindbyid nilai dr xml
//          xml = simple_spinner_dropdown_item
            searchACTV.addTextChangedListener(object : TextWatcher {
//              override pada saat function afterTextChanged/beforeTextChanged/onTextChanged
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//          filter data dari array adapter dengan isi berupa string dari activity xml simple_spinner_dropdown_item
                    val filteredData = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item)
                    data.items.filter { it.itemName.toLowerCase().contains(searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach { filteredData.add(it.itemName) }
                    itemsLV.adapter = filteredData
//                  filter
//                  toLowerCase mengubah itemName menjadi lowercase
//                  contains
//                  replace
//                  toRegex mengubah string menjadi regular expression
//                  Foreach looping data add pada itemName
                }
            })
//        }
//        catch (e: KotlinNullPointerException){ e.printStackTrace() }
//        catch (e: NullPointerException){ e.printStackTrace() }
        return view
    }
}
