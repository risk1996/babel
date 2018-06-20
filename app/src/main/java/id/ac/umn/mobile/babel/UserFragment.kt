package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserFragment : Fragment() {
    var privilege : String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onResume() {
        super.onResume()
        val nameTV = activity.findViewById<TextView>(R.id.fragment_user_tv_name)
        val emailTV = activity.findViewById<TextView>(R.id.fragment_user_tv_email)
        val roleTV = activity.findViewById<TextView>(R.id.fragment_user_tv_role)
        val dobTV = activity.findViewById<TextView>(R.id.fragment_user_tv_dob_date)
        val regDateTV = activity.findViewById<TextView>(R.id.fragment_user_tv_reg_date)
        val lastLoginTV = activity.findViewById<TextView>(R.id.fragment_user_tv_last_login)
        val changePassF = activity.findViewById<Button>(R.id.fragment_user_btn_change_password)
        val otherUsersTR = activity.findViewById<TableRow>(R.id.fragment_user_tr)
        val otherUsersV = activity.findViewById<View>(R.id.fragment_user_view)
        val otherUsersLV = activity.findViewById<ListView>(R.id.fragment_user_lv_other_users)
        val data = object : Data(){
            override fun onComplete() {
                if(isAdded){
                    val user = accountsActive.single { it.email==activity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("EMAIL", "") }
                    nameTV.text = user.name
                    emailTV.text = user.email
                    roleTV.text = user.role
                    val dob = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(user.dob)
                    dobTV.text = String.format("%1\$s", SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault()).format(dob))
                    val regDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(user.regDate)
                    regDateTV.text = String.format("%1\$s", SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault()).format(regDate))
                    val lastLogin = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(user.lastLogin)
                    lastLoginTV.text = String.format("%1\$s", SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault()).format(lastLogin))
                    val acc = ArrayList<HashMap<String, String>>()
                    accountsActive.filter { it._id != user._id }.forEach {
                        val hm = HashMap<String, String>()
                        hm["nameRole"] = String.format("%s (%s)", it.name, it.role)
                        val lastLogin = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.lastLogin)
                        hm["lastLogin"] = String.format("Last login: %s", SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault()).format(lastLogin))
                        acc.add(hm)
                    }
                    otherUsersLV.adapter = SimpleAdapter(activity, acc, android.R.layout.simple_list_item_2, arrayOf("nameRole", "lastLogin"), intArrayOf(android.R.id.text1, android.R.id.text2))
                    if((activity as MainActivity).privilege == "User"){
                        otherUsersTR.visibility = View.GONE
                        otherUsersV.visibility = View.GONE
                        otherUsersLV.visibility = View.GONE
                    }
                }
            }
        }
        changePassF.setOnClickListener{
            val dialog = ChangePasswordDialog()
            dialog.show(fragmentManager, dialog.tag)
        }
        otherUsersLV.setOnItemClickListener { parent, view, position, id ->
            val user = data.accountsAll.single { String.format("%s (%s)", it.name, it.role)== otherUsersLV.getItemAtPosition(position).toString() }
            val intent = Intent(activity, UserActivity::class.java)
            intent.putExtra("OPERATION", "EDIT")
            intent.putExtra("USER_ID", user._id)
            startActivity(intent)
        }
    }
}
