package com.example.flappybird

enum class GameDifficulty(val velocity: Float, val gravity: Float) {
    Easy(1f, 1f),
    Medium(1.25f, 1.25f),
    Hard(2f, 1.75f),
}
