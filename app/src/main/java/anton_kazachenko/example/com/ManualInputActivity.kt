package anton_kazachenko.example.com

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

class ManualInputActivity : AppCompatActivity() {

    private var dialogPosition: Int = -1
    private var tempInputText: String = ""

    companion object {
        private const val KEY_DIALOG_POSITION = "dialog_position"
        private const val KEY_TEMP_TEXT = "temp_text"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_DIALOG_POSITION, dialogPosition)
        if (dialogPosition >= 2) {
            outState.putString(KEY_TEMP_TEXT, tempInputText)
        }
    }

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
            tempInputText = ""
            showDialogForPosition(position)
        }

        if (savedInstanceState != null) {
            tempInputText = savedInstanceState.getString(KEY_TEMP_TEXT, "")
            val savedPosition = savedInstanceState.getInt(KEY_DIALOG_POSITION, -1)
            if (savedPosition != -1) {
                showDialogForPosition(savedPosition)
            }
        }

        findViewById<Button>(R.id.btn_manual_save).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_manual_cancel).setOnClickListener { finish() }
    }

    private fun showDialogForPosition(position: Int) {
        dialogPosition = position
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this, { _, _, _, _ -> },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.setOnDismissListener { dialogPosition = -1 }
        dialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            this, { _, _, _ -> },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        dialog.setOnDismissListener { dialogPosition = -1 }
        dialog.show()
    }

    private fun showInputDialog(title: String, hint: String, inputType: Int) {
        val editText = EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
            this.setText(tempInputText)
            this.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    tempInputText = s.toString()
                }
            })
        }
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnDismissListener { dialogPosition = -1 }
        dialog.show()
    }
}