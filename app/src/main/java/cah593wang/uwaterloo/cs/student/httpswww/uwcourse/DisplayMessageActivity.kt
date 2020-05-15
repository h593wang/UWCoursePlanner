package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.NetworkOnMainThreadException
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView.*
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import java.lang.Exception

class DisplayMessageActivity : AppCompatActivity() {
    lateinit var course: Course
    lateinit var display: ListView

    @Throws(NetworkOnMainThreadException::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val department = intent.getStringExtra(DEP)
        val courseNum = intent.getIntExtra(COURSE_NUM, 0)
        val term = intent.getIntExtra(TERM, 0)

        findViewById<TextView>(R.id.availableSections).bringToFront()
        //start the course class to get course info from the webscraper inside the class
            course = object : Course(department, courseNum, term, this.application) {
                override fun onCourseReturned() {
                    val mainHandler = Handler(Looper.getMainLooper());
                    val myRunnable = Runnable {
                        //when the data is returned, use it to initialize the adapter
                        initAdapter()
                    };
                    mainHandler.post(myRunnable)
                }

                override fun onFailed(e: Exception) {
                    finishFail(e.message ?: "ERROR")
                }
            }


        findViewById<Button>(R.id.button).setOnClickListener {
            val returnIntent = Intent()
            val checkBoxes = (display.adapter as CustomListAdapter).checkBoxes
            var serializedResult = ArrayList<Section>()
            for (i in checkBoxes.indices) {
                if (checkBoxes[i]) {
                    serializedResult.add(display.adapter.getItem(i) as Section)
                }
            }
            returnIntent.putExtra("RESULT", serializedResult)
            setResult(Activity.RESULT_OK,returnIntent)
            finish()
        }
    }

    private fun initAdapter() {
        findViewById<TextView>(R.id.loading).visibility = View.GONE

        //initialize the listview adapter with the course info
        val adapter = CustomListAdapter(this, course)
        display = findViewById(R.id.display)
        display.adapter = adapter
    }

    private fun finishFail(result: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("RESULT", result)
        setResult(Activity.RESULT_CANCELED,returnIntent)
        finish()
    }

    companion object {
        const val DEP = "DEP"
        const val COURSE_NUM = "COURSE_NUM"
        const val TERM = "TERM"
    }
}