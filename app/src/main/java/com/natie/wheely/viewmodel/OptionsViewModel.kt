package com.natie.wheely.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.natie.wheely.data.WheelOptionDao
import com.natie.wheely.model.WheelOption
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class OptionsViewModel(private val dao: WheelOptionDao) : ViewModel() {

    var isLoading = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<String>("")

    fun getOptions(): LiveData<List<WheelOption>> {
        return dao.getOptions()
    }

    fun addNewOption(option: String) {
        isLoading.value = true
        viewModelScope.launch {
            val success = viewModelScope.async {

                val size = dao.getCount()
                if (size > 15) {
                    throw Exception("Too many Slices")
                }
                val op = WheelOption(
                    name = option
                )
                dao.insertOption(op)
                return@async null
            }

            try {
                isLoading.value = false
                success.await()
            } catch (e: Exception) {
                error.value = e.message
                e.printStackTrace()
            }
        }
    }

    fun removeOption(option: WheelOption) {
        isLoading.value = true
        viewModelScope.launch {
            val success = viewModelScope.async {
                try {
                    val opId = option.id!!
                    dao.deleteOption(opId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@async false
                }
                return@async true
            }

            if (!success.await()) {
                //handle error
                print("Error")
                isLoading.value = false
            } else {
                isLoading.value = false
            }
        }
    }
}

class TooBigException : Exception() {
}