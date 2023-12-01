package com.example.examinationcardgame

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var currentCardIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val playButton: ImageView = findViewById(R.id.playButton)
        val allCards = Card.values().toList()
        val allFragments = mutableListOf<CardFragment>()
        val backCardFragment = CardFragment()
        backCardFragment.initCard(Card.CARD_BACK)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.deckContainer, backCardFragment)
        transaction.commit()

        val firstFragment = CardFragment()

        firstFragment.initCard(Card.CARD_BACK)


        for (i in allCards.indices) {
            val fragment = CardFragment()
            fragment.initCard(Card.CARD_BACK)
            allFragments.add(fragment)
        }


        val cardsWithoutBack = allCards.filter{ it != Card.CARD_BACK &&
                it != Card.CLUBS_JACKSAVED && it != Card.CLUBS_JACKSURVIVE }.shuffled()

        playButton.setOnClickListener {
            if (currentCardIndex < cardsWithoutBack.size) {
                val card = cardsWithoutBack[currentCardIndex]

                val fragment = CardFragment()
                fragment.initCard(card)

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.deckContainer, fragment)
                transaction.commit()

                if (card == Card.CLUBS_JACKDEATH) {
                    Toast.makeText(this, "Ahw shit, Jack got you. You lost the game", Toast.LENGTH_LONG).show()
                    playButton.isEnabled = false
                }
                currentCardIndex++
            }
        }
    }
}