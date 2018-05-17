package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class customListAdapter extends ArrayAdapter {
    private final Activity context;
    private final String[] lecNum;
    private final String[] inst;
    private final String[] time;

    public customListAdapter(Activity context, String[] lecNum, String[] inst, String[] time) {
        super(context,R.layout.listview_row,lecNum);
        this.context = context;
        this.lecNum = lecNum;
        this.inst = inst;
        this.time = time;
    }

    public View getView (int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_row, null, true);

        TextView instTextView = (TextView) rowView.findViewById(R.id.instTextView);
        TextView timeTextView = (TextView) rowView.findViewById(R.id.timeTextView);
        TextView lecTextView = (TextView) rowView.findViewById(R.id.lecTextView);

        instTextView.setText(inst[position]);
        timeTextView.setText(time[position]);
        lecTextView.setText(lecNum[position]);

        return rowView;
    }
}
