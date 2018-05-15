package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.common.util.Hex
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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
//                unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                    override fun onNothingSelected(parent: AdapterView<*>?) {}
//                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        val unitName = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
//                        data.units.filter{ it.measure == parent!!.getItemAtPosition(position) }.forEach{unitName.add(it.unit_name)}
//                        unitNameS.adapter = unitName
//                    }
//                }
//                okB.setOnClickListener{
//                    finish()
//                    val db = FirebaseDatabase.getInstance().reference.child("items")
//                    val items = mutableMapOf<String, Any>()
//                    items["item_name"] = itemNameET.text.toString()
//                    items["item_thumbnail"] = "icons8_circled_b_48" // TODO("update thumbnail biar gak literal")
//                    items["safety_stock"] = safetyStockET.text.toString()
//                    items["stocks"] = mutableListOf(999.toString()).toList()
//                    items["unit_id"] = ((unitMeasureS.selectedItemPosition)*100 + 101 + unitNameS.selectedItemPosition).toString()
//                    db.child((data.items.size + 1).toString()).setValue(items)
//                }
            }
            else -> { finish() }
        }

        val data = object : Data(){
            override fun onComplete() {
                val availMeasure = units.distinctBy { it.measure }.map { it.measure }
                unitMeasureS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availMeasure)
                val item  = items.singleOrNull { it._id == itemID }
                val unit = units.singleOrNull { it._id == item?.unit_id }
                if (act == "EDIT" || act == "VIEW") {
                    item!!; unit!!
                    itemThumbnailIB.setImageResource(R.drawable::class.java.getField(item.thumbnail.replace("_48","_96")).getInt(null))
                    itemNameET.setText(item.itemName)
                    unitMeasureS.setSelection(availMeasure.indexOf(unit.measure))
                    safetyStockET.setText(item.safetyStock.toString())
                }
                unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val availUnits = units.filter { it.measure == availMeasure[unitMeasureS.selectedItemPosition] }
                        unitNameS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availUnits.map { it.unit_name })
                        if ((act == "VIEW" || act == "EDIT") && unit!!.measure == availMeasure[unitMeasureS.selectedItemPosition]) unitNameS.setSelection(availUnits.indexOf(unit))
                        else unitNameS.setSelection(0)
                    }
                }
                okB.setOnClickListener{
                    Toast.makeText(this@ItemActivity, "HAI HAI", Toast.LENGTH_SHORT).show()
                    when (act) {
                        "VIEW" -> {
                            item!!
                            val intent = Intent(this@ItemActivity, ItemActivity::class.java)
                            intent.putExtra("OPERATION", "EDIT")
                            intent.putExtra("ITEM_ID", item._id)
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
