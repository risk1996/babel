package id.ac.umn.mobile.babel

import android.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tabsTL = findViewById<TabLayout>(R.id.activity_main_tl_tabs)
//        val manageTI = findViewById<TabItem>(R.id.activity_main_ti_manage)
//        val inOutTI = findViewById<TabItem>(R.id.activity_main_ti_in_out)
//        val reportTI = findViewById<TabItem>(R.id.activity_main_ti_report)
//        val userTI = findViewById<TabItem>(R.id.activity_main_ti_user)
        val moreFAB = findViewById<MovableFloatingActionButton>(R.id.activity_main_fab_more)
        moreFAB.setOnClickListener {
            val bottomModal = MainModal()
            bottomModal.show(supportFragmentManager, bottomModal.tag)
        }
        tabsTL.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, ManageFragment()).commit()
                    1 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, InOutFragment()).commit()
                    2 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, ReportFragment()).commit()
                    3 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, UserFragment()).commit()
                }
            }
        })
    }
//  mengoverride ketika activity dibuka kembali
    override fun onResume() {
//      value tabsTL mengambil findViewById dari activity_main_tl_tabs
        val tabsTL = findViewById<TabLayout>(R.id.activity_main_tl_tabs)
        when(tabsTL!!.selectedTabPosition){
            0 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, ManageFragment()).commit()
            1 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, InOutFragment()).commit()
            2 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, ReportFragment()).commit()
            3 -> fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, UserFragment()).commit()
        }
        super.onResume()
    }
//  mengoverride ketika fungsi berhenti sesaat
    override fun onPause() {
        super.onPause()
//      menggunakan R.id.activity_main_fl_frame
        fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, Fragment()).commit()
    }
//  mengoverride function pada saat activity optionmenu dibuat
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//      dengan layout menu activity_main_menu_appbar
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
            R.id.menu_main_act_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
//        TODO("masukin snack bar")
    }
}
