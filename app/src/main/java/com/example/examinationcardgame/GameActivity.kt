package com.example.examinationcardgame

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
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

        passButton = findViewById(R.id.passButton)
        playButton = findViewById(R.id.playButton)
        defendButton = findViewById(R.id.defendButton)
        val specialCardView: ImageView = findViewById(R.id.specialCardView)
        val borderFrame = findViewById<View>(R.id.border_frame)
        val edgeViews = listOf(R.id.topEdge, R.id.bottomEdge, R.id.leftEdge, R.id.rightEdge)
        var isPassButtonClicked = false
        var clubsJackDeathCount = 1
        val allCards = Card.values().toMutableList()
        val playableCards = allCards.filterNot {
            it in listOf(Card.CARD_BACKGROUND, Card.CLUBS_JACKSAVED, Card.CLUBS_JACKSURVIVE)
        }.shuffled().toMutableList()
        val cardBackgroundFragment = CardFragment()
        cardBackgroundFragment.initCard(Card.CARD_BACKGROUND)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.deckContainer, cardBackgroundFragment)
        transaction.commit()

        fun jackCantBeFirstCard(playableCards: MutableList<Card>, currentCardIndex: Int) {
            if (currentCardIndex == 0) {
                if (playableCards[0] == Card.CLUBS_JACKDEATH) {
                    val randomPlace = (1 until playableCards.size).random()
                    playableCards[0] = playableCards[randomPlace]
                    playableCards[randomPlace] = Card.CLUBS_JACKDEATH
                }
            }
        }

        fun animateSpecialCard() {
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels

            specialCardView.animate()
                .scaleX(2.8f)
                .scaleY(2.6f)
                .translationX(screenWidth * -0.39f)
                .translationY(screenHeight * 0.37f)
                .setDuration(1000)
                .withEndAction {
                    specialCardView.visibility = View.GONE
                }
                .start()
        }

        fun addSpecialCard(playableCards: MutableList<Card>) {
            val randomIndex = Random.nextInt(playableCards.size)
            playableCards.add(randomIndex, Card.CLUBS_JACKDEATH)
            animateSpecialCard()
        }

        passButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(passButton)
            jackCantBeFirstCard(playableCards, currentCardIndex)
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

                jackIsComing(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }

        playButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(playButton)
            jackCantBeFirstCard(playableCards, currentCardIndex)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = false
                val card = playableCards[currentCardIndex]
                if (card == Card.CLUBS_4) {
                    addSpecialCard(playableCards)
                    clubsJackDeathCount++
                }

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
                jackIsComing(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }

        defendButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(defendButton)
            jackCantBeFirstCard(playableCards, currentCardIndex)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = false
                var card = playableCards[currentCardIndex]
                if (card == Card.CLUBS_JACKDEATH) {
                    card = Card.CLUBS_JACKSURVIVE
                    playableCards[currentCardIndex] = card
                    clubsJackDeathCount--
                }
                val fragment = CardFragment()
                fragment.initCard(card, isPassButtonClicked)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.deckContainer, fragment)
                transaction.commit()
                if (card == Card.CLUBS_JACKSURVIVE && clubsJackDeathCount == 0) {
                    Toast.makeText(
                        this,
                        "Jack is now unconscious and you survived. Congratulations!", Toast.LENGTH_LONG).show()
                    inactivateButtons()
                } else {
                    if (card == Card.CLUBS_JACKSURVIVE && clubsJackDeathCount == 1)
                        Toast.makeText(this,
                            "Good work! One more Jack to survive against", Toast.LENGTH_LONG).show()
                }
                currentCardIndex++
                jackIsComing(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }
    }
    private var jackWarning = false


    private fun jackIsComing(cards: MutableList<Card>, currentIndex: Int, borderFrame: View, activity: Activity, edgeViews: List<Int>): Boolean {
        val upcomingCards = cards.subList(currentIndex, min(currentIndex + 5, cards.size)).toMutableList()
        upcomingCards.shuffle()
        val jackIsClose = Card.CLUBS_JACKDEATH in upcomingCards

        if (jackIsClose && !jackWarning) {
            val randomPlace = (currentIndex..min(currentIndex + 4, cards.size - 1)).random()
            val jackIndex = cards.indexOf(Card.CLUBS_JACKDEATH)
            if (jackIndex != -1) {
                cards[jackIndex] = cards[randomPlace]
                cards[randomPlace] = Card.CLUBS_JACKDEATH
            }

            val dangerAnimation = AnimationUtils.loadAnimation(activity, R.anim.danger_animation)
            borderFrame.startAnimation(dangerAnimation)
            edgeViews.forEach { activity.findViewById<View>(it).setBackgroundColor(Color.RED) }

            jackWarning = true
        } else if (!jackIsClose) {
            borderFrame.clearAnimation()
            edgeViews.forEach { activity.findViewById<View>(it).setBackgroundColor(Color.TRANSPARENT) }
            jackWarning = false
        }

        return jackIsClose
    }

    private fun inactivateButtons() {
        passButton.isEnabled = false
        playButton.isEnabled = false
        defendButton.isEnabled = false
    }
}