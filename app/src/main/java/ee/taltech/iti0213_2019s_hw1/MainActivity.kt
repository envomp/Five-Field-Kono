package ee.taltech.iti0213_2019s_hw1

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private var tempBoard = arrayOf(intArrayOf())
        private var gameUID = 0
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

    enum class Color(val rgb: Int) {
        RED(2),
        WHITE(0),
        BLUE(1)
    }

    private val startBoard = arrayOf(
        intArrayOf(1, 1, 1, 1, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(2, 0, 0, 0, 2),
        intArrayOf(2, 2, 2, 2, 2)
    )
    private val AIdepth = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "lifecycle onCreate")

        initializeButtonMatrix()
        createSpinner()
        createSwitch()
        rewriteTemplates()
        wireGameButtons()
        wireStartButton()
    }

    private fun wireGameButtons() {
        for (buttonArray in this.buttonMatrix) {
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
                    getAvailableMoves(findViewById(lastButtonClicked)).contains(buttonId) -> {
                        doMove(button)
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

    private fun isGameOver(): Boolean {
        return gameBoard[0][0] == Color.RED.rgb &&
                gameBoard[0][1] == Color.RED.rgb &&
                gameBoard[0][2] == Color.RED.rgb &&
                gameBoard[0][3] == Color.RED.rgb &&
                gameBoard[0][4] == Color.RED.rgb &&
                gameBoard[1][0] == Color.RED.rgb &&
                gameBoard[1][4] == Color.RED.rgb ||
                gameBoard[4][0] == Color.BLUE.rgb &&
                gameBoard[4][1] == Color.BLUE.rgb &&
                gameBoard[4][2] == Color.BLUE.rgb &&
                gameBoard[4][3] == Color.BLUE.rgb &&
                gameBoard[4][4] == Color.BLUE.rgb &&
                gameBoard[3][0] == Color.BLUE.rgb &&
                gameBoard[3][4] == Color.BLUE.rgb

    }

    private fun isSelectedsTurn(button: Button): Boolean {
        if (playerOneTurn == !isSwitchToggeled) {
            val y = getYPos(button)
            val x = getXPos(button)
            if (gameBoard[y][x] == Color.BLUE.rgb) {
                button.setBackgroundColor(resources.getColor(R.color.colorBlueMove))
                for (more in getAvailableMoves(button)) {
                    findViewById<Button>(more).setBackgroundColor(resources.getColor(R.color.colorBlueMove))
                }
                return true;
            }
        } else {
            val y = getYPos(button)
            val x = getXPos(button)
            if (gameBoard[y][x] == Color.RED.rgb) {
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
            gameBoard[y][x] = Color.BLUE.rgb
        } else {
            val y = getYPos(button)
            val x = getXPos(button)
            gameBoard[y][x] = Color.RED.rgb
        }
        val lastY = getYPos(findViewById(lastButtonClicked))
        val lastX = getXPos(findViewById(lastButtonClicked))
        gameBoard[lastY][lastX] = Color.WHITE.rgb
        lastButtonClicked = -1

        finalizeMove()
    }

    private fun finalizeMove() {
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

    private fun getAvailableMoves(y: Int, x: Int): ArrayList<IntArray> {

        val moves = arrayListOf<IntArray>()

        if (x > 0 && y > 0 && tempBoard[y - 1][x - 1] == 0) {
            moves.add(intArrayOf(y - 1, x - 1))
        }
        if (x > 0 && y + 1 < 5 && tempBoard[y + 1][x - 1] == 0) {
            moves.add(intArrayOf(y + 1, x - 1))
        }
        if (x + 1 < 5 && y + 1 < 5 && tempBoard[y + 1][x + 1] == 0) {
            moves.add(intArrayOf(y + 1, x + 1))
        }
        if (x + 1 < 5 && y > 0 && tempBoard[y - 1][x + 1] == 0) {
            moves.add(intArrayOf(y - 1, x + 1))
        }

        return moves
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
            this.gameBoard[y][x] == Color.WHITE.rgb -> { // white
                button.setBackgroundColor(resources.getColor(R.color.colorWhite))
            }
            this.gameBoard[y][x] == Color.BLUE.rgb -> { // blue
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
            gameUID += 1
            gameBoard = startBoard.deepCopy()
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
                        doAIMove()
                        invokeAI()
                        // This method will be executed once the timer is over
                    }
                },
                100 // value in milliseconds
            )
        }
    }

    private fun minMax(depth: Int, isBlueTurn: Boolean): Int {

        if (depth == 0) {

            val endPositionsBlue = blueWeights()
            val endPositionsRed = redWeights()

            return if (!isBlueTurn) endPositionsBlue else endPositionsRed

        } else {
            var curBestInDepth = if (isBlueTurn) -100000 else 1000000
            for (y in 0..4) {
                for (x in 0..4) {
                    if (tempBoard[y][x] == (if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb)) {
                        for (move in getAvailableMoves(y, x)) {
                            // do move
                            tempBoard[move[0]][move[1]] = if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb
                            tempBoard[y][x] = 0

                            // recursion
                            val temp = minMax(depth - 1, !isBlueTurn)

                            if (isBlueTurn) {
                                curBestInDepth = kotlin.math.max(temp, curBestInDepth)
                            } else {
                                curBestInDepth = min(temp, curBestInDepth)
                            }

                            // undo move
                            tempBoard[y][x] = if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb
                            tempBoard[move[0]][move[1]] = 0
                        }
                    }
                }
            }
            return curBestInDepth
        }
    }

    private fun blueWeights(): Int {
        var endPositionsBlue1 = 0
        for (pos in tempBoard[4]) {
            if (pos == Color.BLUE.rgb) {
                endPositionsBlue1 += 50
            }
        }
        if (tempBoard[3][0] == Color.BLUE.rgb) {
            endPositionsBlue1 += 46
        }
        if (tempBoard[3][4] == Color.BLUE.rgb) {
            endPositionsBlue1 += 46
        }
        for (pos in tempBoard[3]) {
            if (pos == Color.BLUE.rgb) {
                endPositionsBlue1 += 4
            }
        }
        for (pos in tempBoard[2]) {
            if (pos == Color.BLUE.rgb) {
                endPositionsBlue1 += 3
            }
        }
        for (pos in tempBoard[1]) {
            if (pos == Color.BLUE.rgb) {
                endPositionsBlue1 += 2
            }
        }
        for (pos in tempBoard[0]) {
            if (pos == Color.BLUE.rgb) {
                endPositionsBlue1 += 1
            }
        }
        return endPositionsBlue1
    }

    private fun redWeights(): Int {
        var endPositionsBlue1 = 0
        for (pos in tempBoard[0]) {
            if (pos == Color.RED.rgb) {
                endPositionsBlue1 += 50
            }
        }
        if (tempBoard[1][0] == Color.RED.rgb) {
            endPositionsBlue1 += 46
        }
        if (tempBoard[1][4] == Color.RED.rgb) {
            endPositionsBlue1 += 46
        }
        for (pos in tempBoard[1]) {
            if (pos == Color.RED.rgb) {
                endPositionsBlue1 += 4
            }
        }
        for (pos in tempBoard[2]) {
            if (pos == Color.RED.rgb) {
                endPositionsBlue1 += 3
            }
        }
        for (pos in tempBoard[3]) {
            if (pos == Color.RED.rgb) {
                endPositionsBlue1 += 2
            }
        }
        for (pos in tempBoard[4]) {
            if (pos == Color.RED.rgb) {
                endPositionsBlue1 += 1
            }
        }
        return endPositionsBlue1
    }

    private fun doAIMove() {
        tempBoard = gameBoard.deepCopy()
        val isBlueTurn = playerOneTurn == !isSwitchToggeled
        var maxScore = -10000
        var aIChoiceY = -1
        var aIChoiceX = -1
        var aIBestMove = 0

        for (y in 0..4) {
            for (x in 0..4) {
                if (tempBoard[y][x] == (if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb)) {
                    var moveCounter = 0
                    for (move in getAvailableMoves(y, x)) {
                        // do move
                        tempBoard[move[0]][move[1]] =
                            if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb
                        tempBoard[y][x] = 0
                        // calculate value
                        val temp = minMax(AIdepth, isBlueTurn)
                        if (temp >= maxScore) {
                            maxScore = temp
                            aIBestMove = moveCounter
                            aIChoiceY = y
                            aIChoiceX = x
                        }
                        // undo move
                        tempBoard[y][x] = if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb
                        tempBoard[move[0]][move[1]] = 0
                        moveCounter += 1
                    }
                }
            }
        }

        val move = getAvailableMoves(aIChoiceY, aIChoiceX)[aIBestMove]

        // make move
        gameBoard[move[0]][move[1]] = if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb
        gameBoard[aIChoiceY][aIChoiceX] = Color.WHITE.rgb
        finalizeMove()
        colorBoard()
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
        outState.putIntArray("arr1", this.gameBoard[0])
        outState.putIntArray("arr2", this.gameBoard[1])
        outState.putIntArray("arr3", this.gameBoard[2])
        outState.putIntArray("arr4", this.gameBoard[3])
        outState.putIntArray("arr5", this.gameBoard[4])
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "lifecycle onRestoreInstanceState")

        this.firstAI = savedInstanceState.get("firstAI") as Boolean
        this.secondAI = savedInstanceState.get("secondAI") as Boolean
        this.isSwitchToggeled = savedInstanceState.get("isSwitchToggeled") as Boolean
        this.selectedMode = savedInstanceState.get("selectedMode") as Int
        this.hasGameStarted = savedInstanceState.get("hasGameStarted") as Boolean
        this.playerOneTurn = savedInstanceState.get("playerOneTurn") as Boolean
        this.gameBoard[0] = savedInstanceState.get("arr1") as IntArray
        this.gameBoard[1] = savedInstanceState.get("arr2") as IntArray
        this.gameBoard[2] = savedInstanceState.get("arr3") as IntArray
        this.gameBoard[3] = savedInstanceState.get("arr4") as IntArray
        this.gameBoard[4] = savedInstanceState.get("arr5") as IntArray

        this.colorBoard()
        this.currentlyTurn()

        val startButton = findViewById<Button>(R.id.start_game)
        if (this.hasGameStarted) {
            startButton.text = "restart"
        }

    }

}
