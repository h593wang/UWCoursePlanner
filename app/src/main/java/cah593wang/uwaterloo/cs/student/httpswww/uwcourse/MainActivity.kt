package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
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
    var textPaint = TextPaint()
    lateinit var bitmap: Bitmap
    var rect = Rect()
    var courseColor = 0
    var conflictColor = 0
    var backgroundColor = 0
    var accentColor = 0
    var grey = 0
    var white = 0
    var black = 0

    var width: Int = 0
    var height: Int = 0
    val headerSize = 50f
    var sectionWidth = 0
    val offset = 25f

    //handle selected sections returned by the search result selection activity (DisplayMessageActivity)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newEntries = data?.getSerializableExtra("RESULT")
            if (newEntries != null) {
                //only add it if we don't already have a copy
                (newEntries as ArrayList<Section>).forEach { sec ->
                    var contains = false
                    sectionList.forEach { ownedSec -> if (ownedSec.classNum == sec.classNum) contains = true}
                    if (!contains) sectionList.add(sec)
                }
            }
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            val t = Toast.makeText(this, data?.getStringExtra("RESULT"), Toast.LENGTH_LONG)
            t.show()
        }
        //update the cart recyclerview
        display.adapter?.notifyDataSetChanged()
    }

    //initialize the cart recyclerview
    private fun initAdapter() {
        display = findViewById(R.id.classSum)
        //create a instance of the custom adapter, pass it all the sections in the cart along with the selected ones (might be selected form save data
        val adapter = HorizontalListAdapter(this, sectionList, selectedSections, View.OnClickListener { view ->
            //listener for when a view is selected
            view.isSelected = view.isSelected != true
            processCartClick(view.findViewById<TextView>(R.id.lecTextView).text.toString(), view.isSelected)
        }, View.OnClickListener { view ->
            //listener for when the x is clicked
            val indexToDelete = getIndexByTitle((view.parent as View).findViewById<TextView>(R.id.lecTextView).text.toString())
            val idToRemove = sectionList[indexToDelete].classNum
            //remove it from the section set
            sectionList.removeAt(indexToDelete)
            //if it was selected, remove it from the selected set
            for (i in selectedSections.indices) {
                if (selectedSections[i].classNum == idToRemove) {
                    selectedSections.removeAt(i)
                    break
                }
            }
            //update the adapter and the graphics
            display.adapter?.notifyDataSetChanged()
            redrawCanvas()
        })
        val linearLayoutManager =  LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayout.HORIZONTAL
        display.layoutManager = linearLayoutManager
        display.adapter = adapter
    }

    //processing for whenever a cart section is clicked
    private fun processCartClick(lectureInfo: String, selection: Boolean) {
        //if it was the place holder empty item, return
        if (sectionList.size == 0) return
        //get the index based on the class number contained inside lectureInfo
        val indexClicked = getIndexByTitle(lectureInfo)
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

    //get the index of the section with the given classInfo
    private fun getIndexByTitle(classInfo: String): Int {
        val classNum = classInfo.split(" - ")[0]
        for (i in sectionList.indices) {
            if (sectionList[i].classNum == classNum.toInt()) return i
        }
        return -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //load saved data
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
        //setting up the canvas for the calandar view
        imageView = findViewById<ImageView>(R.id.canvas)
        imageView.viewTreeObserver.addOnGlobalLayoutListener { redrawCanvas() }
        conflictColor = ResourcesCompat.getColor(resources, R.color.conflict, null)
        courseColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        backgroundColor = ResourcesCompat.getColor(resources, R.color.colorMiddle, null)
        accentColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
        grey = ResourcesCompat.getColor(resources, R.color.grey, null)
        white = ResourcesCompat.getColor(resources, R.color.white, null)
        black = ResourcesCompat.getColor(resources, R.color.black, null)

        paint.color = backgroundColor
        textPaint.typeface = ResourcesCompat.getFont(this, R.font.segoe)

        //initialize the cart recycler view adapter
        initAdapter()
    }

    //save the data
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

    //NN
    //redraws the calendar based on the current selected set
    private fun redrawCanvas() {
        width = imageView.width
        height = imageView.height
        if (width == 0 || height == 0) return

        sectionWidth = width/6
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        imageView.setImageBitmap(bitmap)

        //drawing the header
        canvas = Canvas(bitmap)
        paint.color=backgroundColor
        canvas.drawRoundRect(0f,0f, width.toFloat(), height.toFloat(), 10f, 10f, paint)

        paint.color = accentColor
        canvas.drawRoundRect(0f,0f,width.toFloat(), headerSize,10f, 10f, paint)
        canvas.drawRect(0f,10f,width.toFloat(), headerSize, paint)

        // 5 weekdays + 2 half width weekends, draws the vertical lines
        var xPosition = sectionWidth/ 2.toFloat()
        paint.color = grey
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

        //draw hour marks, horizontal line
        val yStep = height/15f
        var yPosition = 0f
        textPaint.color = accentColor
        for (i in 0 until 15) {
            yPosition += yStep
            if (i == 4) {
                canvas.drawText("12PM", 10f, yPosition-5, textPaint)
                paint.color = accentColor
            }
            if (yPosition > headerSize) canvas.drawLine(0f,yPosition,width.toFloat(), yPosition, paint)
            paint.color = grey
        }

        canvas.drawLine(0f, 50f, width.toFloat(), headerSize, paint)

        textPaint.color = white
        textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10F, resources.displayMetrics)
        val yVal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12F, resources.displayMetrics)
        xPosition = 20f
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

        //drawing the sections
        paint.color = courseColor
        textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 7F, resources.displayMetrics)
        selectedSections.forEach {
            for (i in it.times.indices) { drawSection(it, i) }
        }

        //detect conflicts
        //map for the time range to the days of week it repersents
        val sectionMap = HashMap<Int, String>()
        var counter = 0
        paint.color = conflictColor
        val timeRanges = ArrayList<IntRange>(selectedSections.size)
        for (i in selectedSections.indices) {
            val sec = selectedSections[i]
            for (x in sec.times.indices) {
                if (sec.getTime(x) == "TBA" || sec.getTime(x) == "") continue
                timeRanges.add(IntRange(sec.getStartHour(x) * 60 + sec.getStartMin(x), sec.getEndHour(x) * 60 + sec.getEndMin(x)))
                sectionMap[counter] = sec.getTime(x)
                counter++
            }
        }
        for (i in timeRanges.indices) {
            for (x in timeRanges.indices) {
                if (i == x) continue
                if (timeRanges[i].contains(timeRanges[x].start) || timeRanges[i].contains(timeRanges[x].endInclusive)) {
                    //theres an intersect
                    val maxStart = Math.max(timeRanges[i].start, timeRanges[x].start)
                    val minEnd = Math.min(timeRanges[i].endInclusive, timeRanges[x].endInclusive)
                    val startY = ((maxStart - 7*60f) / (15*60f) * height)
                    val endY = ((minEnd - 7*60f) / (15*60f) * height)

                    if (sectionMap[i]?.contains("Su") == true && sectionMap[x]?.contains("Su") == true)
                        canvas.drawRoundRect(0f, startY,  sectionWidth/2f, endY, 10f, 10f, paint)
                    if (sectionMap[i]?.contains("M") == true && sectionMap[x]?.contains("M") == true)
                        canvas.drawRoundRect(sectionWidth/2f, startY,  sectionWidth/2f + sectionWidth, endY, 10f, 10f, paint)
                    if (sectionMap[i]?.contains("Tu") == true && sectionMap[x]?.contains("Tu") == true)
                        canvas.drawRoundRect(sectionWidth/2f + sectionWidth, startY,  sectionWidth/2f + 2*sectionWidth, endY, 10f, 10f, paint)
                    if (sectionMap[i]?.contains("W") == true && sectionMap[x]?.contains("W") == true)
                        canvas.drawRoundRect(sectionWidth/2f + 2*sectionWidth, startY,  sectionWidth/2f + 3f*sectionWidth, endY, 10f, 10f, paint)
                    if (sectionMap[i]?.contains("Th") == true && sectionMap[x]?.contains("Th") == true)
                        canvas.drawRoundRect(sectionWidth/2f + 3*sectionWidth, startY,  sectionWidth/2f + 4f*sectionWidth, endY, 10f, 10f, paint)
                    if (sectionMap[i]?.contains("F") == true && sectionMap[x]?.contains("F") == true)
                        canvas.drawRoundRect(sectionWidth/2f + 4*sectionWidth, startY,  sectionWidth/2f + 5f*sectionWidth, endY, 10f, 10f, paint)
                    if (sectionMap[i]?.contains("S") == true && sectionMap[x]?.contains("S") == true)
                        canvas.drawRoundRect(sectionWidth/2f + 5*sectionWidth, startY,  6f*sectionWidth, endY, 10f, 10f, paint)
                }
            }
        }
    }

    //code for drawing the given section onto the calendar
    private fun drawSection(section: Section, index: Int) {
        if (section.times[index] == "TBA" || section.times[index] == "") return
        val startY = ((section.getStartHour(index)*60 + section.getStartMin(index) - 7*60f) / (15*60f) * height)
        val endY = ((section.getEndHour(index)*60 + section.getEndMin(index) - 7*60f) / (15*60f) * height)

        val line = 20f

        if (section.times[index].contains("Su")) {
            canvas.drawRoundRect(0f, startY, sectionWidth/2f, endY, 10f, 10f, paint)
            canvas.drawText(section.className, 0 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, 0 + offset, startY + offset + line, textPaint)

        }
        if (section.times[index].contains("M")) {
            canvas.drawRoundRect(sectionWidth/2f, startY, sectionWidth/2f + sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + offset, startY + offset + line, textPaint)
        }
        if (section.times[index].contains("Tu")) {
            canvas.drawRoundRect(sectionWidth/2f + sectionWidth, startY, sectionWidth/2f + 2*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth + offset, startY + offset + line, textPaint)
        }
        if (section.times[index].contains("W")) {
            canvas.drawRoundRect(sectionWidth/2f + 2*sectionWidth, startY, sectionWidth/2f + 3*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 2 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 2 + offset, startY + offset + line, textPaint)
        }
        if (section.times[index].contains("Th")) {
            canvas.drawRoundRect(sectionWidth/2f + 3*sectionWidth, startY, sectionWidth/2f + 4*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 3 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 3 + offset, startY + offset + line, textPaint)
        }
        if (section.times[index].contains("F")) {
            canvas.drawRoundRect(sectionWidth/2f + 4*sectionWidth, startY, sectionWidth/2f + 5*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 4 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 4 + offset, startY + offset + line, textPaint)
        }
        if (section.times[index].contains("S")) {
            canvas.drawRoundRect(sectionWidth/2f + 5*sectionWidth, startY,  6f*sectionWidth, endY, 10f, 10f, paint)
            canvas.drawText(section.className, sectionWidth/2f + sectionWidth * 5 + offset, startY + offset, textPaint)
            canvas.drawText(section.lecTitle, sectionWidth/2f + sectionWidth * 5 + offset, startY + offset + line, textPaint)
        }
    }

    //NN

    //formats data and passet it to the DisplayMessageActivity
    fun searchClass() {
        val intent = Intent(this, DisplayMessageActivity::class.java)
        val termEditText = findViewById<EditText>(R.id.termEditText)
        val term = termEditText.text.toString()
        var termFormatted = 0
        //TODO validate the entered info

        //handling the #### format or the [FWS]## format
        if (Regex("[0-9][0-9][0-9][0-9]").matches(term)) termFormatted = term.toInt()
        else if (Regex("[FWS][0-9][0-9]").matches(term)) {
            if (term[0] == 'F') termFormatted = 1000 + 100*term[1].toString().toInt() + 10 * term[2].toString().toInt() + 9
            else if (term[0] == 'W') termFormatted = 1000 + 100*term[1].toString().toInt() + 10 * term[2].toString().toInt() + 1
            else if (term[0] == 'S') termFormatted = 1000 + 100*term[1].toString().toInt() + 10 * term[2].toString().toInt() + 5
        }

        val courseEditText = findViewById<EditText>(R.id.courseCodeEditTest)
        val courseString = courseEditText.text
        if (courseString.isNullOrEmpty()) return
        val course = courseEditText.text.toString().toInt()
        courseEditText.setText("")

        val depEditText = findViewById<EditText>(R.id.departmentEditText)
        val dep = depEditText.text.toString()
        if (dep.isEmpty()) return
        depEditText.setText("")
        intent.putExtra(DEP, dep.toUpperCase())
        intent.putExtra(COURSE_NUM, course)
        intent.putExtra(TERM, termFormatted)
        if (termFormatted != 0) startActivityForResult(intent, REQUEST_CODE)
    }

    companion object {
        const val DEP = "DEP"
        const val COURSE_NUM = "COURSE_NUM"
        const val TERM = "TERM"
        const val REQUEST_CODE = 8011
    }
}