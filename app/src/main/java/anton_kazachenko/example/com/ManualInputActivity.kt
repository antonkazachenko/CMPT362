package anton_kazachenko.example.com

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

class ManualInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.manual_input_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "MyRuns2"

        val listView: ListView = findViewById(R.id.manual_input_list)
        val items = arrayOf("Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> showDatePickerDialog()
                1 -> showTimePickerDialog()
                2 -> showInputDialog("Duration", "Enter duration in minutes", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                3 -> showInputDialog("Distance", "Enter distance", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                4 -> showInputDialog("Calories", "Enter calories burned", InputType.TYPE_CLASS_NUMBER)
                5 -> showInputDialog("Heart Rate", "Enter average heart rate", InputType.TYPE_CLASS_NUMBER)
                6 -> showInputDialog("Comment", "Enter comments", InputType.TYPE_CLASS_TEXT)
            }
        }

        val saveButton: Button = findViewById(R.id.btn_manual_save)
        saveButton.setOnClickListener {
            finish()
        }

        val cancelButton: Button = findViewById(R.id.btn_manual_cancel)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun showInputDialog(title: String, hint: String, inputType: Int) {
        val editText = EditText(this)
        editText.hint = hint
        editText.inputType = inputType

        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}