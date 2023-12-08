package com.example.examinationcardgame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import kotlin.math.min

class GameActivity : AppCompatActivity() {

    private lateinit var passButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var defendButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var specialCardView: ImageView
    private lateinit var borderFrame: View
    private lateinit var edgeViews: List<Int>
    private lateinit var playableCards: MutableList<Card>
    private var hasJackShuffled = false
    private var jackWarning = false
    private var isPassButtonClicked = false
    private var currentCardIndex = 0
    private var clubsJackDeathCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initButtons()
        initCards()


        passButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(passButton)
            jackCantBeFirstCard(playableCards, currentCardIndex)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = true
                shuffleCardsFromStart(playableCards, currentCardIndex)
                createAndReplaceFragment(Card.CARD_BACKGROUND, isPassButtonClicked)
                jackIsPresent(playableCards, currentCardIndex, borderFrame, this, edgeViews)
            }
        }

        playButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(playButton)
            jackCantBeFirstCard(playableCards, currentCardIndex)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = false
                val card = playableCards[currentCardIndex]
                addSpecialCardToPlayableCards(playableCards, card)
                createAndReplaceFragment(card, isPassButtonClicked)
                jackWinOrJackLose(playableCards, currentCardIndex, this, card)
                jackIsPresent(playableCards, currentCardIndex, borderFrame, this, edgeViews)
                currentCardIndex++
            }
        }

        defendButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(defendButton)
            jackCantBeFirstCard(playableCards, currentCardIndex)
            if (currentCardIndex < playableCards.size) {
                isPassButtonClicked = false
                swapJackDeathToJackSurvive(playableCards, currentCardIndex, clubsJackDeathCount)
                val card = playableCards[currentCardIndex]
                createAndReplaceFragment(card, isPassButtonClicked)
                defendedAgainstJack(card, clubsJackDeathCount, this)
                jackIsPresent(playableCards, currentCardIndex, borderFrame, this, edgeViews)
                currentCardIndex++
            }
        }

        settingsButton.setOnClickListener {
            val popupMenu = PopupMenu(this, settingsButton)
            popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_rules -> {
                        val intent = Intent(this, RulesActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        startActivity(intent)
                    }

                    R.id.menu_main -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        startActivity(intent)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun initButtons() {
        settingsButton = findViewById(R.id.settingsButton)
        passButton = findViewById(R.id.passButton)
        playButton = findViewById(R.id.playButton)
        defendButton = findViewById(R.id.defendButton)
        specialCardView = findViewById(R.id.specialCardView)
        borderFrame = findViewById(R.id.border_frame)
        edgeViews = listOf(R.id.topEdge, R.id.bottomEdge, R.id.leftEdge, R.id.rightEdge)
        isPassButtonClicked = false
    }

    private fun inactivateButtons() {
        passButton.isEnabled = false
        playButton.isEnabled = false
        defendButton.isEnabled = false
    }

    private fun initCards() {
        val allCards = Card.values().toMutableList()
        playableCards = allCards.filterNot {
            it in listOf(
                Card.CARD_BACKGROUND, Card.CLUBS_JACKSAVED, Card.CLUBS_JACKSURVIVE,
                Card.SPADES_KING, Card.HEARTS_QUEEN, Card.HEARTS_KING, Card.CLUBS_KING
            )
        }.shuffled().toMutableList()
    }

    private fun createAndReplaceFragment(card: Card, isPassButtonClicked: Boolean) {
        val fragment = CardFragment()
        fragment.initCard(card, isPassButtonClicked)
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: CardFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.deckContainer, fragment)
        transaction.commit()
    }

    private fun shuffleCardsFromStart(playableCards: MutableList<Card>, currentCardIndex: Int) {
        val card = playableCards[currentCardIndex]
        playableCards.removeAt(currentCardIndex)
        val randomIndex = Random.nextInt(playableCards.size - currentCardIndex) + currentCardIndex
        playableCards.add(randomIndex, card)
    }

    private fun jackCantBeFirstCard(playableCards: MutableList<Card>, currentCardIndex: Int) {
        if (currentCardIndex == 0) {
            if (playableCards[0] == Card.CLUBS_JACKDEATH) {
                val randomPlace = (1 until playableCards.size).random()
                playableCards[0] = playableCards[randomPlace]
                playableCards[randomPlace] = Card.CLUBS_JACKDEATH
            }
        }
    }

    private fun checkIfJackIsInNextFive(cards: MutableList<Card>, currentIndex: Int): Boolean {
        val upcomingCards =
            cards.subList(currentIndex, min(currentIndex + 5, cards.size)).toMutableList()
        val isJackInNextFive = Card.CLUBS_JACKDEATH in upcomingCards
        if (!isJackInNextFive) {
            hasJackShuffled = false
        }
        return isJackInNextFive
    }

    private fun jackShufflePlaceInNextFive(cards: MutableList<Card>, currentIndex: Int) {
        if (!hasJackShuffled && checkIfJackIsInNextFive(cards, currentIndex)) {
            val randomPlace = Random.nextInt(currentIndex, min(currentIndex + 5, cards.size))
            val jackIndex = cards.indexOf(Card.CLUBS_JACKDEATH)
            if (jackIndex != -1) {
                cards[jackIndex] = cards[randomPlace]
                cards[randomPlace] = Card.CLUBS_JACKDEATH
                hasJackShuffled = true
            }
        }
    }

    private fun jackIsCloseDangerAnimationPreset(
        jackIsClose: Boolean,
        edgeViews: List<Int>,
        activity: Activity
    ) {
        if (jackIsClose && !jackWarning) {
            edgeViews.forEach { activity.findViewById<View>(it).setBackgroundColor(Color.RED) }
        } else if (!jackIsClose) {
            edgeViews.forEach {
                activity.findViewById<View>(it).setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun initiateJackIsCloseDangerAnimation(
        jackIsClose: Boolean,
        borderFrame: View,
        activity: Activity
    ) {
        if (jackIsClose && !jackWarning) {
            val dangerAnimation =
                AnimationUtils.loadAnimation(activity, R.anim.danger_animation)
            borderFrame.startAnimation(dangerAnimation)
            jackWarning = true
        } else if (!jackIsClose) {
            borderFrame.clearAnimation()
            jackWarning = false
        }
    }

    private fun animateSpecialCard() {
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

    private fun addSpecialCardToPlayableCards(
        playableCards: MutableList<Card>, card: Card
    ) {
        if (card == Card.CLUBS_4) {
            val randomIndex = Random.nextInt(playableCards.size)
            playableCards.add(randomIndex, Card.CLUBS_JACKDEATH)
            animateSpecialCard()
            clubsJackDeathCount++
        }
    }

    private fun jackIsPresent(
        cards: MutableList<Card>,
        currentIndex: Int,
        borderFrame: View,
        activity: Activity,
        edgeViews: List<Int>
    ) {
        val jackIsClose = checkIfJackIsInNextFive(cards, currentIndex)
        jackIsCloseDangerAnimationPreset(jackIsClose, edgeViews, activity)
        initiateJackIsCloseDangerAnimation(jackIsClose, borderFrame, activity)
        if (jackIsClose) {
            jackShufflePlaceInNextFive(cards, currentIndex)
        }
    }

    private fun defendedAgainstJack(card: Card, clubsJackDeathCount: Int, context: Context) {
        if (card == Card.CLUBS_JACKSURVIVE && clubsJackDeathCount == 1) {
            Toast.makeText(
                context,
                "Jack is now unconscious and you survived. Congratulations!",
                Toast.LENGTH_LONG
            ).show()
            inactivateButtons()
        }
        if (card == Card.CLUBS_JACKSURVIVE && clubsJackDeathCount == 2) {
            var clubsJackDeathCounter = clubsJackDeathCount
            clubsJackDeathCounter--
            Toast.makeText(
                this,
                "Good work! One more Jack to survive against", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun jackWinOrJackLose(
        playableCards: MutableList<Card>,
        currentCardIndex: Int,
        context: Context,
        card: Card
    ) {
        if (card == Card.CLUBS_JACKDEATH) {
            val message = if (currentCardIndex == playableCards.size - 1) {
                "You knew Jack was coming. You knocked him unconscious. You win!"
            } else {
                "Jack caught you off guard. RIP (Ripped In Pieces)"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            inactivateButtons()
        }
    }

    private fun swapJackDeathToJackSurvive(
        playableCards: MutableList<Card>,
        currentCardIndex: Int,
        clubsJackDeathCount: Int
    ): Int {
        var card = playableCards[currentCardIndex]
        var clubsJackDeathCounter = clubsJackDeathCount
        if (card == Card.CLUBS_JACKDEATH) {
            card = Card.CLUBS_JACKSURVIVE
            playableCards[currentCardIndex] = card
            clubsJackDeathCounter
        }
        return clubsJackDeathCounter
    }
}