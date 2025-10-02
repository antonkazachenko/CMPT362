package anton_kazachenko.example.com

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

    companion object {
        private const val PREFS_NAME = "MyRunsSettings"
        private const val KEY_UNIT_PREFERENCE = "unit_preference"
        private const val KEY_USER_COMMENT = "user_comment"
        private const val KEY_PRIVACY_SETTING = "privacy_setting"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loadComment()

        val accountPreferencesLayout: ConstraintLayout = view.findViewById(R.id.account_preferences_layout)
        accountPreferencesLayout.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        val privacyLayout: ConstraintLayout = view.findViewById(R.id.privacy_setting_layout)
        val privacyCheckbox: MaterialCheckBox = view.findViewById(R.id.checkbox_privacy)

        privacyCheckbox.isChecked = prefs.getBoolean(KEY_PRIVACY_SETTING, false)

        privacyLayout.setOnClickListener {
            privacyCheckbox.isChecked = !privacyCheckbox.isChecked
            prefs.edit().putBoolean(KEY_PRIVACY_SETTING, privacyCheckbox.isChecked).apply()
        }

        val unitPreferenceLayout: ConstraintLayout = view.findViewById(R.id.unit_preference_layout)
        unitPreferenceLayout.setOnClickListener {
            showUnitPreferenceDialog()
        }

        val commentsLayout: ConstraintLayout = view.findViewById(R.id.comments_layout)
        commentsLayout.setOnClickListener {
            showCommentsDialog()
        }

        val webpageLayout: ConstraintLayout = view.findViewById(R.id.webpage_layout)
        webpageLayout.setOnClickListener {
            val url = "https://www.sfu.ca/computing.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    private fun loadComment() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        userComment = prefs.getString(KEY_USER_COMMENT, "") ?: ""
    }

    private fun showUnitPreferenceDialog() {
        val items = resources.getStringArray(R.array.unit_preference_items)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val checkedItem = prefs.getInt(KEY_UNIT_PREFERENCE, -1)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Unit Preference")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                prefs.edit().putInt(KEY_UNIT_PREFERENCE, which).apply()
                dialog.dismiss()
            }
            .show()
    }

    private fun showCommentsDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Write you comments here"
        editText.setText(userComment)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Comments")
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                userComment = editText.text.toString()
                val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_USER_COMMENT, userComment).apply()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}