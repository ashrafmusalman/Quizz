import com.example.quizz.R

object imagePicker {

    // Array of image resources (add more as needed)
    private val quizImages = arrayOf(
        R.drawable.hat_1,
        R.drawable.pencil_2,
        R.drawable.micoroscope_3,
        R.drawable.book_4,
        R.drawable.book_5,
        R.drawable.telescope_6,
        R.drawable.hat_7,
        R.drawable.shoes_9,
        R.drawable.cup_10
    )

    private var imageIndex = 0

    fun getImages(): Int {



        imageIndex = (imageIndex + 1) % quizImages.size

        // Return the current image ID
        return quizImages[imageIndex]
    }
}
