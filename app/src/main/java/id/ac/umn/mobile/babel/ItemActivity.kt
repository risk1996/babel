package id.ac.umn.mobile.babel

import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat

class ItemActivity : AppCompatActivity() {
    //    HEAD
// override function item activity
///origin/master
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val titleTV = findViewById<TextView>(R.id.activity_item_tv_title)
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
                val availMeasure = units.distinctBy { it.measure }.map { it.measure }
                unitMeasureS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availMeasure)
                var availUnits = units.filter { it.measure == availMeasure[unitMeasureS.selectedItemPosition] }
                val item  = items.singleOrNull { it._id == itemID }
                val unit = units.singleOrNull { it._id == item?.unit_id }
                val rawStock = ArrayList<Double>()
                val stockETs = ArrayList<TextView>()
                if (act == "EDIT" || act == "VIEW") {
                    item!!; unit!!
                    itemThumbnailIB.setImageResource(R.drawable::class.java.getField(item.thumbnail.replace("_48","_96")).getInt(null))
                    itemNameET.setText(item.itemName)
                    unitMeasureS.setSelection(availMeasure.indexOf(unit.measure))
                    safetyStockET.setText((item.safetyStock / unit.value).toString())
                    rawStock.add(item.safetyStock)
                    stockETs.add(safetyStockET)
                }
                locations.forEach {
                    val rowTR = TableRow(this@ItemActivity)
                    val locationTV = TextView(this@ItemActivity)
                    val stockOnLocationET = EditText(this@ItemActivity)
                    locationTV.text = it.code
                    rowTR.addView(locationTV, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    if (act == "EDIT" || act == "VIEW") stockOnLocationET.setText(item!!.stocks[locations.indexOf(it)].toString())
                    else stockOnLocationET.setText(0f.toString())
                    rawStock.add(stockOnLocationET.text.toString().toDouble())
                    stockETs.add(stockOnLocationET)
                    if (act == "VIEW") stockOnLocationET.isEnabled = false
                    stockOnLocationET.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    rowTR.addView(stockOnLocationET, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    stockPerLocationTL.addView(rowTR)
                }
                stockETs.forEach {
                    it.addTextChangedListener(object : TextWatcher{
                        override fun afterTextChanged(p0: Editable?) {}
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            rawStock[stockETs.indexOf(it)] = (if(it.text.toString() != "") it.text.toString().toDouble() else .0) * availUnits[unitNameS.selectedItemPosition].value
                        }
                    })
                }
                unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        availUnits = units.filter { it.measure == availMeasure[unitMeasureS.selectedItemPosition] }
                        unitNameS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availUnits.map { it.unit_name })
                        if ((act == "VIEW" || act == "EDIT") && unit!!.measure == availMeasure[unitMeasureS.selectedItemPosition]) unitNameS.setSelection(availUnits.indexOf(unit))
                        else unitNameS.setSelection(0)
                    }
                }
                unitNameS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        stockETs.forEach {it.text = DecimalFormat("0.###############").format(rawStock[stockETs.indexOf(it)] / availUnits[unitNameS.selectedItemPosition].value) }
                    }
                }
                okB.setOnClickListener{
                    Toast.makeText(this@ItemActivity, "HAI HAI", Toast.LENGTH_SHORT).show()
                    when (act) {
                        "VIEW" -> {
                            val intent = Intent(this@ItemActivity, ItemActivity::class.java)
                            intent.putExtra("OPERATION", "EDIT")
                            intent.putExtra("ITEM_ID", item!!._id)
                            startActivity(intent)
                        }
                        "EDIT" -> {
                            item!!
                            val db = FirebaseDatabase.getInstance().reference.child("items")
                            db.child(item._id.toString()).child("item_name").setValue(itemNameET.text.toString())
                            db.child(item._id.toString()).child("safety_stock").setValue(safetyStockET.text.toString())
                            db.child(item._id.toString()).child("unit_id").setValue(((unitMeasureS.selectedItemPosition)*100 + 101 + unitNameS.selectedItemPosition).toString())
//                        TODO("UPDATE item_thumbnail")
                        }
                        "NEW" -> {
                            val db = FirebaseDatabase.getInstance().reference.child("items")
                            val newItem = mutableMapOf<String, Any>()
                            newItem["item_name"] = itemNameET.text.toString()
                            newItem["item_thumbnail"] = "icons8_circled_b_48" // TODO("update thumbnail biar gak literal")
                            newItem["safety_stock"] = safetyStockET.text.toString()
                            newItem["stocks"] = mutableListOf(999.toString()).toList()
                            newItem["unit_id"] = ((unitMeasureS.selectedItemPosition)*100 + 101 + unitNameS.selectedItemPosition).toString()
                            db.child((items.size + 1).toString()).setValue(newItem)
                        }
                    }
                    finish()
                }
            }
        }
        cancelB.setOnClickListener{ finish() }
    }
}
