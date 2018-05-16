package id.ac.umn.mobile.babel

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class AboutActivity : AppCompatActivity() {
// AboutActivity extend AppCompatActivity
//    overrida pada saat oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//      xml nya activity_about
        setContentView(R.layout.activity_about)
    }
// override function onCreate -> setContentView activity_about
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
//  override fun onBackPressed -> back to previous activity
}
