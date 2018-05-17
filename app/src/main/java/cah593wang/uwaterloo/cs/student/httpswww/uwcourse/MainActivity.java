package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("test"+5);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        EditText termEditText = (EditText) findViewById(R.id.termEditText);
        String term = termEditText.getText().toString();

        EditText courseEditText = (EditText) findViewById(R.id.courseCodeEditTest);
        String course = courseEditText.getText().toString();

        EditText depEditText = (EditText) findViewById(R.id.departmentEditText);
        String dep = depEditText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,dep + " " + course + " " + term) ;
        startActivity(intent);

    }

}
