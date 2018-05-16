package id.ac.umn.mobile.babel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SettingsActivity : AppCompatActivity() {
// on create pada saat saveinstance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//      memanggil xml yang akan ditampilkan yaitu activity_settings
        setContentView(R.layout.activity_settings)

        fragmentManager.beginTransaction().replace(R.id.activity_settings_fl_frame, SettingsFragment()).commit()
    }
}
