package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ManageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }

    override fun onStart() {
        val list = ArrayList<Item>()
        prepareList(list)
        val rView = activity.findViewById<RecyclerView>(R.id.fragment_manage_items_rv_items)
        rView.adapter = RVAdapter(activity, list)
        rView.layoutManager = GridLayoutManager(activity, list.size, GridLayoutManager.HORIZONTAL, false)

//        val searchSV = findViewById<SearchView>(R.id.activity_manage_items_sv_search)
//        val itemsLayoutLL = findViewById<LinearLayout>(R.id.activity_manage_items_ll_items_layout)
//        val newFAB = findViewById<FloatingActionButton>(R.id.activity_manage_items_fab_new)
//        for(i in 1..10){
//            val newCV = CardView(this)
//            val newLL = LinearLayout(this)
//            val newTV = TextView(this)
//            newTV.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            newTV.text = i.toString()
//            newLL.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            newLL.addView(newTV)
//            newCV.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            newCV.addView(newLL)
//            itemsLayoutLL.addView(newCV)
//        }
//        newFAB.setOnClickListener{
//
//        }

        super.onStart()
    }

    private fun prepareList(list : ArrayList<Item>){
        list.add(Item("Gelcoat", "2.60 Drigen", R.drawable.icons8_tin_can_48))
        list.add(Item("Resin", "103.00 Drigen", R.drawable.icons8_rock_48))
        list.add(Item("CSM 300", "60.00 Rol", R.drawable.icons8_cinnamon_sticks_48))
        list.add(Item("WR 600", "28.00 Rol", R.drawable.icons8_wallpaper_roll_48))
        list.add(Item("WR 800", "44.00 Rol", R.drawable.icons8_wallpaper_roll_48))
        list.add(Item("Multiaxial", "4.50 Rol", R.drawable.icons8_parchment_48))
        list.add(Item("Katalis", "5.00 Galon", R.drawable.icons8_water_48))
        list.add(Item("Mirror", "33.50 Kaleng", R.drawable.icons8_test_tube_48))
        list.add(Item("Aerosil", "32.00 kg", R.drawable.icons8_dust_48))
        list.add(Item("PU Foam A", "271.00 kg", R.drawable.icons8_circled_a_48))
        list.add(Item("PU Foam B", "292.00 kg", R.drawable.icons8_circled_b_48))
    }
}
