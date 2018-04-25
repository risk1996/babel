package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabItem
import android.support.design.widget.TabLayout
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var showOverflow = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manageTI = findViewById<TabItem>(R.id.activity_main_ti_manage)
        val inOutTI = findViewById<TabItem>(R.id.activity_main_ti_in_out)
        val reportTI = findViewById<TabItem>(R.id.activity_main_ti_report)
        val userTI = findViewById<TabItem>(R.id.activity_main_ti_user)
    }
    override fun onStart() {
        val tabsTL = findViewById<TabLayout>(R.id.activity_main_tl_tabs)
        val moreFAB = findViewById<MovableFloatingActionButton>(R.id.activity_main_fab_more)
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
        fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, ManageFragment()).commit()
        moreFAB.setOnClickListener {
            Toast.makeText(this, "CIE MORE", Toast.LENGTH_SHORT).show()
        }
        super.onStart()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu_appbar, menu)
        val search = menu!!.findItem(R.id.menu_main_act_search) as MenuItem
        val searchView = search.actionView as SearchView
        search.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (item.itemId != R.id.menu_main_act_search){
                        item.isVisible = false
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                    }
                }
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (item.itemId != R.id.menu_main_act_search){
                        item.isVisible = true
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    }
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}
