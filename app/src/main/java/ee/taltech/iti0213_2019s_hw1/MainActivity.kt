package ee.taltech.iti0213_2019s_hw1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    private var isSwitchToggeled = false
    private var selectedMode = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "lifecycle onCreate")

        createSpinner()
        createSwitch()
        rewriteTemplates()
    }

    private fun createSwitch() {
        val switch: Switch = findViewById(R.id.switchTurn)
        switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            this.isSwitchToggeled = isChecked
            rewriteTemplates()
        })
    }

    private fun createSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner_mode_selection)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.game_modes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "spinner onItemSelected $id")
                selectedMode = id.toInt()
                rewriteTemplates()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                Log.d(TAG, "spinner onNothingSelected")
            }
        }
    }

    fun rewriteTemplates() {
        // switch text
        val switch: TextView = findViewById(R.id.switchTurn)

        if (this.isSwitchToggeled && this.selectedMode >= 1 || !this.isSwitchToggeled && this.selectedMode >= 2) {
            if (this.isSwitchToggeled) {
                switch.text = "red AI"
            } else {
                switch.text = "blue AI"
            }
        } else {
            if (this.isSwitchToggeled) {
                switch.text = "red Player"
            } else {
                switch.text = "blue Player"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "lifecycle onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "lifecycle onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "lifecycle onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "lifecycle onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "lifecycle onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "lifecycle onRestart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "lifecycle onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "lifecycle onRestoreInstanceState")
    }

}
