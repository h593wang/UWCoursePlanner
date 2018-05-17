package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import static android.widget.AdapterView.*;

public class DisplayMessageActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) throws NetworkOnMainThreadException {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);


        netRunnable netRun = new netRunnable();
        netRun.run();


        customListAdapter adapter = new customListAdapter(this, netRun.lec, netRun.inst, netRun.time);


        ListView out = (ListView) findViewById(R.id.display);
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

class netRunnable extends AppCompatActivity implements Runnable {

    public String[] inst;
    public String[] time;
    public String[] lec;

    @Override
    public void run(){

        final String[] output = new String[1];

        Ion.with(getApplicationContext()).load("http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess=1185&subject=CS&cournum=245")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        output[0] = result;
                    }
                });

        course courses = new course(output[0]);

        inst = new String[courses.count];
        time = new String[courses.count];
        lec = new String[courses.count];

        for (int i = 0; i < courses.count; i++) {
            lec[i] = Integer.toString(i + 1);
            inst[i] = courses.cour[i].inst;
            time[i] = Integer.toString(courses.cour[i].times[0]) + "-" + Integer.toString(courses.cour[i].times[1]);
        }



    }
}
