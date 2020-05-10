package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun searchClass(view: View?) {
        val intent = Intent(this, DisplayMessageActivity::class.java)
        val termEditText = findViewById<EditText>(R.id.termEditText)
        val term = termEditText.text.toString().toInt()
        val courseEditText = findViewById<EditText>(R.id.courseCodeEditTest)
        val course = courseEditText.text.toString().toInt()
        val depEditText = findViewById<EditText>(R.id.departmentEditText)
        val dep = depEditText.text.toString()
        intent.putExtra(DEP, dep.toUpperCase())
        intent.putExtra(COURSE_NUM, course)
        intent.putExtra(TERM, term)
        startActivity(intent)
    }

    companion object {
        const val DEP = "DEP"
        const val COURSE_NUM = "COURSE_NUM"
        const val TERM = "TERM"
    }
}