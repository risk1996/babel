package id.ac.umn.mobile.babel

import android.os.Bundle
import android.app.Activity
import android.graphics.Color
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_manage_items.*

class ManageItemsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_items)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val rv = findViewById<RecyclerView>(R.id.activity_manage_items_rv_items)
        rv.setHasFixedSize(true)
        val llm = LinearLayoutManager(applicationContext)
        rv.layoutManager = llm
        // 1
        class Person (_name: String, _age: String) {
            var name: String = ""
            var age: String = ""

            init {
                this.name = _name
                this.age = _age
            }
        }
        // 2
        val persons: List<Person>
        persons = ArrayList()
        persons.add( Person("Emma Wilson", "23 years old"))
        persons.add( Person("Lavery Maiss", "25 years old"))
        persons.add( Person("Lillie Watts", "35 years old"))
        // 3
        val _persons: List<Person>
        RVAdapter(_persons){
            this.persons = persons;
        }

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
    }

}
