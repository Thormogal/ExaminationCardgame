package com.example.examinationcardgame

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

private lateinit var backButton: ImageView
class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(backButton)
            onBackPressed()
        }


    }
}