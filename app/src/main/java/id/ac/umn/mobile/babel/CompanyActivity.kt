package id.ac.umn.mobile.babel

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase

class CompanyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)
        val titleTV = findViewById<TextView>(R.id.activity_company_tv_title)
        val companyNameET = findViewById<EditText>(R.id.activity_company_et_company_name)
        val companyStatusET = findViewById<EditText>(R.id.activity_company_et_company_status)
        val companyStatusShortET = findViewById<EditText>(R.id.activity_company_et_company_status_short)
        val companySiteET = findViewById<EditText>(R.id.activity_company_et_company_site)
        val companyMainOfficeET = findViewById<EditText>(R.id.activity_company_et_company_office_main)
        val companySecondaryOfficeET = findViewById<EditText>(R.id.activity_company_et_company_office_secondary)
        val cancelB = findViewById<Button>(R.id.activity_company_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_company_btn_ok)

        titleTV.text = "EDIT COMPANY"
        okB.text = "Confirm Edit"

        val data = object : Data(){
            override fun onComplete() {
                companyNameET.setText(company.name)
                companyStatusET.setText(company.type)
                companyStatusShortET.setText(company.typeShort)
                companySiteET.setText(company.site)
                companyMainOfficeET.setText(company.officeMain)
                companySecondaryOfficeET.setText(company.officeSecondary)
                okB.setOnClickListener {
                    val db = FirebaseDatabase.getInstance().reference.child("_company")
                    val changedItem = mutableMapOf<String, Any>()
                    changedItem["company_name"] = companyNameET.text.toString()
                    changedItem["company_type"] = companyStatusET.text.toString()
                    changedItem["company_type_short"] = companyStatusShortET.text.toString()
                    changedItem["company_site"] = companySiteET.text.toString()
                    changedItem["company_office_main"] = companyMainOfficeET.text.toString()
                    changedItem["company_office_secondary"] = companySecondaryOfficeET.text.toString()
                    changedItem["company_logo"] = "comp_mmm"
                    db.setValue(changedItem)
                    finish()
                }
            }
        }
        cancelB.setOnClickListener { finish() }
    }
}
