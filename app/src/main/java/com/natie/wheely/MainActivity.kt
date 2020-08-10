package com.natie.wheely

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.lifecycle.Observer
import com.natie.wheely.data.WheelyDb
import com.natie.wheely.viewmodel.OptionsViewModel
import com.natie.wheely.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model: OptionsViewModel by viewModels {
            ViewModelFactory(WheelyDb.getWheelyDb(this)?.optionsDao()!!)
        }

        model.error.observeForever { error ->
            if (error.isNotEmpty()) {
                openError(error)
                model.error.value = ""
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            if (supportFragmentManager.backStackEntryCount > 0) onBackPressed()
            openOptionEntryDiag()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openError(message: String?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.error))
        builder.setMessage(message)
        builder.show()
    }

    private fun openOptionEntryDiag() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.menu_add_title))

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT and InputType.TYPE_TEXT_FLAG_CAP_WORDS
        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = LengthFilter(10)

        input.filters = filters

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(resources.getDimensionPixelSize(R.dimen.gutter_space))
        input.layoutParams = params
        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton(
            getString(R.string.menu_add_button_okay)
        ) { _, _ ->
            val model: OptionsViewModel by viewModels {
                ViewModelFactory(WheelyDb.getWheelyDb(this)?.optionsDao()!!)
            }
            val text = input.text.toString()
            model.addNewOption(text)
        }
        builder.setNegativeButton(
            getString(R.string.menu_add_button_cancel)
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
        input.requestFocus()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        WheelyDb.destroySingleton()
        super.onDestroy()
    }
}