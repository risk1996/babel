package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.text.DecimalFormat

//==================================================================================================
// Item Activity
//==================================================================================================
// Called by Manage Fragment (leftmost FAB button), here, user and administrator can view, but
// only administrator can edit the data about an item.
//--------------------------------------------------------------------------------------------------


class ItemActivity : AppCompatActivity() {
    var listener : SharedPreferences.OnSharedPreferenceChangeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        val titleTV = findViewById<TextView>(R.id.activity_item_tv_title)
        val inactiveSW = findViewById<Switch>(R.id.activity_item_sw_inactive)
        val itemNameET = findViewById<EditText>(R.id.activity_item_et_item_name)
        val itemThumbnailIB = findViewById<ImageButton>(R.id.activity_item_ib_item_thumbnail)
        val unitMeasureS = findViewById<Spinner>(R.id.activity_item_spn_unit_measure)
        val unitNameS = findViewById<Spinner>(R.id.activity_item_spn_unit_name)
        val safetyStockET = findViewById<EditText>(R.id.activity_item_et_item_safety_stock)
        val stockPerLocationTL = findViewById<TableLayout>(R.id.activity_item_tl_stock_per_location)
        val cancelB = findViewById<Button>(R.id.activity_item_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_item_btn_ok)
        val act = intent.getStringExtra("OPERATION")
        val itemID = intent.getIntExtra("ITEM_ID", 0)

        when (act) {
            "VIEW" -> {
                titleTV.text = "VIEW ITEM"
                itemNameET.isEnabled = false
                itemThumbnailIB.isEnabled = false
                itemThumbnailIB.drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
                unitMeasureS.isEnabled = false
                unitNameS.isEnabled = false
                safetyStockET.isEnabled = false
                okB.text = "Edit"
            }
            "EDIT" -> {
                titleTV.text = "EDIT ITEM"
                okB.text = "Confirm Edit"
            }
            "NEW" -> {
                titleTV.text = "NEW ITEM"
                okB.text = "Confirm Addition"
            }
            else -> { finish() }
        }

        val data = object : Data(){
            override fun onComplete() {
                val availMeasure = unitsActive.distinctBy { it.measure }.map { it.measure }
                unitMeasureS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availMeasure)
                var availUnits = unitsActive.filter { it.measure == availMeasure[unitMeasureS.selectedItemPosition] }
                val item  = itemsAll.singleOrNull { it._id == itemID }
                val unit = unitsActive.singleOrNull { it._id == item?.unitId }
                val rawStock = ArrayList<Double>()
                val stockETs = ArrayList<TextView>()
                val textWatchers = ArrayList<TextWatcher>()
                listener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
                    val pref = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE)
                    itemThumbnailIB.setImageResource(R.drawable::class.java.getField(pref.getString("RESOURCE", "icons8_box_48").replace("_48","_96")).getInt(null))
                }
                getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener)
                val prefEd = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).edit()
                if (act == "EDIT" || act == "VIEW") {
                    item!!; unit!!
                    itemNameET.setText(item.itemName)
                    prefEd.putString("RESOURCE", item.thumbnail)
                    inactiveSW.isChecked = item.status == "active"
                    unitMeasureS.setSelection(availMeasure.indexOf(unit.measure))
                    safetyStockET.setText((item.safetyStock * unit.value).toString())
                    rawStock.add(item.safetyStock)
                    stockETs.add(safetyStockET)
                } else prefEd.putString("RESOURCE", "icons8_box_48")
                prefEd.apply()
                itemThumbnailIB.setOnClickListener {
                    val dialog = ThumbnailDialog()
                    dialog.show(fragmentManager, dialog.tag)
                }
                locationsActive.forEach {
                    val rowTR = TableRow(this@ItemActivity)
                    val locationTV = TextView(this@ItemActivity)
                    val stockOnLocationET = EditText(this@ItemActivity)
                    locationTV.text = it.code
                    rowTR.addView(locationTV, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    if (act == "EDIT" || act == "VIEW") stockOnLocationET.setText(item!!.stocks[locationsActive.indexOf(it)].toString())
                    else stockOnLocationET.setText(0f.toString())
                    rawStock.add(stockOnLocationET.text.toString().toDouble())
                    stockETs.add(stockOnLocationET)
                    if (act == "VIEW") stockOnLocationET.isEnabled = false
                    stockOnLocationET.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    rowTR.addView(stockOnLocationET, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    stockPerLocationTL.addView(rowTR)
                }
                stockETs.forEach { textWatchers.add(
                    object : TextWatcher{
                        override fun afterTextChanged(p0: Editable?) {}
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            rawStock[stockETs.indexOf(it)] = (if(it.text.toString() != "") it.text.toString().toDouble() else .0) * availUnits[unitNameS.selectedItemPosition].value
                        }
                    }
                ) }
                unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        availUnits = unitsActive.filter { it.measure == availMeasure[unitMeasureS.selectedItemPosition] }
                        unitNameS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availUnits.map { it.unitName })
                        if ((act == "VIEW" || act == "EDIT") && unit!!.measure == availMeasure[unitMeasureS.selectedItemPosition]) unitNameS.setSelection(availUnits.indexOf(unit))
                        else unitNameS.setSelection(0)
                    }
                }
                unitNameS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        stockETs.forEach {
                            it.removeTextChangedListener(textWatchers[stockETs.indexOf(it)])
                            val df = DecimalFormat(PreferenceManager.getDefaultSharedPreferences(this@ItemActivity)!!.getString("global_item_precision", "0.##"))
                            it.text = df.format(rawStock[stockETs.indexOf(it)] / availUnits[unitNameS.selectedItemPosition].value)
                            it.addTextChangedListener(textWatchers[stockETs.indexOf(it)])
                        }
                    }
                }

                okB.setOnClickListener{
                    when (act) {
                        "EDIT", "NEW" -> {
                            val db = FirebaseDatabase.getInstance().reference.child("items")
                            val changedItem = mutableMapOf<String, Any>()
                            changedItem["item_name"] = itemNameET.text.toString()
                            changedItem["status"] = if(inactiveSW.isChecked) "active" else "inactive"
                            changedItem["item_thumbnail"] = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).getString("RESOURCE", "icons8_box_48")
                            changedItem["safety_stock"] = (safetyStockET.text.toString().toInt() * unit!!.value).toString()
                            changedItem["stocks"] = rawStock.drop(1).map { it.toString() }
                            changedItem["unit_id"] = unit._id.toString()
                            val itemId = if (act == "NEW") itemsAll.last()._id + 1 else item!!._id
                            db.child(itemId.toString()).setValue(changedItem)
                        }
                    }
                    finish()
                }
            }
        }
        cancelB.setOnClickListener{ finish() }
    }
    override fun onDestroy() {
        super.onDestroy()
        val prefEd = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).edit()
        prefEd.clear()
        prefEd.apply()
    }
}
