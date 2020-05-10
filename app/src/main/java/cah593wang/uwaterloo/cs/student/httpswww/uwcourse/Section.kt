package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sectionInfo")
class Section : Serializable {
    @PrimaryKey
    var classNum = 0
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

    constructor() {}
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

}