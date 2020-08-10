package com.natie.wheely.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.natie.wheely.R
import com.natie.wheely.data.WheelyDb
import com.natie.wheely.model.WheelOption
import com.natie.wheely.view._base.BaseFragment
import com.natie.wheely.viewmodel.OptionsViewModel
import com.natie.wheely.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.frag_options_wheely.*

class OptionsWheelyFragment : BaseFragment() {

    private val model: OptionsViewModel by activityViewModels {
        ViewModelFactory(WheelyDb.getWheelyDb(context)?.optionsDao()!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (resources.getBoolean(R.bool.is_port)) activity?.title =
            getString(R.string.options_wheely_title)
        return inflater.inflate(R.layout.frag_options_wheely, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getOptions().observe(viewLifecycleOwner, Observer<List<WheelOption>> { ops ->
            frag_options_wheely_wheel.values = ops.map { it.name }.toMutableList()
        })

        options_wheely_button_spin.setOnClickListener {
            frag_options_wheely_wheel.startSpin()
        }
    }
}