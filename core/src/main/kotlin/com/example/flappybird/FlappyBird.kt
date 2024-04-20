package com.example.flappybird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import java.lang.Float.max

class FlappyBird : ApplicationAdapter() {
    // cant instantiate here, coz we cant call it's constructor before create()
    private var batch: SpriteBatch? = null
    private var background: Texture? = null
    private val birds = mutableListOf<Texture>()
    private var flapState = 0
    private var flapTimer = 0f
    private val flapDelay = 0.1f
    private var birdY = 0f
    private var velocity = 0f
    private lateinit var playerHitBox: Circle
    private var gameState = 0
    private var gravity = 1f
    private lateinit var topHurdle: Texture
    private lateinit var bottomHurdle: Texture
    private lateinit var gameOverTexture: Texture
    private var hurdleGap = 500f
    private var randomGenerator = java.util.Random(1000)
    private var tubeVelocity = 4f
    private val hurdleX = mutableListOf<Float>()
    private val tubeOffset = mutableListOf<Float>()
    private val topHurdleBox = mutableListOf<Rectangle>()
    private val bottomHurdleBox = mutableListOf<Rectangle>()
    private var noOfHurdles = 4
    private var distanceBetweenHurdles = 100f
    private var score: Int = 0
    private var scoringHurdle: Int = 0
    private lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        background = Texture("background.png")
        topHurdle = Texture("toptube.png")
        bottomHurdle = Texture("bottomtube.png")
        birds.add(Texture("flappybirdup.png"))
        birds.add(Texture("flappybirddown.png"))
        gameOverTexture = Texture("gameover.png")
        hurdleGap = max(500f, birds[0].height.toFloat() + 20f)
        playerHitBox = Circle()
        font = BitmapFont()
        font.setColor(Color.WHITE)
        font.data.scale(12f)
        distanceBetweenHurdles = Gdx.graphics.width * 3 / 4f
        startGame()
    }

    private fun startGame() {
        birdY = (Gdx.graphics.height / 2 - birds[0].height / 2).toFloat()
        for (i in 0 until noOfHurdles) {
            tubeOffset.add((randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.height - hurdleGap - 200))
            hurdleX.add(
                (
                    Gdx.graphics.width / 2 - (
                        topHurdle?.width
                            ?: 1
                    ) / 2
                ).toFloat() + Gdx.graphics.width + i * distanceBetweenHurdles,
            )
            topHurdleBox.add(Rectangle())
            bottomHurdleBox.add(Rectangle())
        }
    }

    override fun render() {
        batch?.begin()
        batch?.draw(
            background,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat(),
        )
        if (gameState == 1) {
            if (hurdleX[scoringHurdle] < Gdx.graphics.width / 2) {
                score++
                if (scoringHurdle < noOfHurdles - 1) {
                    scoringHurdle++
                } else {
                    scoringHurdle = 0
                }
            }
            if (Gdx.input.justTouched()) {
                velocity = -20f
            }

            for (i in 0 until noOfHurdles) {
                if (hurdleX[i] < -(topHurdle?.width ?: 1)) {
                    hurdleX[i] += noOfHurdles * distanceBetweenHurdles
                    tubeOffset[i] =
                        (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.height - hurdleGap - 200)
                } else {
                    hurdleX[i] = hurdleX[i] - tubeVelocity
                }
                batch?.draw(
                    topHurdle,
                    hurdleX[i],
                    Gdx.graphics.height / 2 + hurdleGap / 2 + tubeOffset[i],
                )
                batch?.draw(
                    bottomHurdle,
                    hurdleX[i],
                    Gdx.graphics.height / 2 - hurdleGap / 2 - (
                        bottomHurdle?.height
                            ?: 1
                    ) + tubeOffset[i],
                )
                topHurdleBox[i] =
                    Rectangle(
                        hurdleX[i],
                        Gdx.graphics.height / 2 + hurdleGap / 2 + tubeOffset[i],
                        topHurdle?.width?.toFloat() ?: 1f,
                        topHurdle?.height?.toFloat() ?: 1f,
                    )
                bottomHurdleBox[i] =
                    Rectangle(
                        hurdleX[i],
                        Gdx.graphics.height / 2 - hurdleGap / 2 - (
                            bottomHurdle?.height
                                ?: 1
                        ) + tubeOffset[i],
                        topHurdle?.width?.toFloat() ?: 1f,
                        topHurdle?.height?.toFloat() ?: 1f,
                    )
            }
            if (birdY > 0) {
                // these two lines will make go bird fall faster when touched
                velocity += gravity
                birdY =
                    (birdY - velocity).coerceAtMost(Gdx.graphics.height.toFloat() - birds[0].height)
            } else {
                gameState = 2
            }
        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1
            }
        } else if (gameState == 2) {
            batch?.draw(
                gameOverTexture,
                Gdx.graphics.width / 2f - gameOverTexture.width / 2,
                Gdx.graphics.height / 2f - gameOverTexture.height / 2,
            )
            if (Gdx.input.justTouched()) {
                gameState = 1
                hurdleX.clear()
                tubeOffset.clear()
                topHurdleBox.clear()
                bottomHurdleBox.clear()
                startGame()
                score = 0
                hurdleX
                scoringHurdle = 0
                velocity = 0f
            }
        }
        flapTimer += Gdx.graphics.deltaTime
        if (flapTimer >= flapDelay) {
            flapState = if (flapState == 0) 1 else 0
            flapTimer = 0f
        }

        batch?.draw(
            birds[flapState],
            ((Gdx.graphics.width / 2) - (birds[flapState].width.div(2))).toFloat(),
            birdY,
        )
        playerHitBox.set(Gdx.graphics.width / 2f, birdY + birds[0].height / 2, birds[0].width / 2f)
        font.draw(batch, score.toString(), 100f, 200f)
        for (i in 0 until noOfHurdles) {
            if (Intersector.overlaps(playerHitBox, topHurdleBox[i]) ||
                Intersector.overlaps(
                    playerHitBox,
                    bottomHurdleBox[i],
                )
            ) {
                gameState = 2
            }
        }
        batch?.end()
    }
}
