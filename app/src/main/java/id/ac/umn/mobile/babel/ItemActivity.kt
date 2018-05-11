package id.ac.umn.mobile.babel

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

class ItemActivity : AppCompatActivity() {
    val filterItems = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val titleTV = findViewById<TextView>(R.id.activity_item_tv_title)
        val itemNameACTV = findViewById<AutoCompleteTextView>(R.id.activity_item_et_item_name)
        val unitMeasureS = findViewById<Spinner>(R.id.activity_item_spn_unit_measure)
        val unitNameS = findViewById<Spinner>(R.id.activity_item_spn_unit_name)
        val safetyStockET = findViewById<EditText>(R.id.activity_item_et_item_safety_stock)
        val cancelB = findViewById<Button>(R.id.activity_item_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_item_btn_ok)
        val act = intent.getStringExtra("OPERATION")

        titleTV.text = "ITEM TRANSACTION"

        val data = object : Data(){
            override fun onComplete() {
                val item = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
                items.filter { it.itemName.toLowerCase().contains(itemNameACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach {item.add( it.itemName )}
                itemNameACTV.setAdapter(item)
                itemNameACTV.threshold = 1

                //blom bisa buat dupe entry
                val unitMeasure = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
                units.forEach{unitMeasure.add(it.measure)}
                unitMeasureS.adapter = unitMeasure

                val unitName = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
                units.forEach{unitName.add(it.unit_name)}
                unitNameS.adapter = unitName
            }
        }

        cancelB.setOnClickListener{
            finish()
        }
        okB.setOnClickListener{
            unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val unitM = parent!!.getItemAtPosition(position)
                    val unitID = data.items.single { it.itemName == unitM }.toString().toInt()
                    data.items.forEach { InOutFragment().inOutItems.add(InOutFragment.TransactionItems(it._id, 0, unitID)) }
                    InOutFragment().saveTransaction()
                }
            }
//            Toast.makeText(this, "ini buat OK", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
