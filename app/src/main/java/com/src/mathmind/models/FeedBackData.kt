package com.src.mathmind.models

data class FeedBackData(
    var placedNumber: Int,
    var nonPlacedNumber: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is FeedBackData
                && this.placedNumber == other.placedNumber
                && this.nonPlacedNumber == other.nonPlacedNumber
    }

    override fun hashCode(): Int {
        var result = placedNumber
        result = 31 * result + nonPlacedNumber
        return result
    }
}