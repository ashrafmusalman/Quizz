
package com.example.quizz.Activities.Util
object ColorPicker {

    private val colors = arrayOf(
        "#F0E4FF",  // Light Purple White

        "#F0F0F0",  // Very Light Gray
        "#E8F0FF",  // Very Light Blue
        "#FCFCFC",  // Near Pure White

        "#FAFAFA",  // Very Light Grayish White
        "#DDEEFF",  // Soft Light Blue
        "#E5F9E0",  // Very Light Green

        "#E8FFF0",  // Light Greenish White

        "#F0F8FF",  // Light Azure
        "#F0FFF5",  // Slightly Greenish White
        "#E6F2FF",  // Light Bluish White

        "#F5F5F5",  // Already a Light Gray
        "#DFF7E8",  // Soft Green
        "#FFFFFF",  // Pure White

        "#EDF7FF",  // Very Light Blueish White
        "#F5EEFF",  // Soft Lavender

        "#ECFBEF",  // Light Mint
        "#F3ECFF" ,  // Slightly Purple White

        "#F3E8FF",  // Very Light Lavender
        "#F8F1FF",  // Very Light Purple White

    )

    private var colorIndex = 0

    fun getColor(): String {
        val currentColor = colors[colorIndex]
        colorIndex = (colorIndex + 1) % colors.size
        return currentColor
    }
}