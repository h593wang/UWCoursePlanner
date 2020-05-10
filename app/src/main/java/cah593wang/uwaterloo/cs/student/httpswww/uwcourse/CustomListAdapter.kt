package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomListAdapter(private var context: Activity, course: Course) : ArrayAdapter<Any?>(context, R.layout.listview_row, course.cour.toArray()) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val rowView = view ?: LayoutInflater.from(context).inflate(R.layout.listview_row, parent, false)
        val instTextView = rowView.findViewById<TextView>(R.id.instTextView)
        val timeTextView = rowView.findViewById<TextView>(R.id.timeTextView)
        val lecTextView = rowView.findViewById<TextView>(R.id.lecTextView)
        instTextView.text = (getItem(position) as Section).inst
        //timeTextView.text = (getItem(position) as Section).times[0].toString() + "-" + (getItem(position) as Section).times[1]
        lecTextView.text = (getItem(position) as Section).lecTitle
        return rowView
    }
}