package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        var welcomeTV = findViewById<TextView>(R.id.activity_user_tv_welcome)
        var manageItemsBtn = findViewById<Button>(R.id.activity_user_btn_manage_items)
        var findItemsBtn = findViewById<Button>(R.id.activity_user_btn_find_items)
        var incomingItemsBtn = findViewById<Button>(R.id.activity_user_btn_incoming_items)
        var outgoingItemsBtn = findViewById<Button>(R.id.activity_user_btn_outgoing_items)
        var relocateItemsBtn = findViewById<Button>(R.id.activity_user_btn_relocate_items)
        var stockAdjustmentsBtn = findViewById<Button>(R.id.activity_user_btn_stock_adjustments)
        var manageUsersBtn = findViewById<Button>(R.id.activity_user_btn_manage_users)
        var viewReportBtn = findViewById<Button>(R.id.activity_user_btn_view_report)
//        welcomeTV.text = getString(R.string.text_logged_in_as)
        manageItemsBtn.text = manageItemsBtn.text.toString().replace(" ", "\n")
        findItemsBtn.text = findItemsBtn.text.toString().replace(" ", "\n")
        incomingItemsBtn.text = incomingItemsBtn.text.toString().replace(" ", "\n")
        outgoingItemsBtn.text = outgoingItemsBtn.text.toString().replace(" ", "\n")
        relocateItemsBtn.text = relocateItemsBtn.text.toString().replace(" ", "\n")
        stockAdjustmentsBtn.text = stockAdjustmentsBtn.text.toString().replace(" ", "\n")
        manageUsersBtn.text = manageUsersBtn.text.toString().replace(" ", "\n")
        viewReportBtn.text = viewReportBtn.text.toString().replace(" ", "\n")
    }
}
