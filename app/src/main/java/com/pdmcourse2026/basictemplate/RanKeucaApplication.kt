package com.pdmcourse2026.basictemplate

import android.app.Application
import com.pdmcourse2026.basictemplate.data.AppProvider

class RanKeucaApplication: Application(){
    val appProvider by lazy { AppProvider(this) }
}