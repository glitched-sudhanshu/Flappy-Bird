package com.example.flappybird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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
    private var gameState = 0
    private var gravity = 2f
    private var topHurdle: Texture? = null
    private var bottomHurdle: Texture? = null
    private var hurdleGap = 500f
    private var maximumOffset = 100f
    private var randomGenerator = java.util.Random(1000)
    private var tubeVelocity = 4f
    private var hurdleX = mutableListOf<Float>()
    private var tubeOffset = mutableListOf<Float>()
    private var noOfHurdles = 4
    private var distanceBetweenHurdles = 100f

    override fun create() {
        batch = SpriteBatch()
        background = Texture("background.png")
        topHurdle = Texture("toptube.png")
        bottomHurdle = Texture("bottomtube.png")
        birds.add(Texture("flappybirdup.png"))
        birds.add(Texture("flappybirddown.png"))
        hurdleGap = max(500f, birds[0].height.toFloat() + 20f)
        birdY = (Gdx.graphics.height / 2 - birds[0].height / 2).toFloat()
//        maximumOffset = Gdx.graphics.height / 2 - hurdleGap / 2 - 100
        distanceBetweenHurdles = Gdx.graphics.width * 3 / 4f
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
        if (gameState != 0) {
            if (Gdx.input.justTouched()) {
                velocity = -30f
            }
            if (birdY > 0 || velocity < 0) {
                // these two lines will make go bird fall faster when touched
                velocity += gravity
                birdY =
                    (birdY - velocity).coerceAtMost(Gdx.graphics.height.toFloat() - birds[0].height)
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
            }
        } else {
            if (Gdx.input.justTouched()) {
                gameState = 1
            }
        }
        flapTimer += Gdx.graphics.deltaTime // Update flap timer
        if (flapTimer >= flapDelay) {
            flapState = if (flapState == 0) 1 else 0
            flapTimer = 0f
        }

        batch?.draw(
            birds[flapState],
            ((Gdx.graphics.width / 2) - (birds[flapState].width.div(2))).toFloat(),
            birdY,
        )
        batch?.end()
    }
}
