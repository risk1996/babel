package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var emailET = findViewById<EditText>(R.id.activity_login_et_email)
        var emailErrorTV = findViewById<TextView>(R.id.activity_login_tv_email_errors)
        var passwordET = findViewById<EditText>(R.id.activity_login_et_password)
        var passwordErrorTV = findViewById<TextView>(R.id.activity_login_tv_password_errors)

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
    }
}
