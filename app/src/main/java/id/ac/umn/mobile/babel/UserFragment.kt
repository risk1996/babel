package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.view.*
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UserFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onResume() {
        super.onResume()
        val nameTV = activity.findViewById<TextView>(R.id.fragment_user_tv_name)
        val emailTV = activity.findViewById<TextView>(R.id.fragment_user_tv_email)
        val roleTV = activity.findViewById<TextView>(R.id.fragment_user_tv_role)
        val regDateTV = activity.findViewById<TextView>(R.id.fragment_user_tv_reg_date)
        val lastLoginTV = activity.findViewById<TextView>(R.id.fragment_user_tv_last_login)
        val changePassF = activity.findViewById<Button>(R.id.fragment_user_btn_change_password)
        val otherUsersLV : ListView = activity.findViewById(R.id.fragment_user_lv_other_users)
        val data = object : Data(){
            override fun onComplete() {
                if(isAdded){
                    val user = accountsActive.single { it.email==activity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("EMAIL", "") }
                    nameTV.text = user.name
                    emailTV.text = user.email
                    roleTV.text = user.role

                    val regDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(user.regDate)
                    regDateTV.text = String.format("Registered since: %1\$s", SimpleDateFormat("E, dd MM yyyy", Locale.getDefault()).format(regDate))

                    val lastLogin = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(user.lastLogin)
                    lastLoginTV.text = String.format("Last login: %1\$s", SimpleDateFormat("E, dd MM yyyy", Locale.getDefault()).format(lastLogin))

                    val acc = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1)
                    accountsActive.filter { it._id != user._id }.forEach {  acc.add(String.format("%s (%s)", it.name, it.role )) }
                    otherUsersLV.adapter = acc
                }
            }
        }
        changePassF.setOnClickListener{
            val dialog = ChangePasswordDialog()
            dialog.show(fragmentManager, dialog.tag)
        }
    }
}
