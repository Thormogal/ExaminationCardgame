package com.example.examinationcardgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class CardFragment : Fragment() {
    private lateinit var defaultImage : ImageView
    private var card: Card? = null

    fun initCard(card: Card) {
        this.card = card
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_game, container, false)
        defaultImage = view.findViewById(R.id.deckImageView)
        card?.let {
            setCard(it)
        }
        return view
    }

    fun setCard(card: Card) {
        defaultImage.setImageResource(card.resourceId)
        defaultImage.tag = card
    }

    fun showBack() {
        setCard(Card.CARD_BACK)
    }

    fun showFront() {
        val originalCard = defaultImage.tag as? Card
        if (originalCard != null) {
            setCard(originalCard)
        }

    }
}