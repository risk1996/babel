package id.ac.umn.mobile.babel

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val welcomeTV = findViewById<TextView>(R.id.activity_user_tv_welcome)
        val manageItemsBtn = findViewById<Button>(R.id.activity_user_btn_manage_items)
        val findItemsBtn = findViewById<Button>(R.id.activity_user_btn_find_items)
        val incomingItemsBtn = findViewById<Button>(R.id.activity_user_btn_incoming_items)
        val outgoingItemsBtn = findViewById<Button>(R.id.activity_user_btn_outgoing_items)
        val relocateItemsBtn = findViewById<Button>(R.id.activity_user_btn_relocate_items)
        val adjustItemsBtn = findViewById<Button>(R.id.activity_user_btn_adjust_items)
        val manageUsersBtn = findViewById<Button>(R.id.activity_user_btn_manage_users)
        val viewReportBtn = findViewById<Button>(R.id.activity_user_btn_view_report)

//        welcomeTV.text = getString(R.string.text_logged_in_as)
        manageItemsBtn.text = manageItemsBtn.text.toString().replace(" ", "\n")
        manageItemsBtn.setOnClickListener{
            startActivity(Intent(this, ManageItemsActivity::class.java))
        }
        findItemsBtn.text = findItemsBtn.text.toString().replace(" ", "\n")
        findItemsBtn.setOnClickListener{
//            startActivity()
        }
        incomingItemsBtn.text = incomingItemsBtn.text.toString().replace(" ", "\n")
        incomingItemsBtn.setOnClickListener{
//            startActivity()
        }
        outgoingItemsBtn.text = outgoingItemsBtn.text.toString().replace(" ", "\n")
        outgoingItemsBtn.setOnClickListener{
//            startActivity()
        }
        relocateItemsBtn.text = relocateItemsBtn.text.toString().replace(" ", "\n")
        relocateItemsBtn.setOnClickListener{
//            startActivity()
        }
        adjustItemsBtn.text = adjustItemsBtn.text.toString().replace(" ", "\n")
        adjustItemsBtn.setOnClickListener{
//            startActivity()
        }
        manageUsersBtn.text = manageUsersBtn.text.toString().replace(" ", "\n")
        manageUsersBtn.setOnClickListener{
//            startActivity()
        }
        viewReportBtn.text = viewReportBtn.text.toString().replace(" ", "\n")
        viewReportBtn.setOnClickListener{
//            startActivity()
        }
    }
}
