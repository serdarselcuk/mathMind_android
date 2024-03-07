package com.src.mathmind.models
data class GuessModel(
    val guessedNumber: Int,
    var placedNumber: Int,
    var notPlacedNumber: Int
){
    override fun equals(other: Any?): Boolean {
        return other is GuessModel && other.guessedNumber == this.guessedNumber
    }
}
