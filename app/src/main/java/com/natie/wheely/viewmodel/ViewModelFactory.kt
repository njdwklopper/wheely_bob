package com.natie.wheely.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.natie.wheely.data.WheelOptionDao

class ViewModelFactory(private val dao: WheelOptionDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OptionsViewModel(dao) as T
    }
}