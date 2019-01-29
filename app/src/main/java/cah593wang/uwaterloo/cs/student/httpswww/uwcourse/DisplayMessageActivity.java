package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import static android.widget.AdapterView.*;

public class DisplayMessageActivity extends AppCompatActivity {
    public static final String COURSE_PARAMS = "COURSE_PARAMS";

    Course course;
    @Override
    protected void onCreate(Bundle savedInstanceState) throws NetworkOnMainThreadException {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        String [] params = getIntent().getStringExtra(COURSE_PARAMS).split(" ");
        course = new Course(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2])) {
            @Override
            public void onCourseReturned() {

                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        initAdapter();
                    } // This is your code
                };
                mainHandler.post(myRunnable);

            }
        };




    }

    private void initAdapter() {
        customListAdapter adapter = new customListAdapter(this, course);
        ListView out = findViewById(R.id.display);
        out.setAdapter(adapter);
        out.setOnItemClickListener(new OnItemClickListener() {
            //TODO add info to database

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(DisplayMessageActivity.this, "todo", Toast.LENGTH_SHORT).show();
            }
        });
        out.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DisplayMessageActivity.this, "todo", Toast.LENGTH_SHORT).show();
                return false;
            }

        });
    }
}

