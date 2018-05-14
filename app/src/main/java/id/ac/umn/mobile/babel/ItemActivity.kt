package id.ac.umn.mobile.babel

import android.content.Context
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
    var unitNameBool = true
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

        val data = object : Data(){
            override fun onComplete() {
//                val item = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
//                items.filter { it.itemName.toLowerCase().contains(itemNameACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach {item.add( it.itemName )}
//                itemNameACTV.setAdapter(item)
//                itemNameACTV.threshold = 1

                val availMeasure = units.map { it.measure }.distinct()
                unitMeasureS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availMeasure)

                val item  = items.single { it._id == itemID }
                val unit = units.single { it._id == item.unit_id }

                if (act == "EDIT" || act == "VIEW") {
                    if (act == "VIEW") {
                        titleTV.text = "VIEW ITEM"
                        unitMeasureS.isEnabled = false
                        unitNameS.isEnabled = false
                        itemNameET.isEnabled = false
                        safetyStockET.isEnabled = false
                    }
                    titleTV.text = "EDIT ITEM"
                    itemNameET.setText(item.itemName)
//                    unitMeasureS.post({ unitMeasureS.setSelection((item.unit_id / 100) - 1) })
                    unitMeasureS.setSelection(availMeasure.indexOf(unit.measure))
                    safetyStockET.setText(item.safetyStock.toString())
                }
                else if (act == "NEW"){
                    titleTV.text = "NEW ITEM"
                }

                unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val availUnits = units.filter { it.measure == availMeasure[unitMeasureS.selectedItemPosition] }
                        unitNameS.adapter = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1, availUnits.map { it.unit_name })
//                        if (unitNameBool && (act == "VIEW" || act == "EDIT")){
//                            unitNameS.post({ unitNameS.setSelection((item.unit_id%100)-1, false) })
//                            unitNameBool = false
//                        }
                        if(unit.measure == availMeasure[unitMeasureS.selectedItemPosition]){
                            unitNameS.setSelection(availUnits.indexOf(unit))
                        }else{
                            unitNameS.setSelection(0)
                        }

                    }
                }

                cancelB.setOnClickListener{
                    finish()
                }
                okB.setOnClickListener{
                    finish()
                    if (act == "EDIT"){
                        val db = FirebaseDatabase.getInstance().reference.child("items")
                        db.child(item._id.toString()).child("item_name").setValue(itemNameET.text.toString())
                        db.child(item._id.toString()).child("safety_stock").setValue(safetyStockET.text.toString())
                        db.child(item._id.toString()).child("unit_id").setValue(((unitMeasureS.selectedItemPosition)*100 + 101 + unitNameS.selectedItemPosition).toString())
//                TODO("UPDATE item_thumbnail")
                    }
                    else if (act == "NEW"){
                        val db = FirebaseDatabase.getInstance().reference.child("items")
                        val items = mutableMapOf<String, Any>()
                        items["item_name"] = itemNameET.text.toString()
                        items["item_thumbnail"] = "icons8_circled_b_48" // TODO("update thumbnail biar gak literal")
                        items["safety_stock"] = safetyStockET.text.toString()
                        items["stocks"] = mutableListOf(999.toString()).toList()
                        items["unit_id"] = ((unitMeasureS.selectedItemPosition)*100 + 101 + unitNameS.selectedItemPosition).toString()
                        db.child((items.size).toString()).setValue(item)
                    }
                }
            }
        }
    }
}
