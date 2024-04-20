package com.src.mathmind.models

data class ScoreCalculus(
    var turn: Int = 0,
    var point: Double = 0.0,
    var position: MutableList<Boolean?> = MutableList(4) { null },
) {

    fun setGameEndPoint() {
        if (turn < 15) point += ((15 - turn) * 100)
        if (turn > 20) point -= (turn - 20)
    }

    /*
   true is indicating correct position
   false stands for wrong positioned digits
    */
    fun setPositionalPoint(positionalInfo: Int, correctPosition: Boolean) {

        if (correctPosition) {
            if (position[positionalInfo] != true) {
                point += (100 / turn)
                this.position[positionalInfo] = true
            }
        } else {
            if (position[positionalInfo] == null) {
                point += (50 / turn)
                this.position[positionalInfo] = false
            }
        }
    }

    fun increaseTurn(): ScoreCalculus {
        turn++
        return this
    }

    fun getPoint(): Int {
        return point.toInt()
    }
}