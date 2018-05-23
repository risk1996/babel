package id.ac.umn.mobile.babel

import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageButton

class ThumbnailDialog : DialogFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_thumbnail, container, false)
        val thumbnailsGV = view.findViewById<GridView>(R.id.dialog_thumbnail_gv_thumbnails)
        val thumbnailAdapter = ThumbnailAdapter()
        R.drawable::class.java.fields.filter { it.name.contains("icons8_.*?_48".toRegex()) }.forEach {
            Log.d("THUMBNAIL LIST", it.name)
            thumbnailAdapter.thumbnails.add(it.name)
        }
        thumbnailsGV.adapter = thumbnailAdapter
        return  view
    }
    inner class ThumbnailAdapter : BaseAdapter() {
        var thumbnails = ArrayList<String>()
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val view = p1 ?: LayoutInflater.from(p2!!.context).inflate(R.layout.dialog_thumbnail_item, p2, false)
            val btn = view.findViewById<ImageButton>(R.id.dialog_thumbnail_item_bi_item)
            btn.setImageDrawable(resources.getDrawable(R.drawable::class.java.getField(thumbnails[p0]).getInt(null), activity.theme))
            btn.setOnClickListener {

            }
            return  view
        }
        override fun getItem(p0: Int): String = thumbnails[p0]
        override fun getItemId(p0: Int): Long = p0.toLong()
        override fun getCount(): Int = thumbnails.size
    }
}