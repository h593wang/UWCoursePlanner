package cah593wang.uwaterloo.cs.student.httpswww.uwcourse

import android.app.Application
import android.arch.lifecycle.MutableLiveData

class ApplicationBase: Application() {
                                                         //rating, prof name
    val profRatings = HashMap<String, MutableLiveData<Pair<String, String>>>()
}