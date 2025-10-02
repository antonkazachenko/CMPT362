package anton_kazachenko.example.com

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartViewModel : ViewModel() {
    val inputType = MutableLiveData<String>()

    val activityType = MutableLiveData<String>()
}