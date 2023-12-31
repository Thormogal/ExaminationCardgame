package com.example.examinationcardgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class CardFragment : Fragment() {
    private lateinit var fragmentView : ImageView
    private var card: Card? = null
    private var isPassButtonClicked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card, container, false)
        fragmentView = view.findViewById(R.id.fragmentView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView.setImageResource(Card.CARD_BACKGROUND.resourceId)
        card?.let { setCard(it) }
    } //if card != null then setCard will run.

    fun initCard(card: Card, isPassButtonClicked: Boolean = false) {
        this.card = card
        this.isPassButtonClicked = isPassButtonClicked
        if (this::fragmentView.isInitialized) {
            setCard(card)
        }
    }

    private fun setCard(card: Card) {
        if (!isPassButtonClicked) {
            fragmentView.setImageResource(card.resourceId)
        }
        fragmentView.tag = card
    }
}