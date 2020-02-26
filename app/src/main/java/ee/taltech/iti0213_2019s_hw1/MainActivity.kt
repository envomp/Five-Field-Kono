package ee.taltech.iti0213_2019s_hw1

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    private var firstAI = false;
    private var secondAI = false;
    private var isSwitchToggeled = false
    private var selectedMode = 0
    private var hasGameStarted = false;
    private var lastButtonClicked = -1;
    private var playerOneTurn = true
    private var gameBoard = arrayOf(
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0)
    )
    private var buttonMatrix = arrayOf(IntArray(0));
    /**
     * Blue is 1
     * Red is 2
     * Empty board is 0
     * **/
    private val startBoard = arrayOf(
        intArrayOf(1, 1, 1, 1, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(2, 0, 0, 0, 2),
        intArrayOf(2, 2, 2, 2, 2)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "lifecycle onCreate")

        initializeButtonMatrix()
        createSpinner()
        createSwitch()
        rewriteTemplates()
        wireStartButton()
        wireGameButtons()
    }

    private fun wireGameButtons() {
        for (buttonArray in this.buttonMatrix) {
            for (buttonId in buttonArray) {
                val button = findViewById<Button>(buttonId)
                button.setOnClickListener {
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
                                getAvailableMoves(findViewById(lastButtonClicked)).contains(buttonId) -> {
                                    doMove(button)
                                    colorBoard()
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
                button.text = ""
            }
        }
    }

    private fun isGameOver(): Boolean {
        return gameBoard[0][0] == 2 &&
                gameBoard[0][1] == 2 &&
                gameBoard[0][2] == 2 &&
                gameBoard[0][3] == 2 &&
                gameBoard[0][4] == 2 &&
                gameBoard[1][0] == 2 &&
                gameBoard[1][4] == 2 ||
                gameBoard[4][0] == 1 &&
                gameBoard[4][1] == 1 &&
                gameBoard[4][2] == 1 &&
                gameBoard[4][3] == 1 &&
                gameBoard[4][4] == 1 &&
                gameBoard[3][0] == 1 &&
                gameBoard[3][4] == 1

    }

    private fun isSelectedsTurn(button: Button): Boolean {
        if (playerOneTurn == !isSwitchToggeled) {
            val y = getYPos(button)
            val x = getXPos(button)
            if (gameBoard[y][x] == 1) {
                button.setBackgroundColor(resources.getColor(R.color.colorBlueMove))
                for (more in getAvailableMoves(button)) {
                    findViewById<Button>(more).setBackgroundColor(resources.getColor(R.color.colorBlueMove))
                }
                return true;
            }
        } else {
            val y = getYPos(button)
            val x = getXPos(button)
            if (gameBoard[y][x] == 2) {
                button.setBackgroundColor(resources.getColor(R.color.colorRedMove))
                for (more in getAvailableMoves(button)) {
                    findViewById<Button>(more).setBackgroundColor(resources.getColor(R.color.colorRedMove))
                }
                return true;
            }
        }
        return false;
    }

    private fun doMove(button: Button) {
        if (playerOneTurn == !isSwitchToggeled) {
            val y = getYPos(button)
            val x = getXPos(button)
            gameBoard[y][x] = 1
        } else {
            val y = getYPos(button)
            val x = getXPos(button)
            gameBoard[y][x] = 2
        }
        val lastY = getYPos(findViewById(lastButtonClicked))
        val lastX = getXPos(findViewById(lastButtonClicked))
        gameBoard[lastY][lastX] = 0
        lastButtonClicked = -1

        playerOneTurn = !playerOneTurn
        if (!isGameOver()) {
            currentlyTurn()
        } else {
            hasGameStarted = false
            findViewById<Button>(R.id.start_game).text = "Start"
            findViewById<TextView>(R.id.currentPlayer).text = "Game over!"
            // idk. Celebrate or something
        }
    }

    private fun getAvailableMoves(button: Button): ArrayList<Int> {
        val y = getYPos(button)
        val x = getXPos(button)

        val moves = arrayListOf<Int>()

        if (x > 0 && y > 0 && this.gameBoard[y - 1][x - 1] == 0) {
            moves.add(this.buttonMatrix[y - 1][x - 1])
        }
        if (x > 0 && y + 1 < 5 && this.gameBoard[y + 1][x - 1] == 0) {
            moves.add(this.buttonMatrix[y + 1][x - 1])
        }
        if (x + 1 < 5 && y + 1 < 5 && this.gameBoard[y + 1][x + 1] == 0) {
            moves.add(this.buttonMatrix[y + 1][x + 1])
        }
        if (x + 1 < 5 && y > 0 && this.gameBoard[y - 1][x + 1] == 0) {
            moves.add(this.buttonMatrix[y - 1][x + 1])
        }

        return moves
    }

    private fun initializeButtonMatrix() {
        buttonMatrix = arrayOf(
            intArrayOf(
                R.id.btn_1_1,
                R.id.btn_1_2,
                R.id.btn_1_3,
                R.id.btn_1_4,
                R.id.btn_1_5
            ), intArrayOf(
                R.id.btn_2_1,
                R.id.btn_2_2,
                R.id.btn_2_3,
                R.id.btn_2_4,
                R.id.btn_2_5
            ), intArrayOf(
                R.id.btn_3_1,
                R.id.btn_3_2,
                R.id.btn_3_3,
                R.id.btn_3_4,
                R.id.btn_3_5
            ), intArrayOf(
                R.id.btn_4_1,
                R.id.btn_4_2,
                R.id.btn_4_3,
                R.id.btn_4_4,
                R.id.btn_4_5
            ), intArrayOf(
                R.id.btn_5_1,
                R.id.btn_5_2,
                R.id.btn_5_3,
                R.id.btn_5_4,
                R.id.btn_5_5
            )
        );
    }

    private fun setGameButtonColor(button: Button) {
        val y = getYPos(button)
        val x = getXPos(button)

        when {
            this.gameBoard[y][x] == 0 -> { // white
                button.setBackgroundColor(resources.getColor(R.color.colorWhite))
            }
            this.gameBoard[y][x] == 1 -> { // blue
                button.setBackgroundColor(resources.getColor(R.color.colorBlue))
            }
            else -> { //red
                button.setBackgroundColor(resources.getColor(R.color.colorRed))
            }
        }
    }

    private fun getXPos(button: Button): Int {
        when {
            intArrayOf(
                R.id.btn_1_1,
                R.id.btn_2_1,
                R.id.btn_3_1,
                R.id.btn_4_1,
                R.id.btn_5_1
            ).contains(button.id) -> {
                return 0;
            }
            intArrayOf(
                R.id.btn_1_2,
                R.id.btn_2_2,
                R.id.btn_3_2,
                R.id.btn_4_2,
                R.id.btn_5_2
            ).contains(button.id) -> {
                return 1;
            }
            intArrayOf(
                R.id.btn_1_3,
                R.id.btn_2_3,
                R.id.btn_3_3,
                R.id.btn_4_3,
                R.id.btn_5_3
            ).contains(button.id) -> {
                return 2;
            }
            intArrayOf(
                R.id.btn_1_4,
                R.id.btn_2_4,
                R.id.btn_3_4,
                R.id.btn_4_4,
                R.id.btn_5_4
            ).contains(button.id) -> {
                return 3;
            }
            intArrayOf(
                R.id.btn_1_5,
                R.id.btn_2_5,
                R.id.btn_3_5,
                R.id.btn_4_5,
                R.id.btn_5_5
            ).contains(button.id) -> {
                return 4;
            }
        }
        return -1;
    }

    private fun getYPos(button: Button): Int {
        when {
            buttonMatrix[0].contains(button.id) -> {
                return 0;
            }
            buttonMatrix[1].contains(button.id) -> {
                return 1;
            }
            buttonMatrix[2].contains(button.id) -> {
                return 2;
            }
            buttonMatrix[3].contains(button.id) -> {
                return 3;
            }
            buttonMatrix[4].contains(button.id) -> {
                return 4;
            }
        }
        return -1;
    }

    private fun resetMove() {
        lastButtonClicked = -1
    }

    private fun Array<IntArray>.deepCopy() = Array(size) { get(it).clone() }

    private fun wireStartButton() {
        val startButton = findViewById<Button>(R.id.start_game)
        startButton.setOnClickListener {
            hasGameStarted = true
            gameBoard = startBoard.deepCopy()
            lastButtonClicked = -1;
            playerOneTurn = true
            colorBoard()
            currentlyTurn()
            startButton.text = "restart"

            if (this.isSwitchToggeled && this.selectedMode >= 1 || !this.isSwitchToggeled && this.selectedMode >= 2) {
                // AI
                doAIMove()
                Log.d(TAG, "AI")
            } else {
                // Player
            }
        }
    }

    private fun doAIMove() {


        if (selectedMode == 3) {
            // reccursion
        }
    }

    private fun colorBoard() {
        for (buttonArray in this.buttonMatrix) {
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
