package id.ac.umn.mobile.babel

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    var privilege : String? = null
    var accountID : String? = null
    var locationID : String? = null
    var thirdPartyID : String? = null
    val manageFragment = ManageFragment()
    val inOutFragment = InOutFragment()
    val reportFragment = ReportFragment()
    val userFragment = UserFragment()
    var globalPref : SharedPreferences? = null
    var globalListener : SharedPreferences.OnSharedPreferenceChangeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tabsTL = findViewById<TabLayout>(R.id.activity_main_tl_tabs)
//        val manageTI = findViewById<TabItem>(R.id.activity_main_ti_manage)
//        val inOutTI = findViewById<TabItem>(R.id.activity_main_ti_in_out)
//        val reportTI = findViewById<TabItem>(R.id.activity_main_ti_report)
//        val userTI = findViewById<TabItem>(R.id.activity_main_ti_user)
        val data = object : Data(){
            override fun onComplete() {
                val user = accountsActive.single { it.email==this@MainActivity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).getString("EMAIL", "") }
                privilege = user.role
                accountID = user._id.toString()
            }
        }
        globalPref = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)!!
        globalListener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            val newGlobalPref = mutableMapOf<String, Any>()
            newGlobalPref["in_out_incoming_max"] = globalPref!!.getString("in_out_incoming_max", "999")
            newGlobalPref["global_stock_precision"] = globalPref!!.getString("global_stock_precision", "0.##")
            FirebaseDatabase.getInstance().reference.child("_global_pref").setValue(newGlobalPref)
        }
        globalPref!!.registerOnSharedPreferenceChangeListener(globalListener)
        val moreFAB = findViewById<MovableFloatingActionButton>(R.id.activity_main_fab_more)
        moreFAB.setOnClickListener {
            val bottomModal = MainModal()
            bottomModal.privilege = privilege!!
            bottomModal.accountID = accountID!!
            bottomModal.locationID = locationID!!
            bottomModal.thirdPartyID = thirdPartyID!!
            bottomModal.show(supportFragmentManager, bottomModal.tag)
        }
        tabsTL.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, manageFragment).commit()
                    1 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, inOutFragment).commit()
                    2 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, reportFragment).commit()
                    3 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, userFragment).commit()
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        val tabsTL = findViewById<TabLayout>(R.id.activity_main_tl_tabs)
        when(tabsTL!!.selectedTabPosition){
            0 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, manageFragment).commit()
            1 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, inOutFragment).commit()
            2 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, reportFragment).commit()
            3 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, userFragment).commit()
        }
    }
    override fun onPause() {
        super.onPause()
        fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, Fragment()).commit()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    class LogOutDialog : YesNoDialog(){
        override fun onYesClicked() { activity.finish(); startActivity(Intent(activity, LoginActivity::class.java)) }
        override fun onNoClicked() {}
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menu_main_act_sign_out -> {
                val dialog = LogOutDialog()
                dialog.isCancelable = false
                dialog.heading = "Log Out"
                dialog.message = "Are you sure you want to log out?"
                dialog.highlight = dialog.HIGHLIGHT_NO
                dialog.show(fragmentManager, "Dialog Yes No")
            }
            R.id.menu_main_act_about -> { startActivity(Intent(this, AboutActivity::class.java)) }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
//        TODO("masukin snack bar")
    }
}
