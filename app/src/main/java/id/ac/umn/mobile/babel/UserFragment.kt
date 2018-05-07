package id.ac.umn.mobile.babel

import android.app.Dialog
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import com.google.android.gms.common.util.Hex
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.text.SimpleDateFormat

class UserFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onStart() {
        super.onStart()

        val nameTV = activity.findViewById<TextView>(R.id.fragment_user_tv_name)
        val emailTV = activity.findViewById<TextView>(R.id.fragment_user_tv_email)
        val roleTV = activity.findViewById<TextView>(R.id.fragment_user_tv_role)
        val regDateTV = activity.findViewById<TextView>(R.id.fragment_user_tv_reg_date)
        val otherUsersLV = activity.findViewById<ListView>(R.id.fragment_user_lv_other_users)
        val changePassF = activity.findViewById<Button>(R.id.fragment_user_btn_change_password)

        val data = object : Data(){
            override fun onComplete() {
                if(isAdded){
                    val user = accounts.single { it.email==activity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("EMAIL", "") }
                    nameTV.text = user.name
                    emailTV.text = user.email
                    roleTV.text = user.role
                    regDateTV.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(user.reg_date).toString()
                    val acc = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1)
                    accounts.filter { it._id != user._id }.forEach {  acc.add(String.format("%s (%s)", it.name, it.role )) }
                    otherUsersLV.adapter = acc
                }
            }
        }

        changePassF.setOnClickListener{
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.activity_change_password)

            val lp = WindowManager.LayoutParams();
            lp.copyFrom(dialog.window.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            dialog.show()
            dialog.window.attributes = lp

            val currPassET = dialog.findViewById<EditText>(R.id.current_password_edit)
            val currPassErrorTV = dialog.findViewById<TextView>(R.id.current_password_edit_error)
            val newPassET = dialog.findViewById<EditText>(R.id.new_password_edit)
            val newPassErrorTV = dialog.findViewById<TextView>(R.id.new_password_edit_error)
            val confirmNewPassET = dialog.findViewById<EditText>(R.id.confirm_new_password_edit)
            val confirmNewPassErrorTV = dialog.findViewById<TextView>(R.id.confirm_new_password_edit_error)
            val changePassB = dialog.findViewById<Button>(R.id.change_password_button)
            val cancelChangePassB = dialog.findViewById<Button>(R.id.cancel_change_password_button)

            newPassET.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    newPassErrorTV.visibility = if(newPassET.length() <= 7) View.VISIBLE else View.GONE
                }
            })
            confirmNewPassET.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    confirmNewPassErrorTV.visibility = if(newPassET.text.toString() != confirmNewPassET.text.toString()) View.VISIBLE else View.GONE
                }
            })

            changePassB.setOnClickListener{
                val db = FirebaseDatabase.getInstance().reference.child("accounts")
                db.orderByChild("email").equalTo(emailTV.text.toString()).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(p0: DataSnapshot?) {
                        p0!!
                        if(p0.children.any()){
                            val salt = p0.children.first().child("salt").value.toString()
                            val password = Hex.bytesToStringLowercase(MessageDigest.getInstance("SHA-256").digest((currPassET.text.toString()+salt).toByteArray()))
                            if(p0.children.first().child("password").value.toString().toLowerCase() == password){
                                // ganti pass di firebasenya
                                // kasih notif berhasil ganti pass
                                currPassErrorTV.visibility = View.GONE
                            } else { currPassErrorTV.visibility = View.VISIBLE }
                        } else{ currPassErrorTV.visibility = View.VISIBLE }
                    }
                })
            }
            cancelChangePassB.setOnClickListener{ dialog.dismiss() }
        }
    }
}
