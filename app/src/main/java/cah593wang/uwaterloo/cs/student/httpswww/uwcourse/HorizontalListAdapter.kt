package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class HorizontalListAdapter(private var context: Activity, val course: ArrayList<Section>, val listener:View.OnClickListener, val deleteListener:View.OnClickListener) : RecyclerView.Adapter<HorizontalListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater.from(context).inflate(R.layout.listview_box, parent, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        if (course.size == 0) return 1
        return course.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (course.size == 0) {
            holder.itemView.findViewById<TextView>(R.id.lecTextView).text = "Empty"
            holder.itemView.findViewById<Button>(R.id.delete).visibility = View.GONE
            return
        }
        holder.itemView.findViewById<Button>(R.id.delete).visibility = View.VISIBLE
        holder.itemView.findViewById<TextView>(R.id.lecTextView).text = course[position].classNum.toString() + " - " + course[position].lecTitle
        holder.itemView.findViewById<TextView>(R.id.instTextView).text = course[position].inst
        holder.itemView.findViewById<TextView>(R.id.timeTextView).text = course[position].times
        holder.itemView.findViewById<Button>(R.id.delete).setOnClickListener(deleteListener)
        holder.itemView.setOnClickListener(listener)
    }
}