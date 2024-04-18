package com.src.mathmind.models

import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

data class ScoreModel(

    val userName: String,
    val point: Int,
    val date: LocalDate,
) : Comparable<ScoreModel> {


    fun calculateDaysPassed(): String {
        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        // Calculate the difference in milliseconds
        val differenceMillis = System.currentTimeMillis() - instant.toEpochMilli()
        // Convert milliseconds to days
        val days = TimeUnit.MILLISECONDS.toDays(differenceMillis)
        return days.toString().padStart(DAYS_LENGTH, SPACE)
    }

    private fun getUserNamePrinted(): String {
        return if (userName.length > USER_NAME_LENGTH) userName.substring(0, USER_NAME_LENGTH)
        else if (userName.length < USER_NAME_LENGTH) userName + SPACE.toString()
            .repeat(USER_NAME_LENGTH - userName.length)
        else userName
    }


    private fun getScorePrinted(): String {
        return point.toString().padStart(SCORE_LENGTH, SPACE)
    }

    override fun compareTo(other: ScoreModel): Int {
        return this.point - other.point
    }

    override fun toString(): String {
        return "${getUserNamePrinted()}\t|\t${calculateDaysPassed()}\t|${getScorePrinted()}"
    }

    companion object {
        private const val SPACE = ' '
        private const val USER_NAME_LENGTH = 12
        private const val SCORE_LENGTH = 5
        private const val DAYS_LENGTH = 5
    }
}