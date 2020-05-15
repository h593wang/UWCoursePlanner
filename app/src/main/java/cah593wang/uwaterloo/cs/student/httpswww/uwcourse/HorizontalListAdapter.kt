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

class HorizontalListAdapter(private var context: Activity, val course: ArrayList<Section>, val selectedSections: ArrayList<Section>, val listener:View.OnClickListener, val deleteListener:View.OnClickListener) : RecyclerView.Adapter<HorizontalListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater.from(context).inflate(R.layout.listview_box, parent, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        //if its empty, we still want a placeholder item
        if (course.size == 0) return 1
        return course.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //handling for placeholder item
        if (course.size == 0) {
            holder.itemView.findViewById<TextView>(R.id.lecTextView).text = "Empty"
            holder.itemView.findViewById<Button>(R.id.delete).visibility = View.GONE
            holder.itemView.findViewById<TextView>(R.id.instTextView).text = ""
            holder.itemView.findViewById<TextView>(R.id.timeTextView).text = ""
            holder.itemView.findViewById<TextView>(R.id.roomView).text = ""
            holder.itemView.findViewById<TextView>(R.id.lecName).text = ""
            holder.itemView.isSelected = false
            holder.itemView.setOnClickListener {  }
            return
        }

        //set the selection status based on the selectedSections info
        holder.itemView.isSelected = false
        selectedSections.forEach {
            if (it.classNum == course[position].classNum) holder.itemView.isSelected = true
        }

        //setting the text based on the section
        holder.itemView.findViewById<Button>(R.id.delete).visibility = View.VISIBLE
        holder.itemView.findViewById<TextView>(R.id.lecTextView).text = course[position].classNum.toString() + " - " + course[position].lecTitle
        holder.itemView.findViewById<TextView>(R.id.instTextView).text = course[position].inst
        holder.itemView.findViewById<TextView>(R.id.timeTextView).text = course[position].getTime(0)
        holder.itemView.findViewById<TextView>(R.id.roomView).text = course[position].getRoom(0)
        holder.itemView.findViewById<TextView>(R.id.lecName).text = course[position].className
        holder.itemView.findViewById<Button>(R.id.delete).setOnClickListener(deleteListener)
        holder.itemView.setOnClickListener(listener)
    }
}