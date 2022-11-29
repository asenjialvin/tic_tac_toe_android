package com.donaboyev.tictactoe

enum class Mode {
    EASY,
    MEDIUM,
    HARD,
    TWO_PLAYERS;

    companion object {
        fun fromInteger(number: Int): Mode? {
            return when (number) {
                0 -> EASY
                1 -> MEDIUM
                2 -> HARD
                3 -> TWO_PLAYERS
                else -> null
            }
        }
    }
}