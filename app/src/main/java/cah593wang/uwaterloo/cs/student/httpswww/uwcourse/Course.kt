package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

abstract class Course(private var dep: String?, private var courseCode: Int, private var term: Int) {
    var dataString = ""
    var count = 0
    lateinit var dataDoc : Document

    abstract fun onCourseReturned()

    lateinit var cour: ArrayList<Section>

    init {
        GetInfoTask().execute((URL("http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess=$term&subject=$dep&cournum=$courseCode")))
    }

    fun initCourse() {
        populateUW()
        onCourseReturned()
    }

    private fun populateUW() {
        cour = ArrayList()
        var elementArr = dataDoc.getElementsByTag("tr")
        lateinit var eleTable :Element
        elementArr.forEach { ele ->
            ele.getElementsByTag("table")?.let { if (it.size != 0) eleTable = it[0] }
        }

        elementArr = eleTable.getElementsByTag("tr")
        elementArr.removeAt(0)

        var curIndex = -1
        elementArr.forEach { ele ->
            if (ele.childNode(0).childNode(0).toString().trim().matches(Regex("[0-9]+"))) {
                curIndex++
                cour.add(Section())
                //unfortunately ill have to assume its always ordered, and the order doesn't change on their end
                var fieldIndex = 0
                ele.getElementsByTag("td").forEach { field ->
                    when (fieldIndex) {
                        0 -> cour[curIndex].classNum = safeGetInt(field)
                        1 -> cour[curIndex].lecTitle = safeGet(field)
                        2 -> cour[curIndex].campLoc = safeGet(field)
                        //3 ->
                        //4 ->
                        //5 ->
                        6 -> cour[curIndex].enrollMax = safeGetInt(field)
                        7 -> cour[curIndex].enrollCur = safeGetInt(field)
                        //8 ->
                        //9 ->
                        10 -> cour[curIndex].times = normalizeTime(safeGet(field))
                        11 -> cour[curIndex].room = safeGet(field)
                        12 -> cour[curIndex].inst = safeGet(field)
                    }

                    fieldIndex++
                }
            }
            //todo handle additional data
        }
    }

    private fun normalizeTime(time: String): String {
        var time = time.replace("T", "Tu")
        time = time.replace("Tuh", "Th")
        return time
    }

    private fun safeGet (field: Element): String {
        if (field.childNodeSize() == 0) return ""
        return field.childNode(0).toString()
    }

    private fun safeGetInt (field: Element): Int{
        return safeGet(field).trim().toIntOrNull() ?: 0
    }
    private inner class GetInfoTask : AsyncTask<URL, Void?, Void?>() {

        override fun doInBackground(vararg urls: URL): Void? {
            try {
                val bufferedReader = BufferedReader(InputStreamReader(urls[0].openStream()))
                var inputLine = ""
                var count = 1
                while (bufferedReader.readLine()?.also { inputLine = it } != null) {
                    dataString += inputLine
                    if (count < 10 && inputLine.contains("LEC 00" + Integer.toString(count))) count++ else if (count < 100 && inputLine.contains("LEC 0" + Integer.toString(count))) count++ else if (inputLine.contains("LEC " + Integer.toString(count))) count++ else if (inputLine.contains("LEC 081")) count++
                }
                bufferedReader.close()
                dataDoc = Jsoup.parse(dataString)
                initCourse()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}