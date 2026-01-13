package com.example.ai_fitness_workout_manager.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.ai.GeminiVisionManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class IngredientRecognitionActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var ivImagePreview: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSelectFromGallery: Button
    private lateinit var btnAnalyzeImage: Button
    private lateinit var tvInstructions: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etIngredients: EditText
    private lateinit var btnGetRecipeRecommendations: Button

    private var currentPhotoUri: Uri? = null
    private var currentImageBitmap: Bitmap? = null

    // Camera permission launcher
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery permission launcher (for Android 13+)
    private val requestGalleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchGallery()
        } else {
            Toast.makeText(this, "Gallery permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera capture launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            loadImageFromUri(currentPhotoUri!!)
        }
    }

    // Gallery selection launcher
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it
            loadImageFromUri(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_recognition)

        initializeViews()
        setupClickListeners()
        initializeVisionAI()
    }

    private fun initializeVisionAI() {
        if (!GeminiVisionManager.isInitialized()) {
            GeminiVisionManager.initialize()
        }

        if (!GeminiVisionManager.hasValidApiKey()) {
            Toast.makeText(
                this,
                "Gemini API key not configured. Vision features disabled.",
                Toast.LENGTH_LONG
            ).show()
            btnAnalyzeImage.isEnabled = false
        }
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        ivImagePreview = findViewById(R.id.ivImagePreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery)
        btnAnalyzeImage = findViewById(R.id.btnAnalyzeImage)
        tvInstructions = findViewById(R.id.tvInstructions)
        progressBar = findViewById(R.id.progressBar)
        etIngredients = findViewById(R.id.etIngredients)
        btnGetRecipeRecommendations = findViewById(R.id.btnGetRecipeRecommendations)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        btnSelectFromGallery.setOnClickListener {
            checkGalleryPermissionAndLaunch()
        }

        btnAnalyzeImage.setOnClickListener {
            if (currentImageBitmap != null) {
                analyzeImage(currentImageBitmap!!)
            } else {
                Toast.makeText(this, "Please select or capture an image first", Toast.LENGTH_SHORT).show()
            }
        }

        btnGetRecipeRecommendations.setOnClickListener {
            val ingredientsText = etIngredients.text.toString().trim()
            if (ingredientsText.isEmpty()) {
                Toast.makeText(this, "Please add some ingredients first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getRecipeRecommendations(ingredientsText)
        }

        // Enable the recommendation button when user types ingredients
        etIngredients.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                btnGetRecipeRecommendations.isEnabled = !s.isNullOrEmpty()
            }
        })
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkGalleryPermissionAndLaunch() {
        // Android 13+ uses READ_MEDIA_IMAGES, older versions use READ_EXTERNAL_STORAGE
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                launchGallery()
            }
            else -> {
                requestGalleryPermission.launch(permission)
            }
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            val photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            currentPhotoUri = photoUri
            takePictureLauncher.launch(photoUri)
        } catch (e: IOException) {
            Toast.makeText(this, "Error creating image file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchGallery() {
        selectImageLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timestamp = System.currentTimeMillis()
        val storageDir = cacheDir
        return File(storageDir, "ingredient_photo_$timestamp.jpg")
    }

    private fun loadImageFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Compress image to reasonable size for API (max 1024x1024)
            currentImageBitmap = compressImage(originalBitmap)

            ivImagePreview.setImageBitmap(currentImageBitmap)
            btnAnalyzeImage.isEnabled = true
            tvInstructions.text = "Image loaded! Click 'Analyze' to detect ingredients."
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compressImage(bitmap: Bitmap): Bitmap {
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val scale = if (width > height) {
            maxDimension.toFloat() / width
        } else {
            maxDimension.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun analyzeImage(bitmap: Bitmap) {
        // Show progress
        progressBar.visibility = android.view.View.VISIBLE
        btnAnalyzeImage.isEnabled = false
        tvInstructions.text = "Analyzing image with AI..."

        lifecycleScope.launch {
            val result = GeminiVisionManager.analyzeIngredientsImage(bitmap)

            result.onSuccess { ingredientsText ->
                etIngredients.setText(ingredientsText)
                tvInstructions.text = "Ingredients detected! You can edit them below."
                btnGetRecipeRecommendations.isEnabled = true
                Toast.makeText(
                    this@IngredientRecognitionActivity,
                    "Analysis complete! Edit ingredients if needed.",
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure { error ->
                tvInstructions.text = "Error analyzing image. Please try again."
                showErrorDialog("Analysis Error", error.message ?: "Unknown error occurred")
            }

            progressBar.visibility = android.view.View.GONE
            btnAnalyzeImage.isEnabled = true
        }
    }

    private fun getRecipeRecommendations(ingredients: String) {
        // Show progress
        progressBar.visibility = android.view.View.VISIBLE
        btnGetRecipeRecommendations.isEnabled = false
        tvInstructions.text = "Generating recipe recommendations..."

        lifecycleScope.launch {
            val result = GeminiVisionManager.getRecipeRecommendations(ingredients)

            result.onSuccess { recommendations ->
                // Launch new activity to show recommendations
                val intent = Intent(this@IngredientRecognitionActivity, RecipeRecommendationsActivity::class.java)
                intent.putExtra(RecipeRecommendationsActivity.EXTRA_RECOMMENDATIONS, recommendations)
                startActivity(intent)
                tvInstructions.text = "Recommendations generated! Click the button again for new ideas."
            }.onFailure { error ->
                tvInstructions.text = "Error generating recommendations. Please try again."
                showErrorDialog("Recommendation Error", error.message ?: "Unknown error occurred")
            }

            progressBar.visibility = android.view.View.GONE
            btnGetRecipeRecommendations.isEnabled = true
        }
    }

    private fun showErrorDialog(title: String, errorMessage: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(errorMessage)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Copy Error") { _, _ ->
                val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Error Message", errorMessage)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Error copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .create()

        dialog.show()

        // Make the error message scrollable
        dialog.findViewById<TextView>(android.R.id.message)?.apply {
            setTextIsSelectable(true)
            maxLines = 30
            setVerticalScrollBarEnabled(true)
            movementMethod = android.text.method.ScrollingMovementMethod()
        }
    }
}
