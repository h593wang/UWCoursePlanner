package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.content.Context
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
import android.util.TypedValue
import android.view.View
import android.widget.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class MainActivity : AppCompatActivity() {
    var sectionList = ArrayList<Section>()
    var selectedSections = ArrayList<Section>()
    lateinit var imageView: ImageView
    lateinit var display: RecyclerView
    lateinit var canvas: Canvas
    var paint = Paint()
    var textPaint = Paint()
    lateinit var bitmap: Bitmap
    var rect = Rect()
    var courseColor = 0
    var conflictColor = 0
    var backgroundColor = 0
    var white = 0
    var black = 0

    var width: Int = 0
    var height: Int = 0
    val headerSize = 50f
    var sectionWidth = 0
    val offset = 25f


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

        val adapter = HorizontalListAdapter(this, sectionList, selectedSections, View.OnClickListener { view ->
            view.isSelected = view.isSelected != true
            processCartClick(view.findViewById<TextView>(R.id.lecTextView).text.toString(), view.isSelected)
        }, View.OnClickListener { view ->
            val indexToDelete = getIndexByTitle((view.parent as View).findViewById<TextView>(R.id.lecTextView).text.toString())
            val idToRemove = sectionList[indexToDelete].classNum
            sectionList.removeAt(indexToDelete)
            display.adapter.notifyDataSetChanged()
            for (i in selectedSections.indices) {
                if (selectedSections[i].classNum == idToRemove) {
                    selectedSections.removeAt(i)
                    break
                }
            }
            redrawCanvas()
        })
        val linearLayoutManager =  LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayout.HORIZONTAL
        display.layoutManager = linearLayoutManager
        display.adapter = adapter

    }

    private fun processCartClick(text: String, selection: Boolean) {
        if (sectionList.size == 0) return
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

        try {
            val fis2: FileInputStream = openFileInput("cart.data")
            val is2 = ObjectInputStream(fis2)
            sectionList = is2.readObject() as ArrayList<Section>
            is2.close()
            fis2.close()

            val fis: FileInputStream = openFileInput("selected.data")
            val ins = ObjectInputStream(fis)
            selectedSections = ins.readObject() as ArrayList<Section>
            ins.close()
            fis.close()

        } catch (e: Exception) {
        }

        findViewById<Button>(R.id.button3).setOnClickListener {searchClass()}
        imageView = findViewById<ImageView>(R.id.canvas)
        imageView.viewTreeObserver.addOnGlobalLayoutListener { redrawCanvas() }
        conflictColor = ResourcesCompat.getColor(resources, R.color.conflict, null)
        courseColor = ResourcesCompat.getColor(resources, R.color.course, null)
        backgroundColor = ResourcesCompat.getColor(resources, R.color.grey, null)
        white = ResourcesCompat.getColor(resources, R.color.white, null)
        black = ResourcesCompat.getColor(resources, R.color.black, null)

        paint.color = backgroundColor

        initAdapter()
    }

    override fun onStop() {
        val fos: FileOutputStream = getApplicationContext().openFileOutput("selected.data", Context.MODE_PRIVATE)
        val fos2: FileOutputStream = getApplicationContext().openFileOutput("cart.data", Context.MODE_PRIVATE)
        val os = ObjectOutputStream(fos)
        val os2 = ObjectOutputStream(fos2)
        os.writeObject(selectedSections)
        os2.writeObject(sectionList)
        os.close()
        fos.close()
        os2.close()
        fos2.close()
        super.onStop()
    }

    private fun redrawCanvas() {
        width = imageView.width
        height = imageView.height

        // 5 weekdays + 2 half width weekends
        sectionWidth = width/6
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        imageView.setImageBitmap(bitmap)

        canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)

        var xPosition = sectionWidth/ 2.toFloat()
        paint.color = white
        //sunday
        canvas.drawLine(xPosition,0f,xPosition, height.toFloat(), paint)
        //monday
        xPosition += sectionWidth
        canvas.drawLine(xPosition,0f,xPosition, height.toFloat(), paint)
        //tuesday
        xPosition += sectionWidth
        canvas.drawLine(xPosition,0f,xPosition, height.toFloat(), paint)
        //wednesday
        xPosition += sectionWidth
        canvas.drawLine(xPosition,0f,xPosition, height.toFloat(), paint)
        //thursday
        xPosition += sectionWidth
        canvas.drawLine(xPosition,0f,xPosition, height.toFloat(), paint)
        //friday
        xPosition += sectionWidth
        canvas.drawLine(xPosition,0f,xPosition, height.toFloat(), paint)

        canvas.drawLine(0f, 50f, width.toFloat(), headerSize, paint)

        textPaint.color = black
        textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12F, resources.displayMetrics)
        val yVal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12F, resources.displayMetrics)
        xPosition = 10f
        canvas.drawText("Sun", xPosition, yVal, textPaint)
        xPosition += sectionWidth*3/4
        canvas.drawText("Mon", xPosition, yVal, textPaint)
        xPosition += sectionWidth
        canvas.drawText("Tue", xPosition, yVal, textPaint)
        xPosition += sectionWidth
        canvas.drawText("Wed", xPosition, yVal, textPaint)
        xPosition += sectionWidth
        canvas.drawText("Thu", xPosition, yVal, textPaint)
        xPosition += sectionWidth
        canvas.drawText("Fri", xPosition, yVal, textPaint)
        xPosition += sectionWidth*3/4
        canvas.drawText("Sat", xPosition, yVal, textPaint)

        paint.color = courseColor
        textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 7F, resources.displayMetrics)
        selectedSections.forEach {
            drawSection(it)
        }

        //detect conflicts
        paint.color = conflictColor
        val timeRanges = ArrayList<IntRange>(selectedSections.size)
        for (i in selectedSections.indices) {
            val sec = selectedSections[i]
            timeRanges.add(IntRange(sec.getStartHour()*60+sec.getStartMin(), sec.getEndHour()*60 + sec.getEndMin()))
        }
        for (i in timeRanges.indices) {
            for (x in timeRanges.indices) {
                if (i == x) continue
                var foundCommonDate = false
                if (timeRanges[i]?.contains(timeRanges[x].start) || timeRanges[i].contains(timeRanges[x].endInclusive)) {
                    //theres an intersect
                    val maxStart = Math.max(timeRanges[i].start, timeRanges[x].start)
                    val minEnd = Math.min(timeRanges[i].endInclusive, timeRanges[x].endInclusive)
                    val startY = ((maxStart - 7*60f) / (15*60f) * height)
                    val endY = ((minEnd - 7*60f) / (15*60f) * height)

                    if (selectedSections[i].times.contains("Su") && selectedSections[x].times.contains("Su"))
                        canvas.drawRoundRect(0f, startY,  sectionWidth/2f, endY, 10f, 10f, paint)
                    if (selectedSections[i].times.contains("M") && selectedSections[x].times.contains("M"))
                        canvas.drawRoundRect(sectionWidth/2f, startY,  sectionWidth/2f + sectionWidth, endY, 10f, 10f, paint)
                    if (selectedSections[i].times.contains("Tu") && selectedSections[x].times.contains("Tu"))
                        canvas.drawRoundRect(sectionWidth/2f + sectionWidth, startY,  sectionWidth/2f + 2*sectionWidth, endY, 10f, 10f, paint)
                    if (selectedSections[i].times.contains("W") && selectedSections[x].times.contains("W"))
                        canvas.drawRoundRect(sectionWidth/2f + 2*sectionWidth, startY,  sectionWidth/2f + 3f*sectionWidth, endY, 10f, 10f, paint)
                    if (selectedSections[i].times.contains("Th") && selectedSections[x].times.contains("Th"))
                        canvas.drawRoundRect(sectionWidth/2f + 3*sectionWidth, startY,  sectionWidth/2f + 4f*sectionWidth, endY, 10f, 10f, paint)
                    if (selectedSections[i].times.contains("F") && selectedSections[x].times.contains("F"))
                        canvas.drawRoundRect(sectionWidth/2f + 4*sectionWidth, startY,  sectionWidth/2f + 5f*sectionWidth, endY, 10f, 10f, paint)
                    if (selectedSections[i].times.contains("S") && selectedSections[x].times.contains("S"))
                        canvas.drawRoundRect(sectionWidth/2f + 5*sectionWidth, startY,  6f*sectionWidth, endY, 10f, 10f, paint)
                }
            }
        }
    }

    private fun drawSection(section: Section) {
        val startY = ((section.getStartHour()*60 + section.getStartMin() - 7*60f) / (15*60f) * height)
        val endY = ((section.getEndHour()*60 + section.getEndMin() - 7*60f) / (15*60f) * height)

        val line = 20f

        if (section.times.contains("Su")) {
            canvas.drawRoundRect(0f, startY, sectionWidth/2f, endY, 10f, 10f, paint)
            canvas.drawText(section.className, 0 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, 0 + offset, startY + offset + line, textPaint)

        }
        if (section.times.contains("M")) {
            canvas.drawRoundRect(sectionWidth/2f, startY, sectionWidth/2f + sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + offset, startY + offset + line, textPaint)
        }
        if (section.times.contains("Tu")) {
            canvas.drawRoundRect(sectionWidth/2f + sectionWidth, startY, sectionWidth/2f + 2*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth + offset, startY + offset + line, textPaint)
        }
        if (section.times.contains("W")) {
            canvas.drawRoundRect(sectionWidth/2f + 2*sectionWidth, startY, sectionWidth/2f + 3*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 2 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 2 + offset, startY + offset + line, textPaint)
        }
        if (section.times.contains("Th")) {
            canvas.drawRoundRect(sectionWidth/2f + 3*sectionWidth, startY, sectionWidth/2f + 4*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 3 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 3 + offset, startY + offset + line, textPaint)
        }
        if (section.times.contains("F")) {
            canvas.drawRoundRect(sectionWidth/2f + 4*sectionWidth, startY, sectionWidth/2f + 5*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 4 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 4 + offset, startY + offset + line, textPaint)
        }
        if (section.times.contains("S")) {
            canvas.drawRoundRect(sectionWidth/2f + 5*sectionWidth, startY,  6f*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 5 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 5 + offset, startY + offset + line, textPaint)
        }
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