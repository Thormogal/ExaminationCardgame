package com.example.examinationcardgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

private lateinit var startButton: ImageView
private lateinit var rulesButton: ImageView
private lateinit var continueButton: ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startButton = findViewById(R.id.newGameButton)
        rulesButton = findViewById(R.id.rulesButton)
        continueButton = findViewById(R.id.continueButton)

        startButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(startButton)
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        rulesButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(rulesButton)
            val intent = Intent(this, RulesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        continueButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(continueButton)
            val intent = Intent(this, GameActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }
}