package com.armutyus.ninova.constants

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.armutyus.ninova.R

class Util {

    companion object {
        fun progressDrawable(context: Context): CircularProgressDrawable {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.backgroundColor = R.color.primaryColor
            circularProgressDrawable.start()

            return circularProgressDrawable
        }
    }

}