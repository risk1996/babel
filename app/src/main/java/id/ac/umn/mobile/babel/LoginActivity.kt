package id.ac.umn.mobile.babel

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

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
//            var valid = true
//            TODO("IMPLEMENT ONLIEN DATABASE & FETCH")
//            if(valid)startActivity(Intent(this,UserActivity::class.java))
//            else credentialErrorTV.visibility = View.VISIBLE
//            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
