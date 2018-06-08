package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        val titleTV = findViewById<TextView>(R.id.activity_location_tv_title)
        val locationCodeET = findViewById<EditText>(R.id.activity_location_et_location_code)
        val activeSW = findViewById<Switch>(R.id.activity_location_sw_active)
        val locationNameET = findViewById<EditText>(R.id.activity_location_et_location_name)
        val errorsTV = findViewById<TextView>(R.id.activity_location_tv_errors)
        val cancelB = findViewById<Button>(R.id.activity_location_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_location_btn_ok)
        val act = intent.getStringExtra("OPERATION")
        val locID = intent.getIntExtra("LOCATION_ID", 0)

        when (act) {
            "EDIT" -> {
                titleTV.text = "EDIT LOCATION"
                okB.text = "Confirm Edit"
            }
            "NEW" -> {
                titleTV.text = "NEW LOCATION"
                okB.text = "Confirm Addition"
            }
            else -> { finish() }
        }
2
        val data = object : Data() {
            override fun onComplete() {
                val location = locationsAll.singleOrNull { it._id == locID }
                if (act == "EDIT") {
                    location!!
                    locationNameET.setText(location.position)
                    locationCodeET.setText(location.code)
                    activeSW.isChecked = location.status == "active"
                }
                locationNameET.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(locationNameET.text.toString() != "") errorsTV.visibility = View.GONE
                        else {
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = "Location Name cannot be empty"
                        }
                    }
                })
                locationCodeET.addTextChangedListener(object : TextWatcher{
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(locationCodeET.text.toString() != "") errorsTV.visibility = View.GONE
                        else {
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = "Location Code cannot be empty"
                        }
                    }
                })
                okB.setOnClickListener {
                    if (act == "NEW"){
                        if (locationsAll.firstOrNull { it.code.toLowerCase() == locationCodeET.text.toString().toLowerCase() } != null){
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = "Location Code already exists"
                        }
                        else if (locationsAll.firstOrNull { it.position.toLowerCase() == locationNameET.text.toString().toLowerCase() } != null){
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = "Location Name already exists"
                        }
                    }
                    if (errorsTV.visibility == View.GONE) {
                        val db = FirebaseDatabase.getInstance().reference.child("locations")
                        val changedLoc = mutableMapOf<String, Any>()
                        changedLoc["code"] = locationCodeET.text.toString()
                        changedLoc["status"] = if(activeSW.isChecked) "active" else "inactive"
                        changedLoc["position"] = locationNameET.text.toString()
                        val itemId = if (act == "NEW") locationsAll.last()._id + 1 else location!!._id
                        db.child(itemId.toString()).setValue(changedLoc)

                        if (act == "NEW"){
                            val db2 = FirebaseDatabase.getInstance().reference.child("items")
                            itemsAll.forEach {
//                                Log.d("a : ", it._id.toString())
                                db2.child(it._id.toString()).child("stocks").child(itemId.toString()).setValue("0")
                            }
                        }

                        finish()
                    }
                }
            }
        }
        cancelB.setOnClickListener{ finish() }
    }
}
