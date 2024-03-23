package com.src.mathmind.models

data class GuessModel(
    val guessedNumber: Int,
    var placedNumber: Int,
    var notPlacedNumber: Int,
) {
    override fun equals(other: Any?): Boolean {
        return other is GuessModel && other.guessedNumber == this.guessedNumber
    }

    override fun toString(): String {
        return "GuessModel:" +
                "\n\tguessedNumber: $guessedNumber" +
                " \n\tplacedNumber: $placedNumber" +
                " \n\tnotPlacedNumber: $notPlacedNumber"
    }

    override fun hashCode(): Int {
        var result = guessedNumber
        result = 31 * result + placedNumber
        result = 31 * result + notPlacedNumber
        return result
    }
}
