package com.example.flappybird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

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

    override fun create() {
        batch = SpriteBatch()
        background = Texture("background.png")
        birds.add(Texture("flappybirdup.png"))
        birds.add(Texture("flappybirddown.png"))
        birdY = (Gdx.graphics.height / 2 - birds[0].height / 2).toFloat()
    }

    override fun render() {
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

        batch?.begin()
        batch?.draw(
            background,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat(),
        )
        batch?.draw(
            birds[flapState],
            ((Gdx.graphics.width / 2) - (birds[flapState].width.div(2))).toFloat(),
            birdY,
        )
        batch?.end()
    }
}
