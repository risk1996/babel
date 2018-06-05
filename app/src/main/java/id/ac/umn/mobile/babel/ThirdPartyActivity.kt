package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class ThirdPartyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_party)
        val titleTV = findViewById<TextView>(R.id.activity_third_party_et_third_party_name)
        val activeSW = findViewById<Switch>(R.id.activity_third_party_sw_active)
        val roleS = findViewById<RadioGroup>(R.id.activity_third_party_rg)
        val errorsTV = findViewById<TextView>(R.id.activity_third_party_tv_errors)
        val cancelB = findViewById<Button>(R.id.activity_third_party_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_third_party_btn_ok)
        val act = intent.getStringExtra("OPERATION")
        val userID = intent.getIntExtra("THIRD_PARTY_ID", 0)

        when (act) {
            "EDIT" -> {
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
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        cancelB.setOnClickListener{ finish() }
    }
}
