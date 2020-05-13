package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CustomListAdapter(private var context: Activity, course: Course) : ArrayAdapter<Any?>(context, R.layout.listview_row, course.cour.toArray()) {
    var checkBoxes = Array(course.cour.size) {false}
    lateinit var layoutDefault: ConstraintLayout.LayoutParams

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val rowView = view ?: LayoutInflater.from(context).inflate(R.layout.listview_row, parent, false)
        if (position == 0) {
            if (!this::layoutDefault.isInitialized)layoutDefault = rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams as ConstraintLayout.LayoutParams
            val layout = ConstraintLayout.LayoutParams((rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams as ConstraintLayout.LayoutParams))
            layout.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54f, context.resources.displayMetrics).toInt()
            rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams = layout
        } else {
            rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams = layoutDefault
        }
        val instTextView = rowView.findViewById<TextView>(R.id.instTextView)
        val timeTextView = rowView.findViewById<TextView>(R.id.timeTextView)
        val lecTextView = rowView.findViewById<TextView>(R.id.lecTextView)
        val checkBox = rowView.findViewById<CheckBox>(R.id.checkBox)
        val instQual = rowView.findViewById<TextView>(R.id.instQual)
        val room = rowView.findViewById<TextView>(R.id.room)
        val capacity = rowView.findViewById<TextView>(R.id.capacity)

        checkBox.isChecked = checkBoxes[position]
        instQual.text = "LOADING"

        instTextView.text = (getItem(position) as Section).inst
        timeTextView.text = (getItem(position) as Section).times
        lecTextView.text = (getItem(position) as Section).lecTitle
        room.text = (getItem(position) as Section).room
        capacity.text = (getItem(position) as Section).enrollCur.toString() + "/" +(getItem(position) as Section).enrollMax

        rowView.setOnClickListener() { view ->
            checkBox.toggle()
            checkBoxes[position] = !checkBoxes[position]
        }
        if ((context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.value?.first != "LOADING" || (context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.value?.first != "N/A" ) {
            instQual.text = (context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.value?.first  ?: "N/A"
        }
        (context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.observeForever {
            //this is stupid
            if (it?.second == (instQual.parent as ViewGroup).findViewById<TextView>(R.id.instTextView).text)
                instQual.text = it?.first ?: "N/A"
        }
        return rowView
    }
}