package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.view.*
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
//class userFragment extends Fragment()
class UserFragment : Fragment() {
//  mengoverride fungsi onCreateView pada saat di buat activity baru dan memiliki inflater dengan tujuan xml yang digunakan
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
//      xml yang digunakan fragment_user
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
//  mengoverride fungsi pada saat activity dibuka kembali
    override fun onResume() {
        super.onResume()
//      nilai val;ue dan findViewById dari R.id.fragment_user
        val nameTV = activity.findViewById<TextView>(R.id.fragment_user_tv_name)
        val emailTV = activity.findViewById<TextView>(R.id.fragment_user_tv_email)
        val roleTV = activity.findViewById<TextView>(R.id.fragment_user_tv_role)
        val regDateTV = activity.findViewById<TextView>(R.id.fragment_user_tv_reg_date)
        val changePassF = activity.findViewById<Button>(R.id.fragment_user_btn_change_password)
        val otherUsersLV : ListView = activity.findViewById(R.id.fragment_user_lv_other_users)
        val data = object : Data(){
//          mengoverride fungsi pada saat data selesai dikirim
            override fun onComplete() {
                if(isAdded){
                    val user = accounts.single { it.email==activity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("EMAIL", "") }
                    nameTV.text = user.name
                    emailTV.text = user.email
                    roleTV.text = user.role
                    regDateTV.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(user.reg_date).toString()
                    val acc = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1)
                    accounts.filter { it._id != user._id }.forEach {  acc.add(String.format("%s (%s)", it.name, it.role )) }
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
