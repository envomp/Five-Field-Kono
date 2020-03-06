package ee.taltech.iti0213_2019s_hw1

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private var gameUID = 0
        private var kono = konoGame()
    }

    private var firstAI = false;
    private var secondAI = false;
    private var isSwitchToggeled = false
    private var selectedMode = 0
    private var hasGameStarted = false;
    private var lastButtonClicked = -1;
    private var playerOneTurn = true

    enum class Color(val rgb: Int) {
        RED(2),
        WHITE(0),
        BLUE(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "lifecycle onCreate")

        kono.initializeButtonMatrix()
        createSpinner()
        createSwitch()
        rewriteTemplates()
        wireGameButtons()
        wireStartButton()
    }

    private fun wireGameButtons() {
        for (buttonArray in kono.buttonMatrix) {
            for (buttonId in buttonArray) {
                val button = findViewById<Button>(buttonId)
                button.setOnClickListener {

                    if (playerOneTurn) {
                        if (!firstAI) {
                            gameButtonLogic(button, buttonId)
                        }
                    } else {
                        if (!secondAI) {
                            gameButtonLogic(button, buttonId)
                        }
                    }
                }
                button.text = ""
            }
        }
    }

    private fun gameButtonLogic(button: Button, buttonId: Int) {
        if (hasGameStarted) {
            if (lastButtonClicked == -1) {

                if (isSelectedsTurn(button)) {
                    lastButtonClicked = buttonId
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Incorrect move!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                when {
                    lastButtonClicked == buttonId -> {
                        resetMove()
                        colorBoard()
                    }
                    kono.getAvailableMoves(findViewById(lastButtonClicked)).contains(buttonId) -> {
                        kono.doMove(button, playerOneTurn, isSwitchToggeled)
                        val lastY = kono.getYPos(findViewById(lastButtonClicked))
                        val lastX = kono.getXPos(findViewById(lastButtonClicked))
                        kono.gameBoard[lastY][lastX] = konoGame.Color.WHITE.rgb
                        lastButtonClicked = -1
                        finalizeMove()
                        colorBoard()
                        invokeAI()
                    }
                    else -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Invalid move!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun isSelectedsTurn(button: Button): Boolean {
        if (playerOneTurn == !isSwitchToggeled) {
            val y = kono.getYPos(button)
            val x = kono.getXPos(button)
            if (kono.gameBoard[y][x] == Color.BLUE.rgb) {
                button.setBackgroundColor(resources.getColor(R.color.colorBlueMove))
                for (more in kono.getAvailableMoves(button)) {
                    findViewById<Button>(more).setBackgroundColor(resources.getColor(R.color.colorBlueMove))
                }
                return true;
            }
        } else {
            val y = kono.getYPos(button)
            val x = kono.getXPos(button)
            if (kono.gameBoard[y][x] == Color.RED.rgb) {
                button.setBackgroundColor(resources.getColor(R.color.colorRedMove))
                for (more in kono.getAvailableMoves(button)) {
                    findViewById<Button>(more).setBackgroundColor(resources.getColor(R.color.colorRedMove))
                }
                return true;
            }
        }
        return false;
    }

    private fun finalizeMove() {
        playerOneTurn = !playerOneTurn
        if (!kono.isGameOver()) {
            currentlyTurn()
        } else {
            hasGameStarted = false
            findViewById<Button>(R.id.start_game).text = "Start"
            findViewById<TextView>(R.id.currentPlayer).text = "Game over!"
            // idk. Celebrate or something
        }
    }

    private fun setGameButtonColor(button: Button) {
        val y = kono.getYPos(button)
        val x = kono.getXPos(button)

        when {
            kono.gameBoard[y][x] == Color.WHITE.rgb -> { // white
                button.setBackgroundColor(resources.getColor(R.color.colorWhite))
            }
            kono.gameBoard[y][x] == Color.BLUE.rgb -> { // blue
                button.setBackgroundColor(resources.getColor(R.color.colorBlue))
            }
            else -> { //red
                button.setBackgroundColor(resources.getColor(R.color.colorRed))
            }
        }
    }

    private fun resetMove() {
        lastButtonClicked = -1
    }

    private fun Array<IntArray>.deepCopy() = Array(size) { get(it).clone() }

    private fun wireStartButton() {
        val startButton = findViewById<Button>(R.id.start_game)
        startButton.setOnClickListener {
            hasGameStarted = true
            gameUID += 1
            kono.gameBoard = kono.startBoard.deepCopy()
            lastButtonClicked = -1;
            playerOneTurn = true
            colorBoard()
            currentlyTurn()
            startButton.text = "restart"
            invokeAI()
        }
    }

    private fun invokeAI() {
        if (firstAI && playerOneTurn || !playerOneTurn && secondAI) {

            var lastUID = gameUID
            Handler().postDelayed(
                {
                    if (hasGameStarted && lastUID == gameUID) {
                        kono.doAIMove(playerOneTurn, isSwitchToggeled)
                        finalizeMove()
                        colorBoard()
                        invokeAI()
                        // This method will be executed once the timer is over
                    }
                },
                100 // value in milliseconds
            )
        }
    }

    private fun colorBoard() {
        for (buttonArray in kono.buttonMatrix) {
            for (buttonId in buttonArray) {
                val button = findViewById<Button>(buttonId)
                setGameButtonColor(button)
            }
        }
    }

    private fun currentlyTurn() {
        val currentTurn: TextView = findViewById(R.id.currentPlayer)
        if (Build.VERSION.SDK_INT >= 23) {
            val toolbar: Toolbar? = findViewById(R.id.my_toolbar)
            if (playerOneTurn == !isSwitchToggeled) {
                toolbar?.setBackgroundColor(resources.getColor(R.color.colorBlueMove))
            } else {
                toolbar?.setBackgroundColor(resources.getColor(R.color.colorRedMove))
            }
        }
        if (playerOneTurn == !isSwitchToggeled) {
            currentTurn.text = "currently blue's turn"
        } else {
            currentTurn.text = "currently red's turn"
        }
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
                selectedMode = id.toInt()
                Log.d(TAG, "spinner onItemSelected $id")
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

        when (this.selectedMode) {
            1 -> {
                if (this.isSwitchToggeled) {
                    switch.text = "red AI"
                    firstAI = true
                    secondAI = false
                } else {
                    switch.text = "blue player"
                    firstAI = false
                    secondAI = true
                }
            }
            2 -> {
                if (this.isSwitchToggeled) {
                    switch.text = "red AI"
                } else {
                    switch.text = "blue AI"
                }
                secondAI = true
                firstAI = true
            }
            else -> {
                if (this.isSwitchToggeled) {
                    switch.text = "red Player"
                } else {
                    switch.text = "blue Player"
                }
                firstAI = false
                secondAI = false
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
        outState.putBoolean("firstAI", this.firstAI)
        outState.putBoolean("secondAI", this.secondAI)
        outState.putBoolean("isSwitchToggeled", this.isSwitchToggeled)
        outState.putInt("selectedMode", this.selectedMode)
        outState.putBoolean("hasGameStarted", this.hasGameStarted)
        outState.putBoolean("playerOneTurn", this.playerOneTurn)
        outState.putIntArray("arr1", kono.gameBoard[0])
        outState.putIntArray("arr2", kono.gameBoard[1])
        outState.putIntArray("arr3", kono.gameBoard[2])
        outState.putIntArray("arr4", kono.gameBoard[3])
        outState.putIntArray("arr5", kono.gameBoard[4])
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "lifecycle onRestoreInstanceState")
        kono = konoGame()

        this.firstAI = savedInstanceState.get("firstAI") as Boolean
        this.secondAI = savedInstanceState.get("secondAI") as Boolean
        this.isSwitchToggeled = savedInstanceState.get("isSwitchToggeled") as Boolean
        this.selectedMode = savedInstanceState.get("selectedMode") as Int
        this.hasGameStarted = savedInstanceState.get("hasGameStarted") as Boolean
        this.playerOneTurn = savedInstanceState.get("playerOneTurn") as Boolean
        kono.gameBoard[0] = savedInstanceState.get("arr1") as IntArray
        kono.gameBoard[1] = savedInstanceState.get("arr2") as IntArray
        kono.gameBoard[2] = savedInstanceState.get("arr3") as IntArray
        kono.gameBoard[3] = savedInstanceState.get("arr4") as IntArray
        kono.gameBoard[4] = savedInstanceState.get("arr5") as IntArray
        kono.initializeButtonMatrix()
        this.colorBoard()
        this.currentlyTurn()

        val startButton = findViewById<Button>(R.id.start_game)
        if (this.hasGameStarted) {
            startButton.text = "restart"
        }

    }

}
