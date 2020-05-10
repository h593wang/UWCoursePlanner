package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    var sectionList = ArrayList<Section>()
    var selectedSections = ArrayList<Section>()
    lateinit var imageView: ImageView
    lateinit var display: RecyclerView
    lateinit var canvas: Canvas
    var paint = Paint()
    lateinit var bitmap: Bitmap
    var rect = Rect()
    var courseColor = 0
    var conflictColor = 0
    var backgroundColor = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newEntries = data?.getSerializableExtra("RESULT")
            if (newEntries != null) {
                (newEntries as ArrayList<Section>).forEach { sec ->
                    var contains = false
                    sectionList.forEach { ownedSec -> if (ownedSec.classNum == sec.classNum) contains = true}
                    if (!contains) sectionList.add(sec)
                }
            }
        }
        display.adapter.notifyDataSetChanged()
    }

    private fun initAdapter() {
        display = findViewById(R.id.classSum)

        val adapter = HorizontalListAdapter(this, sectionList, View.OnClickListener { view ->
            view.isSelected = view.isSelected != true
            processCartClick(view.findViewById<TextView>(R.id.lecTextView).text.toString(), view.isSelected)
        }, View.OnClickListener { view ->
            val indexToDelete = getIndexByTitle((view.parent as View).findViewById<TextView>(R.id.lecTextView).text.toString())
            sectionList.removeAt(indexToDelete)
            display.adapter.notifyDataSetChanged()
        })
        val linearLayoutManager =  LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayout.HORIZONTAL
        display.layoutManager = linearLayoutManager
        display.adapter = adapter

    }

    private fun processCartClick(text: String, selection: Boolean) {
        val indexClicked = getIndexByTitle(text)
        if (selection) selectedSections.add(sectionList[indexClicked])
        else {
            val sectionNum = sectionList[indexClicked].classNum
            for (i in selectedSections.indices) {
                if (selectedSections[i].classNum == sectionNum) {
                    selectedSections.removeAt(i)
                    break
                }
            }
        }
        redrawCanvas()
    }

    private fun getIndexByTitle(text: String): Int {
        val classNum = text.split(" - ")[0]
        for (i in sectionList.indices) {
            if (sectionList[i].classNum == classNum.toInt()) return i
        }
        return -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button3).setOnClickListener {searchClass()}
        imageView = findViewById<ImageView>(R.id.canvas)
        conflictColor = ResourcesCompat.getColor(resources, R.color.conflict, null)
        courseColor = ResourcesCompat.getColor(resources, R.color.course, null)
        backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)

        paint.color = backgroundColor

        initAdapter()
    }

    private fun redrawCanvas() {
        val width = imageView.width
        val height = imageView.height
    }

    fun searchClass() {
        val intent = Intent(this, DisplayMessageActivity::class.java)
        val termEditText = findViewById<EditText>(R.id.termEditText)
        val term = termEditText.text.toString().toInt()
        termEditText.setText("")

        val courseEditText = findViewById<EditText>(R.id.courseCodeEditTest)
        val course = courseEditText.text.toString().toInt()
        courseEditText.setText("")

        val depEditText = findViewById<EditText>(R.id.departmentEditText)
        val dep = depEditText.text.toString()
        depEditText.setText("")
        intent.putExtra(DEP, dep.toUpperCase())
        intent.putExtra(COURSE_NUM, course)
        intent.putExtra(TERM, term)
        startActivityForResult(intent, REQUEST_CODE)
    }

    companion object {
        const val DEP = "DEP"
        const val COURSE_NUM = "COURSE_NUM"
        const val TERM = "TERM"
        const val REQUEST_CODE = 8011
    }
}