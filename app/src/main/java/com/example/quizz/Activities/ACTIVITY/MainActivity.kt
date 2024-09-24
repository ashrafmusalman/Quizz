package com.example.quizz.Activities.ACTIVITY

import android.app.Dialog
import com.example.quizz.Activities.DataClass.Quizz
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quizz.Activities.ADAPTER.QuizAdapter
import com.example.quizz.Activities.DataClass.Questions
import com.example.quizz.R
import com.example.quizz.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var setaAdapter: QuizAdapter
    private lateinit var quizRecylerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var quizList = mutableListOf<Quizz>()
    private lateinit var search: SearchView

    private var fileUri: Uri? = null;
    private lateinit var profileImageView: ImageView

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //since the profile image is present in the header xml and we are getting that from main activity so we need to access the headerView of the NavigationView   Once you have the headerView, you can use findViewById to get the reference to the ImageView
        //setting for the profile images Access the header view of the NavigationView
        val headerView = binding.navView.getHeaderView(0)
        // Find the ImageView inside the header layout
        profileImageView = headerView.findViewById(R.id.profileImage)
        //change the color of the icon of the status bar


        this.window.decorView.getWindowInsetsController()
            ?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)

        auth = FirebaseAuth.getInstance()

        val photoUri = auth.currentUser?.photoUrl

        if (photoUri == null) {
            profileImageView.setImageResource(R.drawable.profile)
        } else {
            Glide.with(this)
                .load(photoUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                )
                .into(profileImageView)
        }

        profileImageView.setOnClickListener {
            imageLauncher.launch("image/*")
        }

        window.statusBarColor = resources.getColor(R.color.colorPrimaryButtonBg, theme)
        //setting the date present inside the floating action button

        // Set up the AppBar
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)




        firestore = FirebaseFirestore.getInstance()


//calling the functions
        setUpViews()
        setUpRv()
        setUpFirestore()


        //  ➡️➡️  SEARCH RELATED WORK
        // Initialize the SearchView and TextView
        search = findViewById<SearchView>(R.id.search)

        val toolbarTitle = findViewById<TextView>(R.id.toolbarTitle)

        // Access the search icon inside the SearchView
        val searchIconId = resources.getIdentifier("android:id/search_button", null, null)
        val searchIcon = search.findViewById<ImageView>(searchIconId)

        // Set the color filter to white if the search icon is found
        searchIcon?.setColorFilter(Color.WHITE)

        // Access the EditText inside the SearchView
        val searchEditTextId = resources.getIdentifier("android:id/search_src_text", null, null)
        val searchEditText = search.findViewById<EditText>(searchEditTextId)

        // Set the text color of the SearchView's input field to white
        searchEditText?.setTextColor(Color.WHITE)

// Change the cursor color to white
        searchEditText?.setCursorVisible(true)
        searchEditText?.highlightColor = Color.WHITE


        // for the cancel option that is shown inside the  search option
        val cancelImageInSearch = resources.getIdentifier("android:id/search_close_btn", null, null)
        val closeIcon = search.findViewById<ImageView>(cancelImageInSearch)



        closeIcon.setOnClickListener {
            toolbarTitle.visibility = View.VISIBLE
            search.onActionViewCollapsed()
        }
        // Set the click listener for the search icon
        searchIcon?.setOnClickListener {
            toolbarTitle.visibility = View.GONE
            search.onActionViewExpanded()
            search.requestFocus()
            closeIcon?.setColorFilter(Color.WHITE) // Set close icon to white
        }


        // Optionally, set the toolbarTitle text color to white (if needed)
        toolbarTitle.setTextColor(Color.WHITE)


        //this is the click operation that when any recycler view image of main activity is clicked , onOptionclick is defined in quiz adapter
        setaAdapter.onOptionClick = { it ->

            val intent = Intent(this, QuestionActivity::class.java)
            intent.putExtra("Title", it)
            startActivity(intent)
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
        }



        searchRelatedFunction()

    }


    private fun setUpFirestore() {


        // Enable offline persistence if not already enabled
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        val collectionReference = firestore.collection("quizzes")

        collectionReference.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "Network error loading cache data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

        }

        // Show shimmer while loading
        collectionReference.addSnapshotListener { value, error ->
            if (error != null) {// that is there is some error in fetching data from firestore
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                //show shimmer and hide recyclerview
                binding.appBarLayout.shimmerLayout.startShimmer()
                binding.appBarLayout.shimmerLayout.visibility = View.VISIBLE
                binding.appBarLayout.RVBelowAppbar.visibility = View.GONE
                return@addSnapshotListener
            }

            if (value != null) {// that is there is no error in fetching data from firestore
                Log.d("data", value.toString())
                quizList.clear()
                val quizzList =   value.toObjects(Quizz::class.java)//  This line converts the documents of the Firestore query result (value) into a list of com.example.quizz.Activities.DataClass.Quizz objects and stores them in quizzList.this line interact with the firestore
                quizList.addAll(quizzList)

                setaAdapter.notifyDataSetChanged()
                Log.d( value.toString(), "setUpFirestore: ")
                // Hide shimmer and show RecyclerView
                binding.appBarLayout.RVBelowAppbar.visibility = View.VISIBLE
                binding.appBarLayout.shimmerLayout.visibility = View.GONE

            } else {
                Log.d("Firestore", "No data received")
                binding.appBarLayout.shimmerLayout.stopShimmer()
                quizRecylerView.visibility = View.GONE
            }
        }


    }


    private fun setUpRv() {
        // Find the RecyclerView inside the included layout
        quizRecylerView = findViewById(R.id.RV_belowAppbar)
        // Set up the RecyclerView
        setaAdapter = QuizAdapter(this, quizList)
        quizRecylerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        quizRecylerView.adapter = setaAdapter
    }


    private fun setUpViews() {
        setUpDrawerLayout()
    }

    private fun setUpDrawerLayout() {
        drawer = binding.mainDrawer
        toggle = ActionBarDrawerToggle(this, drawer, topAppBar, R.string.open, R.string.close)
        toggle.syncState()

        // Change the color of the default hamburger icon THIS IS IMPORTANT IT WILL CHANGE THE COLOR OF THE HAMBURGER ICON TO THE COLOR WE WANT
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.colorPrimaryButton, theme)

        // Open the drawer on click of the hamburger icon
        topAppBar.setNavigationOnClickListener {
            if (drawer.isDrawerOpen(binding.navView)) {
                drawer.closeDrawer(binding.navView)
            } else {
                drawer.openDrawer(binding.navView)
            }
        }

        // Handle navigation item clicks
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true


            when (menuItem.itemId) {
                R.id.follow -> {
                    Toast.makeText(this, "following....", Toast.LENGTH_SHORT).show()
                }

                R.id.RateUs -> {

                    val dialog = Dialog(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null)
                    dialog.setContentView(view)

                    // Set dialog width and height (e.g., 90% of screen width, and wrap content for height)
                    dialog.window?.setLayout(
                        (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 90% of screen width
                        WindowManager.LayoutParams.WRAP_CONTENT  // Wrap content height
                    )
                    val submitButton: Button = view.findViewById(R.id.ratebtn)
                    submitButton.setOnClickListener {
                        Toast.makeText(this, "thanks for your rating", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                    }

                    dialog.show()

                }


            }
            true
        }
    }

    //if any menu items is clickded
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawer.closeDrawer(androidx.core.view.GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    // way to access gallery images
    val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileUri = uri
        uploadImage()
    }

    //this function is for the profile image setup
    fun uploadImage() {
        // Display a progress dialog while uploading the image
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Uploading your image...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        lifecycleScope.launch {
            fileUri?.let { uri ->
                Glide.with(this@MainActivity)
                    .load(uri)
                    .placeholder(R.drawable.profile)
                    .into(profileImageView)

                // Create a storage reference and upload the image
                val storageRef: StorageReference = FirebaseStorage.getInstance()
                    .getReference("profile_images/${UUID.randomUUID()}")

                storageRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Get the download URL after successful upload
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            Log.d("uploadImage", "Download URL: $downloadUrl")

                            // Update the user's profile with the new photo URL
                            auth.currentUser?.let { user ->
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(downloadUrl))
                                    .build()

                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d("User Profile", "Profile updated successfully")
                                            Glide.with(this@MainActivity)
                                                .load(downloadUrl)
                                                .placeholder(R.drawable.profile)
                                                .into(profileImageView)
                                            Toast.makeText(
                                                applicationContext,
                                                "Profile updated successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Failed to update profile",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        progressDialog.dismiss()
                                    }
                            } ?: run {
                                Toast.makeText(
                                    applicationContext,
                                    "User is not logged in",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.dismiss()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                applicationContext,
                                "Failed to retrieve download URL",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog.dismiss()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Failed to upload image",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
            } ?: run {
                Toast.makeText(applicationContext, "No image selected", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }


    private fun searchRelatedFunction() {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //if the user merely presses the search icon from keyboard then it will do nothing
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {//this line run when the user write something in the search bar and press the search icon
                // it then filter the adapter and if matches with the user input then it will return true
                setaAdapter.filter(newText ?: "") // Pass the query to the adapter's filter method
                return true
            }

        })
    }


}
