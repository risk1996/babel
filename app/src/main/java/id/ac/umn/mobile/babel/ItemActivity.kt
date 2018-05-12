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
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.security.MessageDigest
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
        val itemThumbnailIB = findViewById<ImageButton>(R.id.activity_item_ib_item_thumbnail)
        val unitMeasureS = findViewById<Spinner>(R.id.activity_item_spn_unit_measure)
        val unitNameS = findViewById<Spinner>(R.id.activity_item_spn_unit_name)
        val safetyStockET = findViewById<EditText>(R.id.activity_item_et_item_safety_stock)
        val cancelB = findViewById<Button>(R.id.activity_item_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_item_btn_ok)

        val act = intent.getStringExtra("OPERATION")
        val name = intent.getStringExtra("ITEM_NAME")
        val thumbnail = intent.getStringExtra("THUMBNAIL")
        val unit = intent.getIntExtra("UNIT", 0)
        val safetyStock = intent.getStringExtra("SAFETY_STOCK")

        val data = object : Data(){
            override fun onComplete() {
                val item = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
                items.filter { it.itemName.toLowerCase().contains(itemNameACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()) }.forEach {item.add( it.itemName )}
                itemNameACTV.setAdapter(item)
                itemNameACTV.threshold = 1

                val unitMeasure = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
                units.filter{ (it._id%100).toString() == "1" }.forEach { unitMeasure.add(it.measure)}
                if (act == "VIEW") unitMeasureS.isEnabled = false
                unitMeasureS.adapter = unitMeasure
            }
        }

        if (act == "VIEW"){
            titleTV.text = "VIEW ITEM"
            itemNameACTV.isEnabled = false
            safetyStockET.isEnabled = false
        }
        else titleTV.text = "EDIT ITEM"
        itemNameACTV.setText(name)
//        unitMeasureS.post({ unitMeasureS.setSelection((unit/100)-1) })
        safetyStockET.setText(safetyStock)

        unitMeasureS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val unitName = ArrayAdapter<String>(this@ItemActivity, android.R.layout.simple_list_item_1)
                data.units.filter{ it.measure == parent!!.getItemAtPosition(position) }.forEach{unitName.add(it.unit_name)}
                if (act == "VIEW") unitNameS.isEnabled = false
                unitNameS.adapter = unitName
//                unitNameS.post({ unitNameS.setSelection((unit%100)-1) })
            }
        }

        cancelB.setOnClickListener{
            finish()
            Toast.makeText(this, unit, Toast.LENGTH_SHORT).show()
        }
        okB.setOnClickListener{
            finish()
            if (act == "EDIT"){
                val item = data.items.single { it.itemName == name }
                val db = FirebaseDatabase.getInstance().reference.child("items")
                db.child(item._id.toString()).child("item_name").setValue(itemNameACTV.text.toString())
                db.child(item._id.toString()).child("safety_stock").setValue(safetyStockET.text.toString())
                db.child(item._id.toString()).child("unit_id").setValue(((unitMeasureS.selectedItemPosition)*100 + 101 + unitNameS.selectedItemPosition).toString())
                // udah keganti tp langsung crash gara2 index array out of bounds tp blom nemu kenapa bisa gitu
                // masalahnya mungkin di spinner atau array adapter, yg pasti related sama kalo gw mau set nilai spinnernya pas mau edit
//                TODO("UPDATE item_thumbnail, fix bug")
            }
        }
    }
}
