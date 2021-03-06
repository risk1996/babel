package id.ac.umn.mobile.babel

import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DecimalFormat

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
                 val presentItems = if (pref.getString("ITEMS", "") != "") pref.getString("ITEMS", "").split(";").map { it.split(",")[0].toInt() } else null
                 var availItems = ArrayList<Item>()
                 val data = object : Data(){
                     override fun onComplete() {
                         availItems = ArrayList(itemsActive.filter { if (presentItems != null) presentItems.indexOf(it._id) < 0 else true })
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
                 itemsLV.setOnItemClickListener { parent, _, position, id ->
                     val item = data.itemsActive.single { it.itemName == itemsLV.getItemAtPosition(position).toString() }
                     val prefEd = activity.getSharedPreferences("ACTIVE_TRANSACTION", Context.MODE_PRIVATE).edit()
                     prefEd.putString("ITEMS", pref.getString("ITEMS", "").plus(String.format("%s%d,%d,%d", if(pref.getString("ITEMS", "") == "")"" else ";", item._id, 0, item.unitId)))
                     prefEd.apply()
                     Snackbar.make( activity.findViewById(android.R.id.content), "Item has been added", Snackbar.LENGTH_LONG).show()
                     dismiss()
                 }
            }

            "OOS" -> {
                headingTV.text = "OUT OF STOCK ITEMS"
                operationsTR.visibility = View.GONE
                val data = object : Data(){
                    override fun onComplete() {
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        var oosItems = ArrayList<HashMap<String, String>>()
                        val availItems = ArrayList(itemsActive.filter { item -> item.stocks.any { it < item.safetyStock } }.filter { it.itemName.toLowerCase().contains(term) })
                        availItems.forEach { item ->
                            val unit = unitsAll.single { it._id == item.unitId }
                            val hm = HashMap<String, String>()
                            hm["itemName"] = item.itemName
                            var oosLoc = ""
                            val dec = DecimalFormat("0.##")
                            item.stocks.forEachIndexed { index, d ->
                                if(d < item.safetyStock) oosLoc += String.format(", %s (%s/%s %s)", locationsAll[index].code, dec.format(d/unit.value), dec.format(item.safetyStock/unit.value), unit.unitName)
                            }
                            hm["oosLoc"] = oosLoc.drop(2)
                            oosItems.add(hm)
                        }
                        itemsLV.adapter = SimpleAdapter(activity, oosItems, android.R.layout.simple_list_item_2, arrayOf("itemName", "oosLoc"), intArrayOf(android.R.id.text1, android.R.id.text2))
                    }
                }
                searchACTV.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { data.onComplete() }
                })
            }

            "ITEM" -> {
                headingTV.text = "MANAGE ITEMS"
                newBtn.setOnClickListener {
                    val intent = Intent(activity, ItemActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
                var availItem = ArrayList<Item>()
                val data = object : Data(){
                    override fun onComplete() {
                        availItem = ArrayList(if(inactiveSW.isChecked) itemsAll else itemsActive)
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availItem.filter { it.itemName.toLowerCase().contains(term) }.map { it.itemName })
                    }
                }
                inactiveSW.setOnCheckedChangeListener { compoundButton, b -> data.onComplete() }
                searchACTV.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val term = searchACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                        itemsLV.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, availItem.filter { it.itemName.toLowerCase().contains(term) }.map { it.itemName })
                    }
                })
                itemsLV.setOnItemClickListener { parent, _, position, id ->
                    val item = data.itemsAll.single { it.itemName == itemsLV.getItemAtPosition(position).toString() }
                    val intent = Intent(activity, ItemActivity::class.java)
                    intent.putExtra("OPERATION", "EDIT")
                    intent.putExtra("ITEM_ID", item._id)
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
            }

            "UNITS" -> {
                headingTV.text = "MANAGE UNITS"
                newBtn.setOnClickListener {
                    val intent = Intent(activity, UnitActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
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
                itemsLV.setOnItemClickListener { adapterView, _, position, l ->
                    val unit = data.unitsAll.single { it.unitName == itemsLV.getItemAtPosition(position).toString() }
                    val intent = Intent(activity, UnitActivity::class.java)
                    intent.putExtra("OPERATION", "EDIT")
                    intent.putExtra("UNIT_ID", unit._id)
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
            }
            "LOCATIONS" -> {
                headingTV.text = "MANAGE LOCATIONS"
                newBtn.setOnClickListener {
                    val intent = Intent(activity, LocationActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
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
                itemsLV.setOnItemClickListener { adapterView, _, position, l ->
                    val unit = data.locationsAll.single { it.position == itemsLV.getItemAtPosition(position).toString() }
                    val intent = Intent(activity, LocationActivity::class.java)
                    intent.putExtra("OPERATION", "EDIT")
                    intent.putExtra("LOCATION_ID", unit._id)
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
            }

            "THIRD PARTIES" -> {
                headingTV.text = "MANAGE THIRD PARTIES"
                newBtn.setOnClickListener {
                    val intent = Intent(activity, ThirdPartyActivity::class.java)
                    intent.putExtra("OPERATION", "NEW")
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
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
                itemsLV.setOnItemClickListener { adapterView, _, position, l ->
                    val unit = data.thirdPartiesAll.single { it.tpName == itemsLV.getItemAtPosition(position).toString() }
                    val intent = Intent(activity, ThirdPartyActivity::class.java)
                    intent.putExtra("OPERATION", "EDIT")
                    intent.putExtra("THIRD_PARTY_ID", unit._id)
                    startActivity(intent)
                    dismissAllowingStateLoss()
                }
            }
            else -> dismissAllowingStateLoss()
        }
        return view
    }
}
