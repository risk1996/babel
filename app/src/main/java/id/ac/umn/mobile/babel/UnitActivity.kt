package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import android.R.attr.category
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter



//==================================================================================================
// Unit Activity
//==================================================================================================
//
//
//--------------------------------------------------------------------------------------------------


class UnitActivity : AppCompatActivity() {
    var listener : SharedPreferences.OnSharedPreferenceChangeListener? = null // TODO("IMPLEMENT LISTENER FROM THUMBNAIL DIALOG")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit)
        val titleTV = findViewById<TextView>(R.id.activity_unit_tv_title)
        val activeSW = findViewById<Switch>(R.id.activity_unit_sw_active)
        val unitNameET = findViewById<EditText>(R.id.activity_unit_et_unit_name)
        val unitThumbnailIB = findViewById<ImageButton>(R.id.activity_unit_ib_item_thumbnail)
        val unitMeasureACTV = findViewById<AutoCompleteTextView>(R.id.activity_unit_actv_unit_measurement)
        val unitIncrementET = findViewById<EditText>(R.id.activity_unit_et_unit_increment)
        val unitValueET = findViewById<EditText>(R.id.activity_unit_et_unit_value)
        val cancelB = findViewById<Button>(R.id.activity_unit_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_unit_btn_ok)
        val errorsTV = findViewById<TextView>(R.id.activity_unit_tv_errors)
        val act = intent.getStringExtra("OPERATION")
        val unitID = intent.getIntExtra("UNIT_ID", 0)

        when (act) {
            "EDIT" -> {
                titleTV.text = "EDIT UNIT"
                okB.text = "Confirm Edit"
            }
            "NEW" -> {
                titleTV.text = "NEW UNIT"
                okB.text = "Confirm Addition"
            }
            else -> { finish() }
        }

        val data = object : Data(){
            override fun onComplete() {
                val unit = unitsAll.singleOrNull { it._id == unitID }
                val availMeasure = unitsAll.distinctBy { it.measure }.map { it.measure }

//                val term = unitMeasureACTV.text.toString().toLowerCase().replace(" ", ".*?").toRegex()
                unitMeasureACTV.setAdapter(ArrayAdapter<String>(this@UnitActivity, android.R.layout.simple_list_item_1, availMeasure))

                listener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
                    val pref = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE)
                    unitThumbnailIB.setImageResource(R.drawable::class.java.getField(pref.getString("RESOURCE", "icons8_package_24").replace("_24","_96")).getInt(null))
                }
                getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener)
                val prefEd = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).edit()
                if (act == "EDIT") {
                    unit!!
                    unitNameET.setText(unit.unitName)
                    prefEd.putString("RESOURCE", unit.unitThumbnail)
                    activeSW.isChecked = unit.status == "active"
                    unitMeasureACTV.setText(unit.measure)
                    unitIncrementET.setText(unit.increment.toString())
                    unitValueET.setText(unit.value.toString())
                } else prefEd.putString("RESOURCE", "icons8_package_24")
                prefEd.apply()
                unitThumbnailIB.setOnClickListener {
                    val dialog = ThumbnailDialog()
                    dialog.show(fragmentManager, dialog.tag)
                }

                val textViews = ArrayList<Pair<TextView, String>>()
                textViews.add(Pair(unitNameET, "Unit name"))
                textViews.add(Pair(unitMeasureACTV, "Unit measure"))
                textViews.add(Pair(unitIncrementET, "Unit increment"))
                textViews.add(Pair(unitValueET, "Unit value"))
                textViews.forEach { it.first.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(it.first.text != "") errorsTV.visibility = View.GONE
                        else {
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = String.format("%s cannot be empty", it.second)
                        }
                    }
                }) }

                okB.setOnClickListener{
                    if (unitsAll.firstOrNull { it.unitName.toLowerCase() == unitNameET.text.toString().toLowerCase() } != null){
                        errorsTV.visibility = View.VISIBLE
                        errorsTV.text = String.format("Unit Name already exists in another Unit Measurement")
                    }
                    when(""){
                        unitNameET.text.toString(), unitMeasureACTV.text.toString(), unitIncrementET.text.toString(), unitValueET.text.toString() -> {
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = "All fields are required"
                        }
                    }
                    if (errorsTV.visibility == View.GONE) {
                        val db = FirebaseDatabase.getInstance().reference.child("units")
                        val changedUnit = mutableMapOf<String, Any>()
                        changedUnit["increment"] = unitIncrementET.text.toString()
                        changedUnit["measure"] = unitMeasureACTV.text.toString()
                        changedUnit["status"] = if(activeSW.isChecked) "active" else "inactive"
                        changedUnit["unit_name"] = unitNameET.text.toString()
                        changedUnit["unit_thumbnail"] = getSharedPreferences("THUMBNAIL", Context.MODE_PRIVATE).getString("RESOURCE", "icons8_package_24")
                        changedUnit["val"] = unitValueET.text.toString()
                        val unitId = if (unitsAll.distinctBy { it.measure }.singleOrNull { unitMeasureACTV.text.toString().toLowerCase() == it.measure.toLowerCase() } == null){
                            unitsAll.last()._id / 100 * 100 + 101
                        }
                        else {
                            if ( unitsAll.singleOrNull { unitNameET.text.toString().toLowerCase() == it.unitName.toLowerCase() } == null){
                                unitsAll.last { unitMeasureACTV.text.toString().toLowerCase() == it.measure.toLowerCase() }._id + 1
                            }
                            else unit!!._id
                        }
                        db.child(unitId.toString()).setValue(changedUnit)
                        finish()
                    }
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
