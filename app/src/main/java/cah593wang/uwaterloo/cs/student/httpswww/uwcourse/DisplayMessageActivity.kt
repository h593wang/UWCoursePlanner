package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.NetworkOnMainThreadException
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.Toast
import cah593wang.uwaterloo.cs.student.httpswww.uwcourse.DisplayMessageActivity

class DisplayMessageActivity : AppCompatActivity() {
    lateinit var course: Course
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
    }

    private fun initAdapter() {
        val adapter = CustomListAdapter(this, course)
        val out = findViewById<ListView>(R.id.display)
        out.adapter = adapter
        out.onItemClickListener = OnItemClickListener { adapterView, view, i, l -> //TODO add info to database
            Toast.makeText(this@DisplayMessageActivity, "todo", Toast.LENGTH_SHORT).show()
        }
        out.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
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