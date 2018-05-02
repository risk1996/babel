package id.ac.umn.mobile.babel

import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class LogoutDialog : DialogFragment() {

    private var ConfirmYes: TextView? = null
    private var ConfirmNo: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_logout, container, false)
        ConfirmYes = view.findViewById(R.id.logout_yes)
        ConfirmNo = view.findViewById(R.id.logout_no)

        ConfirmNo!!.setOnClickListener {
            Log.d(TAG, "onClick: Closing Dialog")
            dialog.dismiss()
        }

        ConfirmYes!!.setOnClickListener {
            Log.d(TAG, "onClick: Logging Out")
            activity.finish()
            dialog.dismiss()
        }

        return view
    }

    companion object {
        private val TAG = "Logout Dialog"
    }
}