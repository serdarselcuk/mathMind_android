package com.src.mathmind.Utils

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsIntegerInRangeMatcher(private val min: Int, private val max: Int) :
    TypeSafeMatcher<String>() {

    override fun matchesSafely(item: String?): Boolean {
        return item?.toIntOrNull() in min..max
    }

    override fun describeTo(description: Description?) {
        description?.appendText("an integer between $min and $max")
    }
}