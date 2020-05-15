package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.arch.lifecycle.LiveData
import java.io.Serializable

class Section : Serializable {
    var classNum = 0
    var className = ""
    var lecTitle = ""
    var campLoc: String = ""
    var enrollMax = 0
    var enrollCur = 0
    var times = ArrayList<String>()
    var room = ArrayList<String>()
    var inst: String = ""

    fun getTime(index: Int): String {
        if (times.size >= index + 1) return times[index]
        return ""
    }

    fun getRoom(index: Int): String {
        if (room.size >= index + 1) return room[index]
        return ""
    }

    fun getStartHour(index: Int): Int {
        val startEnd = times[index].split("-")
        val startHourMin = startEnd[0].split(":")

        if (startHourMin[0].toInt() < 8) {
            return (startHourMin[0].toInt() + 12)
        }
        return startHourMin[0].toInt()
    }

    fun getStartMin(index: Int): Int {
        val startEnd = times[index].split("-")
        val startHourMin = startEnd[0].split(":")

        return startHourMin[1].toInt()
    }

    fun getEndHour(index: Int): Int {
        val startEnd = times[index].split("-")
        val startHourMin = startEnd[0].split(":")
        val endHourMin = startEnd[1].split(":")

        if (startHourMin[0].toInt() < 8 || endHourMin[0] < startHourMin[0]) {
            return (endHourMin[0].toInt() + 12)
        }
        return endHourMin[0].toInt()
    }

    fun getEndMin(index: Int): Int {
        val timeOfDay = times[index].replace(Regex("[a-zA-Z]"), "")
        val startEnd = timeOfDay.split("-")
        val endHourMin = startEnd[1].split(":")
        return endHourMin[1].toInt()
    }
}