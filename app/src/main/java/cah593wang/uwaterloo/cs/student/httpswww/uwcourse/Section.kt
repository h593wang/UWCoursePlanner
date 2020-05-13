package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

class Section : Serializable {
    var classNum = 0
    var className = ""
    var lecTitle = ""
    var campLoc: String = ""
    var enrollMax = 0
    var enrollCur = 0
    var times = ""
    var room: String = ""
    var inst: String = ""

    fun getStartHour(): Int {
        val startEnd = times.split("-")
        val startHourMin = startEnd[0].split(":")

        if (startHourMin[0].toInt() < 8) {
            return (startHourMin[0].toInt() + 12)
        }
        return startHourMin[0].toInt()
    }

    fun getStartMin(): Int {
        val startEnd = times.split("-")
        val startHourMin = startEnd[0].split(":")

        return startHourMin[1].toInt()
    }

    fun getEndHour(): Int {
        val startEnd = times.split("-")
        val startHourMin = startEnd[0].split(":")
        val endHourMin = startEnd[1].split(":")

        if (startHourMin[0].toInt() < 8 || endHourMin[0] < startHourMin[0]) {
            return (endHourMin[0].toInt() + 12)
        }
        return endHourMin[0].toInt()
    }

    fun getEndMin(): Int {
        val timeOfDay = times.replace(Regex("[a-zA-Z]"), "")
        val startEnd = timeOfDay.split("-")
        val endHourMin = startEnd[1].split(":")
        return endHourMin[1].toInt()
    }
}