package id.ac.umn.mobile.babel

import android.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabItem
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manageTI = findViewById<TabItem>(R.id.activity_main_ti_manage)
        val inOutTI = findViewById<TabItem>(R.id.activity_main_ti_in_out)
        val reportTI = findViewById<TabItem>(R.id.activity_main_ti_report)
        val userTI = findViewById<TabItem>(R.id.activity_main_ti_user)
    }

    override fun onStart() {
        fragmentManager.beginTransaction().replace(R.id.activity_main_fl_frame, ManageFragment()).commit()
        super.onStart()
    }
}
