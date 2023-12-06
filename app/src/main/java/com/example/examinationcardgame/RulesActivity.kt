package com.example.examinationcardgame

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

private lateinit var menuButton: ImageView

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        menuButton = findViewById(R.id.menuButton)

        menuButton.setOnClickListener {
            ButtonUtils.pressedButtonAppearance(menuButton)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}