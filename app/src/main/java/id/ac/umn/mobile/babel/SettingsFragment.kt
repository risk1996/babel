package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SettingsFragment : PreferenceFragment() {
//  mengoverride fungsi ketika activity baru dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//      untuk memasukan file xml prefenrence
        addPreferencesFromResource(R.xml.preferences)
    }
}
