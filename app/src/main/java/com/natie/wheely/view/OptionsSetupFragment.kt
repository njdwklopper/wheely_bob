package com.natie.wheely.view

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natie.wheely.R
import com.natie.wheely.data.WheelyDb
import com.natie.wheely.model.WheelOption
import com.natie.wheely.view._base.BaseFragment
import com.natie.wheely.viewmodel.OptionsViewModel
import com.natie.wheely.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.frag_options_setup.*
import kotlinx.android.synthetic.main.view_progress.*

class OptionsSetupFragment : BaseFragment(), OptionViewDelegate {

    private val model: OptionsViewModel by activityViewModels {
        ViewModelFactory(WheelyDb.getWheelyDb(context)?.optionsDao()!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.frag_options_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupOrientationAwareUi()
        setupModelBoundUi()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupModelBoundUi() {
        view?.let {
            model.isLoading.observe(viewLifecycleOwner, Observer<Boolean> { isVisible ->
                main_progress.visibility = if (isVisible) View.VISIBLE else View.GONE
            })

            frag_options_setup_recycler_list.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(it.context)
                val options = ArrayList<WheelOption>()
                model.getOptions().observe(viewLifecycleOwner, Observer<List<WheelOption>> { ops ->
                    options.clear()
                    options.addAll(ops)
                    adapter?.notifyDataSetChanged()
                })
                adapter = OptionsAdapter(options, this@OptionsSetupFragment)
            }
        }
    }

    private fun setupOrientationAwareUi() {
        if (resources.getBoolean(R.bool.is_port)) {
            activity?.let { act ->
                act.title =
                    getString(R.string.options_setup_title)
                frag_options_setup_footer_layout.visibility = View.VISIBLE
                options_setup_button_spin.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .add(R.id.details_fragment, OptionsWheelyFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack("wheelyOptionsFrag")
                        .commit()
                }
            }
        }
    }

    override fun onDelete(option: WheelOption) {
        model.removeOption(option)
    }
}

interface OptionViewDelegate {
    fun onDelete(option: WheelOption)
}

class OptionsAdapter(private val options: List<WheelOption>, delegate: OptionViewDelegate) :
    RecyclerView.Adapter<OptionView>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        touchHelper.attachToRecyclerView(recyclerView)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionView {
        return OptionView(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_item_option, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: OptionView, position: Int) {
        holder.text.text = options[position].name
    }

    private var touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT
    ) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
            if (i == ItemTouchHelper.LEFT) {
                delegate.onDelete(options[viewHolder.adapterPosition])
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                viewHolder.itemView.setBackgroundColor(Color.RED)
            }
            if (!isCurrentlyActive) {
                viewHolder.itemView.setBackgroundColor(0)
                notifyDataSetChanged()
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            viewHolder1: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }
    })
}

class OptionView(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text: TextView = itemView.findViewById(R.id.item_option_label)
}