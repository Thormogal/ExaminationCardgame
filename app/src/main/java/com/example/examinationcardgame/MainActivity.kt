package com.example.examinationcardgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView


private lateinit var startButton: ImageView
private lateinit var rulesButton: ImageView
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startButton = findViewById(R.id.startButton)
        rulesButton = findViewById(R.id.rulesButton)

        startButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(startButton)
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        rulesButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(rulesButton)
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }


    }


}