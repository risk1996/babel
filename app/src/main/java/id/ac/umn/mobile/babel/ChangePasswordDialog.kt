package id.ac.umn.mobile.babel

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.util.Hex
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import java.security.MessageDigest
import java.util.*

class ChangePasswordDialog : DialogFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//    layout tampilan untuk fragment
        val view = inflater.inflate(R.layout.dialog_change_password, container, false)
        val currPassET = view.findViewById<EditText>(R.id.current_password_edit)
        val currPassErrorTV = view.findViewById<TextView>(R.id.current_password_edit_error)
        val newPassET = view.findViewById<EditText>(R.id.new_password_edit)
        val newPassErrorTV = view.findViewById<TextView>(R.id.new_password_edit_error)
        val confirmNewPassET = view.findViewById<EditText>(R.id.confirm_new_password_edit)
        val confirmNewPassErrorTV = view.findViewById<TextView>(R.id.confirm_new_password_edit_error)
        val changePassB = view.findViewById<Button>(R.id.change_password_button)
        val cancelChangePassB = view.findViewById<Button>(R.id.cancel_change_password_button)
        val requiredFieldErrorTV = view.findViewById<TextView>(R.id.required_field_error_text_view)
        currPassET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                requiredFieldErrorTV.visibility = View.GONE
                currPassErrorTV.visibility = View.GONE
            }
        })

        newPassET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { newPassErrorTV.visibility = if(newPassET.length() <= 7) View.VISIBLE else View.GONE }
        })
        confirmNewPassET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                confirmNewPassErrorTV.visibility = if(newPassET.text.toString() != confirmNewPassET.text.toString()) View.VISIBLE else View.GONE
            }
        })

        changePassB.setOnClickListener{
            when {
                currPassET.length() <= 0 -> requiredFieldErrorTV.visibility = View.VISIBLE
                newPassET.length() <= 0 -> newPassErrorTV.visibility = View.VISIBLE
                confirmNewPassET.length() <= 0 -> confirmNewPassErrorTV.visibility = View.VISIBLE
                else -> {
                    val pref = activity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                    val data = object : Data(){ override fun onComplete() {
                        if(isAdded) {
                            val account = accounts.single { it.email == pref.getString("EMAIL", "") }
                            val salt = account.salt
                            val newSalt = UUID.randomUUID().toString().substring(25, 30)
                            val oldPassword = Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((currPassET.text.toString() + salt).toByteArray()))
                            if (account.password == oldPassword/* && newPassET.text == confirmNewPassET.text*/) {
                                currPassErrorTV.visibility = View.GONE
                                Snackbar.make(activity.findViewById(android.R.id.content), "Password successfully changed", Snackbar.LENGTH_LONG).show()
                                dismissAllowingStateLoss()
                                val db = FirebaseDatabase.getInstance().reference.child("accounts")
                                db.child(account._id.toString()).child("salt").setValue(newSalt)
                                db.child(account._id.toString()).child("password")
                                        .setValue(Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((newPassET.text.toString() + newSalt).toByteArray())))
                            } else { currPassErrorTV.visibility = View.VISIBLE }
                        }
                    }}}
            }
        }
        cancelChangePassB.setOnClickListener{
            Snackbar.make( activity.findViewById(android.R.id.content), "Password did not change", Snackbar.LENGTH_LONG).show()
            dismissAllowingStateLoss()
        }
        return view
    }
}