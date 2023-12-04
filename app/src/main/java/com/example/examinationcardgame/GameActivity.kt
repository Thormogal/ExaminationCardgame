package com.example.examinationcardgame

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import kotlin.math.min

class GameActivity : AppCompatActivity() {

    private var currentCardIndex = 0
    private lateinit var passButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var defendButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val borderFrame = findViewById<View>(R.id.border_frame)
        val edgeViews = listOf(R.id.topEdge, R.id.bottomEdge, R.id.leftEdge, R.id.rightEdge)
        passButton = findViewById(R.id.passButton)
        playButton = findViewById(R.id.playButton)
        defendButton = findViewById(R.id.defendButton)
        var isPassButtonClicked = false
        val cardBackgroundFragment = CardFragment()
        cardBackgroundFragment.initCard(Card.CARD_BACKGROUND)
        val allCards = Card.values().toMutableList()
        val playableCards = allCards.filter {
            it != Card.CARD_BACKGROUND && it != Card.CLUBS_JACKSAVED && it != Card.CLUBS_JACKSURVIVE
        }.shuffled().toMutableList()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.deckContainer, cardBackgroundFragment)
        transaction.commit()

        passButton.setOnClickListener {
            pressedButtonAppearance(passButton)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = true
                val card = playableCards[currentCardIndex]
                playableCards.removeAt(currentCardIndex)
                val randomIndex =
                    Random.nextInt(playableCards.size - currentCardIndex) + currentCardIndex
                playableCards.add(randomIndex, card)
                val fragment = CardFragment()
                fragment.initCard(Card.CARD_BACKGROUND, isPassButtonClicked)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.deckContainer, fragment)
                transaction.commit()

                isClubsJackDeathNear(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }

        playButton.setOnClickListener {
            pressedButtonAppearance(playButton)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = false
                val card = playableCards[currentCardIndex]
                val fragment = CardFragment()
                fragment.initCard(card, isPassButtonClicked)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.deckContainer, fragment)
                transaction.commit()
                if (card == Card.CLUBS_JACKDEATH) {
                    Toast.makeText(
                        this, "Jack caught you off guard. RIP (Ripped In Pieces)",
                        Toast.LENGTH_LONG
                    ).show()
                    inactivateButtons()
                }
                currentCardIndex++
                isClubsJackDeathNear(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }

        defendButton.setOnClickListener {
            pressedButtonAppearance(defendButton)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = false
                var card = playableCards[currentCardIndex]
                if (card == Card.CLUBS_JACKDEATH) {
                    card = Card.CLUBS_JACKSURVIVE
                    playableCards[currentCardIndex] = card
                }
                val fragment = CardFragment()
                fragment.initCard(card, isPassButtonClicked)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.deckContainer, fragment)
                transaction.commit()
                if (card == Card.CLUBS_JACKSURVIVE) {
                    Toast.makeText(
                        this,
                        "Jack is now unconscious and you survived. Congratulations!",
                        Toast.LENGTH_LONG
                    ).show()
                    inactivateButtons()
                }
                currentCardIndex++
                isClubsJackDeathNear(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }
    }
    fun isClubsJackDeathNear(cards: MutableList<Card>, currentIndex: Int, borderFrame: View, activity: Activity, edgeViews: List<Int>): Boolean {
        val upcomingCards = cards.subList(currentIndex, min(currentIndex + 5, cards.size))
        val isNear = Card.CLUBS_JACKDEATH in upcomingCards

        if (isNear) {
            val blinkAnimation = AnimationUtils.loadAnimation(activity, R.anim.blink_animation)
            borderFrame.startAnimation(blinkAnimation)
            edgeViews.forEach { activity.findViewById<View>(it).setBackgroundColor(Color.RED) }
        } else {
            borderFrame.clearAnimation()
            edgeViews.forEach { activity.findViewById<View>(it).setBackgroundColor(Color.TRANSPARENT) }
        }

        return isNear
    }

    fun inactivateButtons() {
        passButton.isEnabled = false
        playButton.isEnabled = false
        defendButton.isEnabled = false
    }

    fun pressedButtonAppearance(button: ImageView) {
        button.imageAlpha = 150
        button.postDelayed( {
            button.imageAlpha = 255}, 100)
    }
}