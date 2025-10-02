package anton_kazachenko.example.com

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val profileImageBitmap = MutableLiveData<Bitmap>()
}