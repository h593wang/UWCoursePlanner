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

        //if its the first item, add margins so it wont be under the header
        if (position == 0) {
            if (!this::layoutDefault.isInitialized)layoutDefault = rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams as ConstraintLayout.LayoutParams
            val layout = ConstraintLayout.LayoutParams((rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams as ConstraintLayout.LayoutParams))
            layout.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58f, context.resources.displayMetrics).toInt()
            rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams = layout
        } else {
            rowView.findViewById<ConstraintLayout>(R.id.layout).layoutParams = layoutDefault
        }

        val instTextView = rowView.findViewById<TextView>(R.id.instTextView)
        val timeTextView = rowView.findViewById<TextView>(R.id.timeTextView)
        val room = rowView.findViewById<TextView>(R.id.room)
        val lecTextView = rowView.findViewById<TextView>(R.id.lecTextView)
        val checkBox = rowView.findViewById<CheckBox>(R.id.checkBox)
        val instQual = rowView.findViewById<TextView>(R.id.instQual)
        val capacity = rowView.findViewById<TextView>(R.id.capacity)
        val timeTextView2 = rowView.findViewById<TextView>(R.id.timeTextView2)
        val room2 = rowView.findViewById<TextView>(R.id.room2)
        //setting the text based on the section info
        checkBox.isChecked = checkBoxes[position]
        instQual.text = "LOADING"
        instTextView.text = (getItem(position) as Section).inst
        timeTextView.text = (getItem(position) as Section).getTime(0)
        lecTextView.text = (getItem(position) as Section).lecTitle
        room.text = (getItem(position) as Section).getRoom(0)
        capacity.text = (getItem(position) as Section).enrollCur.toString() + "/" +(getItem(position) as Section).enrollMax
        //handling multiple location/times
        if ((getItem(position) as Section).times.size > 1) {
            timeTextView2.visibility = View.VISIBLE
            timeTextView2.text = (getItem(position) as Section).getTime(1)
        } else {
            timeTextView2.visibility = View.GONE
        }
        if ((getItem(position) as Section).room.size > 1) {
            room2.visibility = View.VISIBLE
            room2.text = (getItem(position) as Section).getRoom(1)
        } else {
            room2.visibility = View.GONE
        }

        //when the item is clicked, toggle the checkBox
        rowView.setOnClickListener() { view ->
            checkBox.toggle()
            checkBoxes[position] = !checkBoxes[position]
        }

        //if the prof data is ready, use it
        if ((context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.value?.first != "LOADING" || (context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.value?.first != "N/A" ) {
            instQual.text = (context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.value?.first  ?: "N/A"
        }
        //if its not ready, observe it until it is
        (context.application as ApplicationBase).profRatings[(getItem(position) as Section).inst]?.observeForever {
            //since the items are reused, we need to make sure its still the prof we want
            if (it?.second == instTextView.text)
                instQual.text = it?.first ?: "N/A"
        }
        return rowView
    }
}