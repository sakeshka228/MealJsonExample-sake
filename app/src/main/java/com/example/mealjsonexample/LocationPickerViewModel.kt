package com.example.mealjsonexample

// LocationPickerViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationPickerViewModel : ViewModel() {
    private val _savedLocations = MutableLiveData<List<LatLng>>(emptyList())
    val savedLocations: LiveData<List<LatLng>> get() = _savedLocations

    fun saveLocation(location: LatLng) {
        val updatedList = _savedLocations.value.orEmpty().toMutableList()
        updatedList.add(location)
        _savedLocations.value = updatedList
    }
}
