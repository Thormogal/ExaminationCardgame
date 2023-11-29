package com.example.examinationcardgame

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var currentCardIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val playButton: ImageView = findViewById(R.id.playButton)
        val allCards = Card.values().toList()
        val allFragments = mutableListOf<CardFragment>()

        for (card in allCards) {
            val fragment = CardFragment()
            fragment.initCard(card)
            allFragments.add(fragment)
        }

        for (i in allFragments.indices) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.deckContainer, allFragments[i])
            if (i != 0) {
                transaction.hide(allFragments[i])
            }
            transaction.commit()
        }

        playButton.setOnClickListener {
            if (currentCardIndex < allFragments.size - 1) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(allFragments[currentCardIndex])
                currentCardIndex++
                transaction.show(allFragments[currentCardIndex])
                transaction.commit()
            }
        }
    }
}