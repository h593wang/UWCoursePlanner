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
import java.lang.Exception
import java.net.URL

abstract class Course(private var dep: String?, private var courseCode: String, private var term: Int, val application: Application) {
    var dataString = ""

    abstract fun onCourseReturned()
    abstract fun onFailed(e: Exception)

    lateinit var cour: ArrayList<Section>

    init {
        //start by getting the html from the Uwaterloo registrar site
        GetInfoTask().execute((URL("http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess=$term&subject=$dep&cournum=$courseCode")))
    }

    //create the section instances based on the html
    private fun populateUW(dataDoc: Element) {
        cour = ArrayList()
        //get the tags that contain tables
        var elementArr = dataDoc.getElementsByTag("table")
        lateinit var eleTable :Element
        if (courseCode.contains("R") && elementArr.size >= 3) eleTable = elementArr[2]
        else if (!courseCode.contains(Regex("[a-zA-Z]")) && elementArr.size >= 2) eleTable = elementArr[1]
        else eleTable = elementArr.last()

        //get the rows from the table
        elementArr = eleTable.getElementsByTag("tr")
        //first row is always the column titles, not needed
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
                        10 -> cour[curIndex].times.add(normalizeTime(safeGet(field)))
                        11 -> cour[curIndex].room.add(safeGet(field))
                        12 -> cour[curIndex].inst = safeGet(field)
                    }
                    fieldIndex++
                }
                //rate my prof handling
                if (!(application as ApplicationBase).profRatings.containsKey(cour[curIndex].inst)) {
                    //create new livedata and rate my prof task if one doesnt exist yet for the given prof
                    val liveData = MutableLiveData<Pair<String, String>>()
                    liveData.postValue(Pair("LOADING", cour[curIndex].inst))
                    application.profRatings[cour[curIndex].inst] = liveData
                    //not loaded yet, so set a default value and load it
                    GetProfInfoTask().execute("https://www.ratemyprofessors.com/search.jsp?query=" + cour[curIndex].inst.replace(",", "+").trim(), cour[curIndex].inst)
                }
            } else if (ele.getElementsByTag("td").size == 13) { //handling additional data
                var fieldIndex = 0
                ele.getElementsByTag("td").forEach { field ->
                    when (fieldIndex) {
                        10 -> cour[curIndex].times.add(normalizeTime(safeGet(field)))
                        11 -> cour[curIndex].room.add( safeGet(field))
                    }
                    fieldIndex++
                }
            }
            //todo handle additional data
        }
    }

    //make it so Tuesday "T" isnt a substring of Thursday "Th"
    private fun normalizeTime(time: String): String {
        if (time == "TBA") return time
        var time = time.replace("T", "Tu")
        time = time.replace("Tuh", "Th")
        return time
    }

    private fun safeGet (field: Element): String {
        if (field.childNodeSize() == 0) return ""
        return field.childNode(0).toString().trim()
    }

    private fun safeGetInt (field: Element): Int{
        return safeGet(field).toIntOrNull() ?: 0
    }

    //task for getting UWaterloo course info html
    private inner class GetInfoTask : AsyncTask<URL, Void?, Void?>() {

        override fun doInBackground(vararg urls: URL): Void? {
            try {
                val bufferedReader = BufferedReader(InputStreamReader(urls[0].openStream()))
                var inputLine = ""
                while (bufferedReader.readLine()?.also { inputLine = it } != null) {
                    dataString += inputLine
                }
                bufferedReader.close()
                //make it into a tree structure class
                val dataDoc = Jsoup.parse(dataString)
                //create the section instances with the tree structure
                try {
                    populateUW(dataDoc)
                } catch (e: Exception) {
                    val exception = IOException("Unable to get course data from web results")
                    onFailed(exception)
                }
                onCourseReturned()
            } catch (e: IOException) {
                val exception = IOException("Unable to connect to the web")
                onFailed(exception)
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
                //get the search query results from rate my prof, well use this to get the professor id
                val profDoc = Jsoup.parse(dataString)

                //try and find a matching prof to get their id
                var profID = ""
                profDoc.getElementsByClass("listing PROFESSOR").forEach { it ->
                    if (it.toString().contains("University of Waterloo")) {
                        val info = it.getElementsByTag("a")[0].attributes()["href"].split("=")
                        if (info.size == 2) profID = info[1]
                    }
                }
                if (profID != "") {
                    //use the id to get the info from their specific page
                    val bufferedReader2 = BufferedReader(InputStreamReader(URL("https://www.ratemyprofessors.com/ShowRatings.jsp?tid=$profID").openStream()))
                    while (bufferedReader2.readLine()?.also { inputLine = it } != null) {
                        if (inputLine.contains("<div class=\"RatingValue__Numerator")) {
                            //if at any point we find the rating numerator class
                            val match = Regex("RatingValue__Numerator-qw8sqy-2 gxuTRq").find(inputLine)
                            //find where the class is in the returned string
                            var startIndex = match?.range?.start ?: 0
                            startIndex += "RatingValue__Numerator-qw8sqy-2 gxuTRq".length + 2
                            var rating = ""
                            //extract the rating from the string directly
                            while (inputLine[startIndex].isDigit() || inputLine[startIndex] == '.') {
                                rating += inputLine[startIndex]
                                startIndex++
                            }
                            //post the value to the livedata
                            (application as ApplicationBase).profRatings[inputs[1]]?.postValue(Pair<String, String>("$rating/5", inputs[1]))
                            break
                        }
                    }
                    bufferedReader2.close()
                } else {
                    //if anything goes wrong, post N/A
                    (application as ApplicationBase).profRatings[inputs[1]]?.postValue(Pair("N/A", inputs[1]))
                }
            } catch (e: IOException) {
                val exception = IOException("Unable to connect to the web")
                onFailed(exception)
            }
            return null
        }
    }
}