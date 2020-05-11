package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sectionInfo")
class Section : Serializable {
    @PrimaryKey
    var classNum = 0
    var className = ""
    var lecTitle = ""
    var campLoc: String? = null
    var enrollMax = 0
    var enrollCur = 0
    var times = ""
    var room: String = ""
    var room2: String = ""
    var inst: String = ""
    var instQual: String = ""
    var wta: String = ""
    var lod: String = ""

    constructor()
    constructor(classNum: Int, lecNum: String?, campLoc: String?, enrollMax: Int, enrollCur: Int, times: String?, room: String?, room2: String?, inst: String?, instQual: String?, wta: String?, lod: String?, hotness: Boolean) {
        this.classNum = classNum
        this.lecTitle = lecNum ?: ""
        this.campLoc = campLoc
        this.enrollMax = enrollMax
        this.enrollCur = enrollCur
        this.times = times ?: ""
        this.room = room ?: ""
        this.room2 = room2 ?: ""
        this.inst = inst ?: ""
        this.instQual = instQual ?: ""
        this.wta = wta ?: ""
        this.lod = lod ?: ""
        isHotness = hotness
    }

    var isHotness = false

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