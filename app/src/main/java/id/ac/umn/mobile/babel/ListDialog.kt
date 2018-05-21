package id.ac.umn.mobile.babel

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

//==================================================================================================
// List Dialog
//==================================================================================================
//
//
//
//
//--------------------------------------------------------------------------------------------------

class ListDialog : DialogFragment() {
    var content : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_list, container, false)
        val headingTV = view.findViewById<TextView>(R.id.dialog_list_tv_heading)
        val operationsTR = view.findViewById<TableRow>(R.id.dialog_list_tr_operations)
        val inactiveSW = view.findViewById<Switch>(R.id.dialog_list_sw_inactive)
        val newBtn = view.findViewById<Button>(R.id.dialog_list_btn_new)
        val searchACTV = view.findViewById<AutoCompleteTextView>(R.id.dialog_list_actv_search)
        val itemsLV = view.findViewById<ListView>(R.id.dialog_list_lv_filtered_search)
        when (content){
             "IN OUT ITEM" -> {
                 headingTV.text = "ADD ITEM"
                 operationsTR.visibility = View.GONE
                 val pref = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE)
                 val presentItems = pref.getString("ITEMS", "").split(";").map { it.split(",")[0].toInt() }
                 var availItems = ArrayList<Item>()
                 val data = object : Data(){
                     override fun onComplete() {
                         availItems = ArrayList(itemsActive.filter { presentItems.indexOf(it._id) < 0 })
                         itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availItems.map { it.itemName })
                     }
                 }
                 searchACTV.addTextChangedListener(object : TextWatcher {
                     override fun afterTextChanged(p0: Editable?) {}
                     override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                     override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                         val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                         itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availItems.filter { it.itemName.toLowerCase().contains(term) }.map { it.itemName })
                     }
                 })
                 itemsLV.setOnItemClickListener { parent, view, position, id ->
                     val item = data.itemsActive.single { it.itemName == itemsLV.getItemAtPosition(position).toString() }
                     val prefEd = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit()
                     prefEd.putString("ITEMS", pref.getString("ITEMS", "").plus(String.format(";%d,%d,%d", item._id, 0, item.unitId)))
                     prefEd.apply()
                     Snackbar.make( activity.findViewById(android.R.id.content), "Item has been added", Snackbar.LENGTH_LONG).show()
                     dismiss()
                 }
            }
            "UNITS" -> {
                headingTV.text = "MANAGE UNITS"
                newBtn.setOnClickListener {  }
                var availUnits = ArrayList<Unit>()
                val data = object : Data(){
                    override fun onComplete() {
                        availUnits = ArrayList(if(inactiveSW.isChecked) unitsAll else unitsActive)
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availUnits.filter { it.unitName.toLowerCase().contains(term) }.map { it.unitName })
                    }
                }
                inactiveSW.setOnCheckedChangeListener { compoundButton, b -> data.onComplete() }
                searchACTV.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availUnits.filter { it.unitName.toLowerCase().contains(term) }.map { it.unitName })
                    }
                })
                itemsLV.setOnItemClickListener { adapterView, view, i, l ->

                }
            }
            "LOCATIONS" -> {
                headingTV.text = "MANAGE LOCATIONS"
                newBtn.setOnClickListener {  }
                var availLocation = ArrayList<Location>()
                val data = object : Data(){
                    override fun onComplete() {
                        availLocation = ArrayList(if(inactiveSW.isChecked) locationsAll else locationsActive)
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availLocation.filter { it.position.toLowerCase().contains(term) }.map { it.position })
                    }
                }
                inactiveSW.setOnCheckedChangeListener { compoundButton, b -> data.onComplete() }
                searchACTV.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availLocation.filter { it.position.toLowerCase().contains(term) }.map { it.position })
                    }
                })
                itemsLV.setOnItemClickListener { adapterView, view, i, l ->

                }
            }

            "THIRD PARTIES" -> {
                headingTV.text = "MANAGE THIRD PARTIES"
                newBtn.setOnClickListener {  }
                var availThirdParty = ArrayList<ThirdParty>()
                val data = object : Data(){
                    override fun onComplete() {
                        availThirdParty = ArrayList(if(inactiveSW.isChecked) thirdPartiesAll else thirdPartiesActive)
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availThirdParty.filter { it.tpName.toLowerCase().contains(term) }.map { it.tpName })
                    }
                }
                inactiveSW.setOnCheckedChangeListener { compoundButton, b -> data.onComplete() }
                searchACTV.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availThirdParty.filter { it.tpName.toLowerCase().contains(term) }.map { it.tpName })
                    }
                })
                itemsLV.setOnItemClickListener { adapterView, view, i, l ->

                }
            }
            else -> dismissAllowingStateLoss()
        }
        return view
    }
}
