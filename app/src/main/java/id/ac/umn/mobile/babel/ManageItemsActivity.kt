package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Activity
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.widget.SearchView

import kotlinx.android.synthetic.main.activity_manage_items.*

class ManageItemsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_items)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val SearchSV = findViewById<SearchView>(R.id.activity_manage_items_sv_search)
        val ItemsRV = findViewById<CardView>(R.id.activity_manage_items_rv_items)
        val NewFAB = findViewById<FloatingActionButton>(R.id.activity_manage_items_fab_new)
//        for(int i=0)
//        ItemsRV.addView()
        NewFAB.setOnClickListener{

        }
    }

}
