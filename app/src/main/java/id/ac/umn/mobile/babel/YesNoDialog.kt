package id.ac.umn.mobile.babel

import android.app.DialogFragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text

abstract class YesNoDialog : DialogFragment() {
    val HIGHLIGHT_NONE  = 0
    val HIGHLIGHT_NO    = 1
    val HIGHLIGHT_YES   = 2
    val HIGHLIGHT_BOTH  = 3

    var heading : String = ""
    var message : String = ""
    var highlight : Int = HIGHLIGHT_YES
    var value : String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_yes_no, container, false)
        val yesBtn : Button = view.findViewById(R.id.dialog_yes_no_btn_yes)!!
        val noBtn : Button = view.findViewById(R.id.dialog_yes_no_btn_no)!!
        val headingTV : TextView = view.findViewById(R.id.dialog_yes_no_tv_heading)!!
        val messageTV : TextView = view.findViewById(R.id.dialog_yes_no_tv_message)!!
        yesBtn.setOnClickListener { onYesClicked(); dialog.dismiss() }
        if(highlight and HIGHLIGHT_YES == 0) yesBtn.setTextColor(ContextCompat.getColor(activity, android.R.color.darker_gray))
        else yesBtn.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        noBtn.setOnClickListener { onNoClicked(); dialog.dismiss() }
        if(highlight and HIGHLIGHT_NO == 0)noBtn.setTextColor(ContextCompat.getColor(activity, android.R.color.darker_gray))
        else noBtn.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        headingTV.text = heading
        messageTV.text = message
        return view
    }

    abstract fun onYesClicked()
    abstract fun onNoClicked()

    companion object {
        private val TAG = "Dialog Yes No"
    }
}