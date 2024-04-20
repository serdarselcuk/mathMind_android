package com.src.mathmind.models

data class GuessModel(
    val guessedNumber: Int,
    val feedBackData: FeedBackData
) {
    override fun equals(other: Any?): Boolean {
        return other is GuessModel && other.guessedNumber == this.guessedNumber
    }

    override fun toString(): String {
        return "GuessModel:" +
                "\n\tguessedNumber: $guessedNumber" +
                " \n\tplacedNumber: ${feedBackData.placedNumber}" +
                " \n\tnotPlacedNumber: ${feedBackData.nonPlacedNumber}"
    }

    override fun hashCode(): Int {
        var result = guessedNumber
        result = 31 * result + feedBackData.placedNumber
        result = 31 * result + feedBackData.nonPlacedNumber
        return result
    }
}
