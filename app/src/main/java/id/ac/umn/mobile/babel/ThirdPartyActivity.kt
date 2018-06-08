package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.firebase.database.FirebaseDatabase

class ThirdPartyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_party)
        val titleTV = findViewById<TextView>(R.id.activity_third_party_tv_title)
        val thirdPartyNameET = findViewById<EditText>(R.id.activity_third_party_et_third_party_name)
        val activeSW = findViewById<Switch>(R.id.activity_third_party_sw_active)
        val roleRG = findViewById<RadioGroup>(R.id.activity_third_party_rg)
        val roleCRB = findViewById<RadioButton>(R.id.activity_third_party_rb_customer)
        val roleSRB = findViewById<RadioButton>(R.id.activity_third_party_rb_supplier)
        val errorsTV = findViewById<TextView>(R.id.activity_third_party_tv_errors)
        val cancelB = findViewById<Button>(R.id.activity_third_party_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_third_party_btn_ok)
        val act = intent.getStringExtra("OPERATION")
        val tpID = intent.getIntExtra("THIRD_PARTY_ID", 0)

        when (act) {
            "EDIT" -> {
                roleCRB.isEnabled = false
                roleSRB.isEnabled = false
                titleTV.text = "EDIT THIRD PARTY"
                okB.text = "Confirm Edit"
            }
            "NEW" -> {
                titleTV.text = "NEW THIRD PARTY"
                okB.text = "Confirm Addition"
            }
            else -> { finish() }
        }

        val data = object : Data(){
            override fun onComplete() {
                val thirdParty = thirdPartiesAll.singleOrNull { it._id == tpID }
                if (act == "EDIT") {
                    thirdParty!!
                    thirdPartyNameET.setText(thirdParty.tpName)
                    activeSW.isChecked = thirdParty.status == "active"
                    if(thirdParty.role=="S") roleRG.check(R.id.activity_third_party_rb_supplier) else roleRG.check(R.id.activity_third_party_rb_customer)
                }
                thirdPartyNameET.addTextChangedListener(object : TextWatcher{
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(thirdPartyNameET.text.toString() != "") errorsTV.visibility = View.GONE
                        else {
                            errorsTV.visibility = View.VISIBLE
                            errorsTV.text = "Third Party Name cannot be empty"
                        }
                    }
                })
                okB.setOnClickListener {
                    if (thirdPartiesAll.firstOrNull { it.tpName.toLowerCase() == thirdPartyNameET.text.toString().toLowerCase() } != null && act == "NEW"){
                        errorsTV.visibility = View.VISIBLE
                        errorsTV.text = "Third Party Name already exists"
                    }
                    if (errorsTV.visibility == View.GONE) {
                        val db = FirebaseDatabase.getInstance().reference.child("third_parties")
                        val changedTp = mutableMapOf<String, Any>()
                        changedTp["role"] = findViewById<RadioButton>(roleRG.checkedRadioButtonId).text.toString().substring(0,1)
                        changedTp["status"] = if(activeSW.isChecked) "active" else "inactive"
                        changedTp["tp_name"] = thirdPartyNameET.text.toString()
                        val itemId = if (act == "NEW") thirdPartiesAll.last()._id + 1 else thirdParty!!._id
                        db.child(itemId.toString()).setValue(changedTp)
                        finish()
                    }
                }
            }
        }
        cancelB.setOnClickListener{ finish() }
    }
}
