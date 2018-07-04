package id.ac.umn.mobile.babel

import android.app.DatePickerDialog
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
import com.google.android.gms.common.util.Hex
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

//==================================================================================================
// User Activity
//==================================================================================================
//
//
//--------------------------------------------------------------------------------------------------


class UserActivity : AppCompatActivity() {
    var listener : SharedPreferences.OnSharedPreferenceChangeListener? = null
    var calendar = Calendar.getInstance()!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val titleTV = findViewById<TextView>(R.id.activity_unit_tv_title)
        val userNameET = findViewById<EditText>(R.id.activity_user_et_user_name)
        val inactiveSW = findViewById<Switch>(R.id.activity_user_sw_inactive)
        val dobTV = findViewById<TextView>(R.id.activity_user_tv_dob)
        val dobB = findViewById<TextView>(R.id.activity_user_btn_dob)
        val emailET = findViewById<EditText>(R.id.activity_user_et_email)
        val passwordET = findViewById<EditText>(R.id.activity_user_et_password)
        val confirmPasswordET = findViewById<EditText>(R.id.activity_user_et_confirm_password)
        val emailErrorTV = findViewById<TextView>(R.id.activity_user_tv_email_errors)
        val passwordErrorTV = findViewById<TextView>(R.id.activity_user_tv_password_error)
        val confirmPasswordErrorTV = findViewById<TextView>(R.id.activity_user_tv_confirm_password_error)
        val roleS = findViewById<Spinner>(R.id.activity_user_spn_role)
        val errorsTV = findViewById<TextView>(R.id.activity_user_tv_errors)
        val cancelB = findViewById<Button>(R.id.activity_user_btn_cancel)
        val okB = findViewById<Button>(R.id.activity_user_btn_ok)
        val act = intent.getStringExtra("OPERATION")
        val userID = intent.getIntExtra("USER_ID", 0)

        when (act) {
            "EDIT" -> {
                titleTV.text = "EDIT USER"
                okB.text = "Confirm Edit"
            }
            "NEW" -> {
                titleTV.text = "NEW USER"
                okB.text = "Confirm Addition"
            }
            else -> { finish() }
        }

        val data = object : Data(){
            override fun onComplete() {
                val user = accountsAll.singleOrNull { it._id == userID }
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dobTV!!.text = String.format("DOB: %s", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time))
                }
                dobB.setOnClickListener { DatePickerDialog(this@UserActivity,
                        dateSetListener,
                        // set DatePickerDialog to point to today's date when it loads up
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show()
                }
                val availRoles = arrayOf("User", "Administrator")
                roleS.adapter = ArrayAdapter<String>(this@UserActivity, android.R.layout.simple_spinner_dropdown_item, availRoles)
                var regDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("1970-01-01")
                if (act == "EDIT") {
                    user!!
                    userNameET.setText(user.name)
                    inactiveSW.isChecked = user.status == "active"
                    emailET.setText(user.email)
                    roleS.setSelection(availRoles.indexOf(user.role))
                    regDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(user.dob)
                    dobTV.text = String.format("DOB: %s", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(regDate))
                } else dobTV.text = String.format("DOB: %s", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(regDate))
                userNameET.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        errorsTV.visibility = View.GONE
                    }
                })
                emailET.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        emailErrorTV.visibility = if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.text).matches()) View.VISIBLE else View.GONE
                        errorsTV.visibility = View.GONE
                    }
                })
                passwordET.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        passwordErrorTV.visibility = if(passwordET.length() <= 7) View.VISIBLE else View.GONE
                        errorsTV.visibility = View.GONE
                    }
                })
                confirmPasswordET.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        confirmPasswordErrorTV.visibility = if(confirmPasswordET.text.toString() != passwordET.text.toString()) View.VISIBLE else View.GONE
                        errorsTV.visibility = View.GONE
                    }
                })

                okB.setOnClickListener{
                    when(""){
                        userNameET.text.toString(), emailET.text.toString(), passwordET.text.toString(), confirmPasswordET.text.toString() -> {
                            errorsTV.visibility = View.VISIBLE
                        }
                    }
                    if (errorsTV.visibility == View.GONE
                            && emailErrorTV.visibility == View.GONE
                            && passwordErrorTV.visibility == View.GONE
                            && confirmPasswordErrorTV.visibility == View.GONE
                            && errorsTV.visibility == View.GONE){
                        val db = FirebaseDatabase.getInstance().reference.child("accounts")
                        val salt = UUID.randomUUID().toString().substring(25, 30)
                        val changedUser = mutableMapOf<String, Any>()
                        changedUser["dob"] = String.format("%s", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(regDate))
                        changedUser["email"] = emailET.text.toString()
                        changedUser["last_login"] = if (act == "NEW") SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) else user!!.lastLogin
                        changedUser["name"] = userNameET.text.toString()
                        changedUser["password"] = Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((passwordET.text.toString() + salt).toByteArray()))
                        changedUser["reg_date"] = if (act == "NEW") SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) else user!!.regDate
                        changedUser["role"] = roleS.selectedItem.toString()
                        changedUser["salt"] = salt
                        changedUser["status"] = if (inactiveSW.isChecked) "active" else "inactive"
                        val userId = if (act == "NEW") accountsAll.count() else user!!._id
                        db.child(userId.toString()).setValue(changedUser)
                        finish()
                    }
                }
            }
        }
        cancelB.setOnClickListener{ finish() }
    }
}
