package com.isanechek.beardycast.Utils

import android.os.Build

/**
 * Created by isanechek on 27.07.16.
 */
open class Util() {
    inline fun isLollipopPlus(code: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            code()
        }
    }
}