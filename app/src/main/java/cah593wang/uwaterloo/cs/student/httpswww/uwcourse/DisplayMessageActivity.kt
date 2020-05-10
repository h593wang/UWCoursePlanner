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
        course = object : Course(department, courseNum, term) {
            override fun onCourseReturned() {
                val mainHandler = Handler(Looper.getMainLooper());
                val myRunnable = Runnable {
                        initAdapter() // This is your code
                };
                mainHandler.post(myRunnable)
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            val returnIntent = Intent()
            val checkBoxes = (display.adapter as CustomListAdapter).checkBoxes
            var serializedResult = ArrayList<Section>()
            for (i in checkBoxes.indices) {
                if (checkBoxes[i]?.isChecked ?: false) {
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

        val adapter = CustomListAdapter(this, course)
        display = findViewById<ListView>(R.id.display)
        display.adapter = adapter
        display.onItemClickListener = OnItemClickListener { adapterView, view, i, l -> //TODO add info to database
            Toast.makeText(this@DisplayMessageActivity, "todo", Toast.LENGTH_SHORT).show()
        }
        display.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            Toast.makeText(this@DisplayMessageActivity, "todo", Toast.LENGTH_SHORT).show()
            false
        }
    }

    companion object {
        const val DEP = "DEP"
        const val COURSE_NUM = "COURSE_NUM"
        const val TERM = "TERM"
    }
}