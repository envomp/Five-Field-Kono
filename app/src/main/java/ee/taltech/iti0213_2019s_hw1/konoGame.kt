package ee.taltech.iti0213_2019s_hw1

import kotlin.math.min
import android.widget.Button

class konoGame {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private var tempBoard = arrayOf(intArrayOf())
    }

    var buttonMatrix = arrayOf(IntArray(0))

    var gameBoard = arrayOf(
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0)
    )

    val startBoard = arrayOf(
        intArrayOf(1, 1, 1, 1, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(2, 0, 0, 0, 2),
        intArrayOf(2, 2, 2, 2, 2)
    )

    val AIdepth = 5

    enum class Color(val rgb: Int) {
        RED(2),
        WHITE(0),
        BLUE(1)
    }

    private fun Array<IntArray>.deepCopy() = Array(size) { get(it).clone() }

    fun initializeButtonMatrix() {
        this.buttonMatrix = arrayOf(
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

    fun doMove(button: Button, playerOneTurn: Boolean, isSwitchToggeled: Boolean) {
        if (playerOneTurn == !isSwitchToggeled) {
            val y = getYPos(button)
            val x = getXPos(button)
            gameBoard[y][x] = Color.BLUE.rgb
        } else {
            val y = getYPos(button)
            val x = getXPos(button)
            gameBoard[y][x] = Color.RED.rgb
        }
    }

    fun isGameOver(): Boolean {
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

    fun getAvailableMoves(y: Int, x: Int): ArrayList<IntArray> {

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

    fun getAvailableMoves(button: Button): ArrayList<Int> {
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

    fun getXPos(button: Button): Int {
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

    fun getYPos(button: Button): Int {
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

    fun doAIMove(playerOneTurn: Boolean, isSwitchToggeled: Boolean) {
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
                            tempBoard[move[0]][move[1]] =
                                if (isBlueTurn) Color.BLUE.rgb else Color.RED.rgb
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

}