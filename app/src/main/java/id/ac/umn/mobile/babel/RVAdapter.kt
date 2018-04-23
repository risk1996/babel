package id.ac.umn.mobile.babel

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RVAdapter : RecyclerView.Adapter<RVAdapter.PersonViewHolder>() {

    class PersonViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cv: CardView
        internal var personName: TextView
        internal var personAge: TextView

        init {
            cv = itemView.findViewById<View>(R.id.cv) as CardView
            personName = itemView.findViewById<View>(R.id.person_name) as TextView
            personAge = itemView.findViewById<View>(R.id.person_age) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
