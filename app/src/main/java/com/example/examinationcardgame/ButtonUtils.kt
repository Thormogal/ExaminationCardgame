package com.example.examinationcardgame

import android.widget.ImageView

class ButtonUtils {
    companion object {
        fun pressedButtonAppearance(button: ImageView) {
            button.imageAlpha = 150
            button.postDelayed({
                button.imageAlpha = 255
            }, 100)
        }
    }
}