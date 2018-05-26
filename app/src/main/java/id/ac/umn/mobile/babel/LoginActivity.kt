package id.ac.umn.mobile.babel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.common.util.Hex
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

//==================================================================================================
// Login Activity
//==================================================================================================
//
// Part of Main Activity, user can login to the app with the account that already register with
// software, more over, admin have an access with different feature.
//
//--------------------------------------------------------------------------------------------------


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = getString(R.string.activity_login_label)
        val emailET = findViewById<EditText>(R.id.activity_login_et_email)
        val emailErrorTV = findViewById<TextView>(R.id.activity_login_tv_email_errors)
        val passwordET = findViewById<EditText>(R.id.activity_login_et_password)
        val passwordErrorTV = findViewById<TextView>(R.id.activity_login_tv_password_errors)
        val rememberMeChk = findViewById<CheckBox>(R.id.activity_login_chk_remember_me)
        val credentialErrorTV = findViewById<TextView>(R.id.activity_login_tv_credentials_errors)
        val signInBtn = findViewById<Button>(R.id.activity_login_btn_sign_in)
        val pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)

        if(pref.getBoolean("REMEMBER_ME", false)){
            emailET.setText(pref.getString("EMAIL",""))
            passwordET.setText(pref.getString("PASSWORD", ""))
            rememberMeChk.isChecked = pref.getBoolean("REMEMBER_ME", false)
        }
        emailET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emailErrorTV.visibility = if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.text).matches()) View.VISIBLE else View.GONE
            }
        })
        passwordET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passwordErrorTV.visibility = if(passwordET.text.length<8) View.VISIBLE else View.GONE
            }
        })
        signInBtn.setOnClickListener {
            val progressPB = findViewById<ProgressBar>(R.id.activity_login_pb_progress)
            progressPB.visibility = View.VISIBLE
            val prefEd = getSharedPreferences("LOGIN", Context.MODE_PRIVATE).edit()
            if(rememberMeChk.isChecked){
                prefEd.putString("PASSWORD", passwordET.text.toString())
                prefEd.putBoolean("REMEMBER_ME", rememberMeChk.isChecked)
            } else prefEd.clear()
            prefEd.putString("EMAIL", emailET.text.toString())
            prefEd.putString("LAST_LOGIN", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            prefEd.apply()
            val db = FirebaseDatabase.getInstance().reference.child("accounts")
            db.orderByChild("email").equalTo(emailET.text.toString()).addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(p0: DataSnapshot?) {
                    if(emailErrorTV.visibility == View.VISIBLE || passwordErrorTV.visibility == View.VISIBLE) credentialErrorTV.visibility = View.VISIBLE
                    else{
                        p0!!
                        if(p0.children.any()){
                            val salt = p0.children.first().child("salt").value.toString()
                            val password = Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((passwordET.text.toString()+salt).toByteArray()))
                            if(p0.children.first().child("password").value.toString().toLowerCase() == password){
                                val data = object : Data(){
                                    override fun onComplete() {
                                        val user = accountsActive.single { it.email==this@LoginActivity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("EMAIL", "") }
                                        val lastLoginString = this@LoginActivity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("LAST_LOGIN", "")
                                        db.child(user._id.toString()).child("last_login").setValue(lastLoginString)
                                        val globalPrefEd = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity).edit()
                                        globalPrefEd.putString("in_out_incoming_max", inOutIncomingMax!!.toString())
                                        globalPrefEd.putString("global_stock_precision", globalStockPrecision!!)
                                        globalPrefEd.apply()
                                    }
                                }
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                credentialErrorTV.visibility = View.GONE
                                finish()
                            } else { credentialErrorTV.visibility = View.VISIBLE }
                        } else{ credentialErrorTV.visibility = View.VISIBLE }
                    }
                    if(credentialErrorTV.visibility == View.VISIBLE) progressPB.visibility = View.GONE
                }
            })
        }
    }
}
