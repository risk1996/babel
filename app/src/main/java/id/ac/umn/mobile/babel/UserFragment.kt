package id.ac.umn.mobile.babel

import android.app.Dialog
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
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
        val changePassB = activity.findViewById<Button>(R.id.fragment_user_btn_change_password)

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

        changePassB.setOnClickListener{
            changePass(context)
        }
    }

    private fun changePass(context : Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.activity_change_password)

        val lp = WindowManager.LayoutParams();
        lp.copyFrom(dialog.window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.show()
        dialog.window.attributes = lp
    }
}
