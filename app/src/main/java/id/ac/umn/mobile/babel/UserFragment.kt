package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
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
        val data = object : Data(){
            override fun onComplete() {
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
}
