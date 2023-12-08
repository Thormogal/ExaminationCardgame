package com.example.examinationcardgame

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RulesActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        val myTextView = findViewById<TextView>(R.id.rulesTextView)
        val myString = myTextView.text.toString()
        val spannableString = SpannableString(myString)
        val start = 874
        val end = myString.length
        spannableString.setSpan(StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        myTextView.text = spannableString

        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(backButton)
            onBackPressed()
        }
    }
}