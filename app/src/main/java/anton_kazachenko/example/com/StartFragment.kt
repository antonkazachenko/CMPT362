package anton_kazachenko.example.com

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class StartFragment : Fragment() {

    private lateinit var viewModel: StartViewModel

    private lateinit var inputTypeAdapter: ArrayAdapter<String>
    private lateinit var activityTypeAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputTypeSpinner: AutoCompleteTextView = view.findViewById(R.id.input_type_spinner)
        val activityTypeSpinner: AutoCompleteTextView = view.findViewById(R.id.activity_type_spinner)
        val startButton: Button = view.findViewById(R.id.btn_start)

        inputTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.input_type_array).toList()
        )
        activityTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.activity_type_array).toList()
        )

        setupDropdown(inputTypeSpinner, inputTypeAdapter) { selected ->
            viewModel.inputType.value = selected
        }
        viewModel.inputType.observe(viewLifecycleOwner) { value ->
            if (!value.isNullOrEmpty() && inputTypeSpinner.text.toString() != value) {
                inputTypeSpinner.setText(value, false)
            }
            clearFilterAfterSetText(inputTypeSpinner)
        }

        setupDropdown(activityTypeSpinner, activityTypeAdapter) { selected ->
            viewModel.activityType.value = selected
        }
        viewModel.activityType.observe(viewLifecycleOwner) { value ->
            if (!value.isNullOrEmpty() && activityTypeSpinner.text.toString() != value) {
                activityTypeSpinner.setText(value, false)
            }
            clearFilterAfterSetText(activityTypeSpinner)
        }

        if (viewModel.inputType.value.isNullOrEmpty()) {
            viewModel.inputType.value = inputTypeAdapter.getItem(0)
        }
        if (viewModel.activityType.value.isNullOrEmpty()) {
            viewModel.activityType.value = activityTypeAdapter.getItem(0)
        }

        startButton.setOnClickListener {
            when (viewModel.inputType.value) {
                "Manual Entry" -> startActivity(Intent(activity, ManualInputActivity::class.java))
                else -> startActivity(Intent(activity, MapDisplayActivity::class.java))
            }
        }
    }

    private fun setupDropdown(
        tv: AutoCompleteTextView,
        adapter: ArrayAdapter<String>,
        onSelect: (String) -> Unit
    ) {
        tv.setAdapter(adapter)
        tv.setOnItemClickListener { _, _, position, _ ->
            adapter.getItem(position)?.let(onSelect)
        }
    }
    private fun clearFilterAfterSetText(tv: AutoCompleteTextView) {
        tv.post {
            (tv.adapter as? ArrayAdapter<*>)?.filter?.filter(null)
        }
    }
}
