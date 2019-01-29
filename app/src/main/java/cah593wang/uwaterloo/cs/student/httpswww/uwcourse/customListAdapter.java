package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class customListAdapter extends ArrayAdapter {
    private final Activity context;
    public customListAdapter(Activity context, Course course) {
        super(context,R.layout.listview_row, course.cour);
        this.context = context;
    }

    public View getView (int position, View view, ViewGroup parent) {

        View rowView=LayoutInflater.from(context).inflate(R.layout.listview_row, parent, false);

        TextView instTextView =  rowView.findViewById(R.id.instTextView);
        TextView timeTextView =  rowView.findViewById(R.id.timeTextView);
        TextView lecTextView =  rowView.findViewById(R.id.lecTextView);

        instTextView.setText(((section)getItem(position)).getInst());
        timeTextView.setText(((section)getItem(position)).getTimes()[0]+"-"+((section)getItem(position)).getTimes()[1]);
        lecTextView.setText(String.valueOf(((section)getItem(position)).getLecNum()));

        return rowView;
    }
}
