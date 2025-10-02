package anton_kazachenko.example.com

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MapDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.map_display_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Map"

        val saveButton: Button = findViewById(R.id.btn_map_save)
        saveButton.setOnClickListener {
            finish()
        }

        val cancelButton: Button = findViewById(R.id.btn_map_cancel)
        cancelButton.setOnClickListener {
            finish()
        }
    }
}