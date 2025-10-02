    package anton_kazachenko.example.com

    import android.Manifest
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.os.Build
    import android.os.Bundle
    import android.provider.MediaStore
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.RadioGroup
    import android.widget.Toast
    import androidx.activity.result.ActivityResultLauncher
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.lifecycle.ViewModelProvider
    import java.io.File
    import java.io.FileInputStream
    import java.io.FileOutputStream

    class ProfileActivity : AppCompatActivity() {

        private lateinit var imageViewProfile: ImageView
        private lateinit var buttonTakePicture: Button
        private lateinit var editTextName: EditText
        private lateinit var editTextEmail: EditText
        private lateinit var editTextPhone: EditText
        private lateinit var editTextClass: EditText
        private lateinit var radioGroupGender: RadioGroup
        private lateinit var editTextMajor: EditText
        private lateinit var buttonSave: Button
        private lateinit var buttonCancel: Button

        private lateinit var viewModel: ProfileViewModel

        private var newProfileBitmap: Bitmap? = null
        private val permanentImageFileName = "profile_image.jpg"

        private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>

        companion object {
            private const val PREFS_NAME = "MyRunsProfile"
            private const val KEY_NAME = "name"
            private const val KEY_CLASS = "class"
            private const val KEY_EMAIL = "email"
            private const val KEY_PHONE = "phone"
            private const val KEY_GENDER_ID = "gender_id"
            private const val KEY_MAJOR = "major"
            private const val REQUEST_CODE_PERMISSIONS = 101
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profile)

            checkAndRequestPermissions()
            setupUI()

            viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

            takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val bitmap = result.data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        newProfileBitmap = it
                        imageViewProfile.setImageBitmap(it)
                    }
                }
            }

            viewModel.profileImageBitmap.observe(this) { bitmap ->
                imageViewProfile.setImageBitmap(bitmap)
            }

            setupClickListeners()
            loadData()
        }

        private fun setupUI() {
            imageViewProfile = findViewById(R.id.image_profile)
            buttonTakePicture = findViewById(R.id.button_take_picture)
            editTextName = findViewById(R.id.edit_text_name)
            editTextEmail = findViewById(R.id.edit_text_email)
            editTextClass = findViewById(R.id.edit_text_class)
            editTextPhone = findViewById(R.id.edit_text_phone)
            radioGroupGender = findViewById(R.id.radio_group_gender)
            editTextMajor = findViewById(R.id.edit_text_major)
            buttonSave = findViewById(R.id.button_save)
            buttonCancel = findViewById(R.id.button_cancel)
        }

        private fun setupClickListeners() {
            buttonTakePicture.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            }

            buttonSave.setOnClickListener {
                saveData()
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                finish()
            }

            buttonCancel.setOnClickListener {
                finish()
            }
        }

        private fun saveData() {
            newProfileBitmap?.let { bmp ->
                val permanentFile = File(filesDir, permanentImageFileName)
                try {
                    FileOutputStream(permanentFile).use { out ->
                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putString(KEY_CLASS, editTextClass.text.toString())
            prefs.putString(KEY_NAME, editTextName.text.toString())
            prefs.putString(KEY_EMAIL, editTextEmail.text.toString())
            prefs.putString(KEY_PHONE, editTextPhone.text.toString())
            prefs.putInt(KEY_GENDER_ID, radioGroupGender.checkedRadioButtonId)
            prefs.putString(KEY_MAJOR, editTextMajor.text.toString())
            prefs.apply()
        }

        private fun loadData() {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            editTextName.setText(prefs.getString(KEY_NAME, ""))
            editTextEmail.setText(prefs.getString(KEY_EMAIL, ""))
            editTextPhone.setText(prefs.getString(KEY_PHONE, ""))
            editTextMajor.setText(prefs.getString(KEY_MAJOR, ""))
            editTextClass.setText(prefs.getString(KEY_CLASS, ""))

            val genderId = prefs.getInt(KEY_GENDER_ID, -1)
            if (genderId != -1) {
                radioGroupGender.check(genderId)
            }

            val imageFile = File(filesDir, permanentImageFileName)
            if (imageFile.exists()) {
                val bitmap = loadBitmapFromFile()
                viewModel.profileImageBitmap.value = bitmap
            }
        }

        private fun loadBitmapFromFile(): Bitmap? {
            return try {
                val file = File(filesDir, permanentImageFileName)
                val inputStream = FileInputStream(file)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun checkAndRequestPermissions() {
            val permissionsToRequest = ArrayList<String>()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.CAMERA)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
            }
        }
    }