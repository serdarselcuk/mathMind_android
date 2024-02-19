package com.example.mathmind.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mathmind.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class Utility {

    companion object {
        fun arrayToNum(array: IntArray): Int {
            return "${array[0]}${array[1]}${array[2]}${array[3]}".toInt()
        }


        suspend fun highlightElement(
            obj: View,
            color: Int= R.color.red_highlight,
            context: Context?,
            flash:Boolean=true,
            repeatCount:Int=5,
            timeOut:Long=50
        ){
            val highlightingColor: Int? = context?.let { ContextCompat.getColor(it, color) }
            val background = obj.background
            var backgroundColor = 0
            if (background is ColorDrawable) {
                // Get the color of the ColorDrawable
                backgroundColor = background.color
                println("Background color: $color")
            } else {
                println("View background is not a ColorDrawable")
            }
            println("element found to highlight: ${obj.id}")
            var _repeatCount = 1
            withContext(Dispatchers.Main) {
                //if background color not found flushing is not possible
                if(flash && backgroundColor!=0) _repeatCount = repeatCount
                repeat(_repeatCount){
                    if (highlightingColor is Int) obj.setBackgroundColor(highlightingColor)
                    if(backgroundColor!=0){
                        delay(timeOut)
                        obj.setBackgroundColor(backgroundColor)
                        delay(timeOut)
                    }
                }
            }
        }
    }
}