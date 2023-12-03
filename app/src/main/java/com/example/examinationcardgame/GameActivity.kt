package com.example.examinationcardgame

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private var currentCardIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        val passButton: ImageView = findViewById(R.id.passButton)
        val playButton: ImageView = findViewById(R.id.playButton)
        val defendButton: ImageView = findViewById(R.id.defendButton)
        var isPassButtonClicked = false
        val cardBackgroundFragment = CardFragment()
        cardBackgroundFragment.initCard(Card.CARD_BACKGROUND)
        val allCards = Card.values().toMutableList()
        val playableCards = allCards.filter {
            it != Card.CARD_BACKGROUND &&
                    it != Card.CLUBS_JACKSAVED && it != Card.CLUBS_JACKSURVIVE
        }.shuffled().toMutableList()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.deckContainer, cardBackgroundFragment)
        transaction.commit()

        val firstFragment = CardFragment()
        firstFragment.initCard(Card.CARD_BACKGROUND)


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

        passButton.setOnClickListener {
            pressedButtonAppearance(passButton)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = true
                val card = playableCards[currentCardIndex]
                playableCards.removeAt(currentCardIndex)
                val RandomIndex =
                    Random.nextInt(playableCards.size - currentCardIndex) + currentCardIndex
                playableCards.add(RandomIndex, card)

                val fragment = CardFragment()
                fragment.initCard(Card.CARD_BACKGROUND, isPassButtonClicked)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.deckContainer, fragment)
                transaction.commit()
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
            }
        }
    }
}