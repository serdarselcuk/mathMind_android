package com.src.mathmind.utils

import com.src.mathmind.models.GuessModel

class GuessEngine(
) {
//    private var number = mutableListOf(-1, -1, -1, -1)
//    private var feedBacks: MutableMap<Int, Array<Array<Int>>> = mutableMapOf()
    val possibleNumbers = arrayListOf<Int>()

    // digit -> guessNumber, index
//    private val usedNumbersMap: MutableMap<Int, MutableList<Int>> = mutableMapOf()
//    private var possibilityMap: Map<Int, MutableList<Int>> = mapOf(
//        0 to mutableListOf(0, 1, 1, 1),
//        1 to mutableListOf(1, 1, 1, 1),
//        2 to mutableListOf(1, 1, 1, 1),
//        3 to mutableListOf(1, 1, 1, 1),
//        4 to mutableListOf(1, 1, 1, 1),
//        5 to mutableListOf(1, 1, 1, 1),
//        6 to mutableListOf(1, 1, 1, 1),
//        7 to mutableListOf(1, 1, 1, 1),
//        8 to mutableListOf(1, 1, 1, 1),
//        9 to mutableListOf(1, 1, 1, 1)
//    )

//    fun getPossibilityMap(): Map<Int, MutableList<Int>>{
//        return possibilityMap
//    }
//
//    fun getUsedNumbersMap(): MutableMap<Int, MutableList<Int>>{
//        return usedNumbersMap
//    }
//
//    fun getFeedbacks():MutableMap<Int, Array<Array<Int>>>{
//        return feedBacks
//    }
//
//    fun getNumber():MutableList<Int>{
//        return number
//    }

//    fun newFeedback(feedback: GuessModel) {
////        storing feedback
//        if (feedBacks.putIfAbsent(
//                feedback.guessedNumber,
//                arrayOf(
//                    arrayOf(feedback.placedNumber, feedback.notPlacedNumber)
//                )
//            ) == null
//        ) {
//            //put digit in to map to show where it has been used to find it easily when needed
//            Utility.numToArray(feedback.guessedNumber).forEachIndexed { index, digit ->
//                if (usedNumbersMap.putIfAbsent(
//                        digit,
//                        mutableListOf(feedback.guessedNumber, index)
//                    ) != null
//                ) {
//                    usedNumbersMap[digit]?.add(feedback.guessedNumber, index)
//                }
//            }
//            evaluate(feedback)
//        } else throw IllegalArgumentException("Repeated feedback: $feedback")
//    }


}

