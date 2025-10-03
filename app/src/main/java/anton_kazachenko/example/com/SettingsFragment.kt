package anton_kazachenko.example.com

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var userComment: String = ""
    private var openedDialog: Int = DIALOG_NONE

    companion object {
        private const val PREFS_NAME = "MyRunsSettings"
        private const val KEY_UNIT_PREFERENCE = "unit_preference"
        private const val KEY_USER_COMMENT = "user_comment"
        private const val KEY_PRIVACY_SETTING = "privacy_setting"
        private const val KEY_OPENED_DIALOG = "opened_dialog"
        private const val KEY_TEMP_COMMENT = "temp_comment"

        private const val DIALOG_NONE = 0
        private const val DIALOG_UNITS = 1
        private const val DIALOG_COMMENTS = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_OPENED_DIALOG, openedDialog)
        if (openedDialog == DIALOG_COMMENTS) {
            outState.putString(KEY_TEMP_COMMENT, userComment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadComment()

        if (savedInstanceState != null) {
            userComment = savedInstanceState.getString(KEY_TEMP_COMMENT, userComment)
            val dialogToOpen = savedInstanceState.getInt(KEY_OPENED_DIALOG, DIALOG_NONE)
            if (dialogToOpen == DIALOG_UNITS) showUnitPreferenceDialog()
            if (dialogToOpen == DIALOG_COMMENTS) showCommentsDialog()
        }

        val accountPreferencesLayout: ConstraintLayout = view.findViewById(R.id.account_preferences_layout)
        accountPreferencesLayout.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }

        val privacyLayout: ConstraintLayout = view.findViewById(R.id.privacy_setting_layout)
        val privacyCheckbox: MaterialCheckBox = view.findViewById(R.id.checkbox_privacy)
        privacyCheckbox.isChecked = prefs.getBoolean(KEY_PRIVACY_SETTING, false)
        privacyLayout.setOnClickListener {
            privacyCheckbox.isChecked = !privacyCheckbox.isChecked
            prefs.edit().putBoolean(KEY_PRIVACY_SETTING, privacyCheckbox.isChecked).apply()
        }

        val unitPreferenceLayout: ConstraintLayout = view.findViewById(R.id.unit_preference_layout)
        unitPreferenceLayout.setOnClickListener { showUnitPreferenceDialog() }

        val commentsLayout: ConstraintLayout = view.findViewById(R.id.comments_layout)
        commentsLayout.setOnClickListener {
            loadComment()
            showCommentsDialog()
        }

        val webpageLayout: ConstraintLayout = view.findViewById(R.id.webpage_layout)
        webpageLayout.setOnClickListener {
            val url = "https://www.sfu.ca/computing.html"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun loadComment() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        userComment = prefs.getString(KEY_USER_COMMENT, "") ?: ""
    }

    private fun showUnitPreferenceDialog() {
        openedDialog = DIALOG_UNITS
        val items = resources.getStringArray(R.array.unit_preference_items)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val checkedItem = prefs.getInt(KEY_UNIT_PREFERENCE, -1)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Unit Preference")
            .setNegativeButton("Cancel", null)
            .setSingleChoiceItems(items, checkedItem) { d, which ->
                prefs.edit().putInt(KEY_UNIT_PREFERENCE, which).apply()
                d.dismiss()
            }
            .create()

        dialog.setOnDismissListener { openedDialog = DIALOG_NONE }
        dialog.show()
    }

    private fun showCommentsDialog() {
        openedDialog = DIALOG_COMMENTS
        val editText = EditText(requireContext()).apply {
            hint = "Write you comments here"
            setText(userComment)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    userComment = s.toString()
                }
            })
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Comments")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_USER_COMMENT, userComment).apply()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnDismissListener { openedDialog = DIALOG_NONE }
        dialog.show()
    }
}