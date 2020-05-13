package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

abstract class Course(private var dep: String?, private var courseCode: Int, private var term: Int, val application: Application) {
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
            //if this is the start of a new section
            if (ele.childNode(0).childNode(0).toString().trim().matches(Regex("[0-9]+"))) {
                curIndex++
                cour.add(Section())
                cour[curIndex].className = "$dep $courseCode"
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
                //rate my prof handling
                if (!(application as ApplicationBase).profRatings.containsKey(cour[curIndex].inst)) {
                    val liveData = MutableLiveData<Pair<String, String>>()
                    liveData.postValue(Pair("LOADING", cour[curIndex].inst))
                    application.profRatings[cour[curIndex].inst] = liveData
                    //not loaded yet, so set a default value and load it
                    GetProfInfoTask().execute("https://www.ratemyprofessors.com/search.jsp?query=" + cour[curIndex].inst.replace(",", "+").trim(), cour[curIndex].inst)
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
                while (bufferedReader.readLine()?.also { inputLine = it } != null) {
                    dataString += inputLine
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

    private inner class GetProfInfoTask : AsyncTask<String, Void?, Void?>() {
        override fun doInBackground(vararg inputs: String): Void? {
            try {
                val bufferedReader = BufferedReader(InputStreamReader(URL(inputs[0]).openStream()))
                var inputLine = ""
                dataString = ""
                while (bufferedReader.readLine()?.also { inputLine = it } != null) {
                    dataString += inputLine
                }
                bufferedReader.close()
                val profDoc = Jsoup.parse(dataString)

                var profID = ""
                profDoc.getElementsByClass("listing PROFESSOR").forEach { it ->
                    if (it.toString().contains("University of Waterloo")) {
                        val info = it.getElementsByTag("a")[0].attributes()["href"].split("=")
                        if (info.size == 2) profID = info[1]
                    }
                }
                if (profID != "") {
                    val bufferedReader2 = BufferedReader(InputStreamReader(URL("https://www.ratemyprofessors.com/ShowRatings.jsp?tid=$profID").openStream()))
                    while (bufferedReader2.readLine()?.also { inputLine = it } != null) {
                        if (inputLine.contains("<div class=\"RatingValue__Numerator")) {
                            val match = Regex("RatingValue__Numerator-qw8sqy-2 gxuTRq").find(inputLine)
                            var startIndex = match?.range?.start ?: 0
                            startIndex += "RatingValue__Numerator-qw8sqy-2 gxuTRq".length + 2
                            var rating = ""
                            while (true) {
                                if (inputLine[startIndex].isDigit() || inputLine[startIndex] == '.') {
                                    rating += inputLine[startIndex]
                                    startIndex++
                                } else
                                break
                            }
                            (application as ApplicationBase).profRatings[inputs[1]]?.postValue(Pair<String, String>("$rating/5", inputs[1]))
                            break
                        }
                    }
                    bufferedReader2.close()
                } else {
                    (application as ApplicationBase).profRatings[inputs[1]]?.postValue(Pair("N/A", inputs[1]))
                }
            } catch (e: IOException) {
                (application as ApplicationBase).profRatings[inputs[1]]?.postValue(Pair("N/A", inputs[1]))
                e.printStackTrace()
            }
            return null
        }
    }
}