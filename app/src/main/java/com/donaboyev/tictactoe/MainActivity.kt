package com.donaboyev.tictactoe

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.donaboyev.tictactoe.Mode.*
import com.donaboyev.tictactoe.Util.KEY_MODE
import com.donaboyev.tictactoe.Util.NIGHT_MODE
import com.donaboyev.tictactoe.Util.SHARED_PREF_MODE
import com.donaboyev.tictactoe.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity(), BottomSheetFragment.ItemClickListener {

    private val buttons = Array(3) {
        arrayOfNulls<AppCompatImageButton>(
            3
        )
    }
    private var isPlayer1sTurn = true
    private var gameMode = EASY
    private var gameBoard: GameBoard? = null
    private var countUsingMinimaxInMedium = 0
    private var prefs: SharedPreferences? = null
    private var countYou = 0
    private var countCPU = 0
    private var countSteps = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSettings.setOnClickListener {
            supportFragmentManager.let {
                val bundle = Bundle()
                BottomSheetFragment.newInstance(bundle).apply {
                    show(it, tag)
                }
            }
        }

        prefs = getSharedPreferences(SHARED_PREF_MODE, MODE_PRIVATE)
        if (prefs?.getBoolean(NIGHT_MODE, false)!!) AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val mDrawable = binding.mainLayout.background as AnimationDrawable
        mDrawable.setEnterFadeDuration(3000)
        mDrawable.setExitFadeDuration(3000)
        mDrawable.start()

        loadBoard()
    }

    override fun onItemClick(mode: Mode) {
        val edit = getSharedPreferences(SHARED_PREF_MODE, MODE_PRIVATE).edit()
        when (mode) {
            EASY -> {
                Toast.makeText(this, "Easy", Toast.LENGTH_SHORT).show()
                edit.putInt(KEY_MODE, 0).apply()
                resetGame()
            }
            MEDIUM -> {
                Toast.makeText(this, "Medium", Toast.LENGTH_SHORT).show()
                edit.putInt(KEY_MODE, 1).apply()
                resetGame()
            }
            HARD -> {
                Toast.makeText(this, "Hard", Toast.LENGTH_SHORT).show()
                edit.putInt(KEY_MODE, 2).apply()
                resetGame()
            }
            TWO_PLAYERS -> {
                Toast.makeText(this, "Two players", Toast.LENGTH_SHORT).show()
                edit.putInt(KEY_MODE, 3).apply()
                resetGame()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadBoard() {
        gameBoard = GameBoard()
        for (i in buttons.indices) {
            for (j in buttons.indices) {
                val buttonId = "btn$i$j"
                val resId = resources.getIdentifier(buttonId, "id", packageName)
                buttons[i][j] = findViewById(resId)
                gameMode = Mode.fromInteger(prefs?.getInt(KEY_MODE, 0)!!)!!
                when (gameMode) {
                    EASY -> {
                        buttons[i][j]?.setOnClickListener(CellClickListenerInEasy(i, j))
                        binding.tvPlayer1.text = "You"
                        binding.tvPlayer2.text = "AI"
                    }
                    MEDIUM -> {
                        buttons[i][j]?.setOnClickListener(
                            CellClickListenerInMedium(
                                i,
                                j
                            )
                        )
                        binding.tvPlayer1.text = "You"
                        binding.tvPlayer2.text = "AI"
                    }
                    HARD -> {
                        buttons[i][j]?.setOnClickListener(CellClickListenerInHard(i, j))
                        binding.tvPlayer1.text = "You"
                        binding.tvPlayer2.text = "AI"
                    }
                    TWO_PLAYERS -> {
                        buttons[i][j]?.setOnClickListener(
                            CellClickListenerInTwoPlayers(i, j)
                        )
                        binding.tvPlayer1.text = "Player 1"
                        binding.tvPlayer2.text = "Player 2"
                    }
                }
            }
        }
    }

    private fun resetBoard() {
        gameBoard = GameBoard()
        countSteps = 0
        loadBoard()
        mapBoardToUi()
    }

    @SuppressLint("SetTextI18n")
    private fun resetGame() {
        countCPU = 0
        countYou = 0
        binding.tvScore.text = "$countYou - $countCPU"
        resetBoard()
    }

    private fun mapBoardToUi() {
        for (i in gameBoard!!.board.indices) {
            for (j in gameBoard!!.board.indices) {
                when (gameBoard!!.board[i][j]) {
                    GameBoard.PLAYER -> {
                        buttons[i][j]!!.setImageResource(R.drawable.iks)
                        buttons[i][j]!!.isEnabled = false
                    }
                    GameBoard.COMPUTER -> {
                        buttons[i][j]!!.setImageResource(R.drawable.circle)
                        buttons[i][j]!!.isEnabled = false
                    }
                    else -> {
                        buttons[i][j]!!.setImageResource(R.drawable.empty)
                        buttons[i][j]!!.isEnabled = true
                    }
                }
            }
        }
    }

    internal inner class CellClickListenerInTwoPlayers(
        private val i: Int, private val j: Int
    ) : View.OnClickListener {
        @SuppressLint("SetTextI18n")
        override fun onClick(v: View?) {
            if (!gameBoard!!.isGameOver) {
                val cell = Cell(i, j)
                if (isPlayer1sTurn) {
                    gameBoard!!.placeMove(cell, GameBoard.PLAYER)
                } else {
                    gameBoard!!.placeMove(cell, GameBoard.COMPUTER)
                }
                isPlayer1sTurn = !isPlayer1sTurn
                mapBoardToUi()
            }
            when {
                gameBoard!!.hasComputerWon() -> {
                    Toast.makeText(this@MainActivity, "Player 2 won", Toast.LENGTH_SHORT).show()
                    countCPU++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.hasPlayerWon() -> {
                    Toast.makeText(this@MainActivity, "Player 1 won", Toast.LENGTH_SHORT).show()
                    countYou++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.isGameOver -> {
                    Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                    resetBoard()
                }
            }
        }
    }

    internal inner class CellClickListenerInMedium(private val i: Int, private val j: Int) :
        View.OnClickListener {
        @SuppressLint("SetTextI18n")
        override fun onClick(v: View?) {
            if (!gameBoard!!.isGameOver) {
                val cell = Cell(i, j)
                gameBoard!!.placeMove(cell, GameBoard.PLAYER)
                if (gameBoard!!.hasComputerWon()) {
                    Toast.makeText(this@MainActivity, "Computer won", Toast.LENGTH_SHORT).show()
                    countUsingMinimaxInMedium = 0
                    countCPU++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                } else if (gameBoard!!.hasPlayerWon()) {
                    Toast.makeText(this@MainActivity, "Player won", Toast.LENGTH_SHORT).show()
                    countYou++
                    binding.tvScore.text = "$countYou - $countCPU"
                    countUsingMinimaxInMedium = 0
                    resetBoard()
                } else if (gameBoard!!.isGameOver) {
                    Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                    countUsingMinimaxInMedium = 0
                    resetBoard()
                } else {
                    if (countUsingMinimaxInMedium < 2) {
                        gameBoard!!.minimax(0, GameBoard.COMPUTER)
                        gameBoard!!.placeMove(gameBoard!!.computersMove!!, GameBoard.COMPUTER)
                        countUsingMinimaxInMedium++
                    } else {
                        if (gameBoard!!.availableCells.isNotEmpty()) {
                            val randomCell = gameBoard!!.availableCells[Random.nextInt(
                                gameBoard!!.availableCells.size
                            )]
                            gameBoard!!.placeMove(randomCell, GameBoard.COMPUTER)
                        }
                    }
                }
                mapBoardToUi()
            }
            when {
                gameBoard!!.hasComputerWon() -> {
                    Toast.makeText(this@MainActivity, "Computer won", Toast.LENGTH_SHORT).show()
                    countUsingMinimaxInMedium = 0
                    countCPU++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.hasPlayerWon() -> {
                    Toast.makeText(this@MainActivity, "Player won", Toast.LENGTH_SHORT).show()
                    countUsingMinimaxInMedium = 0
                    countYou++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.isGameOver -> {
                    Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                    countUsingMinimaxInMedium = 0
                    resetBoard()
                }
            }
        }
    }

    internal inner class CellClickListenerInHard(private val i: Int, private val j: Int) :
        View.OnClickListener {
        @SuppressLint("SetTextI18n")
        override fun onClick(v: View?) {
            if (!gameBoard!!.isGameOver) {
                val cell = Cell(i, j)
                gameBoard!!.placeMove(cell, GameBoard.PLAYER)
                when {
                    gameBoard!!.hasComputerWon() -> {
                        Toast.makeText(this@MainActivity, "Computer won", Toast.LENGTH_SHORT).show()
                        countCPU++
                        binding.tvScore.text = "$countYou - $countCPU"
                        resetBoard()
                    }
                    gameBoard!!.hasPlayerWon() -> {
                        Toast.makeText(this@MainActivity, "Player won", Toast.LENGTH_SHORT).show()
                        countYou++
                        binding.tvScore.text = "$countYou - $countCPU"
                        resetBoard()
                    }
                    gameBoard!!.isGameOver -> {
                        Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                        resetBoard()
                    }
                    else -> {
                        gameBoard!!.minimax(0, GameBoard.COMPUTER)
                        gameBoard!!.placeMove(gameBoard!!.computersMove!!, GameBoard.COMPUTER)
                    }
                }
                mapBoardToUi()
            }
            when {
                gameBoard!!.hasComputerWon() -> {
                    Toast.makeText(this@MainActivity, "Computer won", Toast.LENGTH_SHORT).show()
                    countCPU++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.hasPlayerWon() -> {
                    Toast.makeText(this@MainActivity, "Player won", Toast.LENGTH_SHORT).show()
                    countYou++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.isGameOver -> {
                    Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                    resetBoard()
                }
            }
        }
    }

    internal inner class CellClickListenerInEasy(
        private val i: Int, private val j: Int
    ) : View.OnClickListener {
        @SuppressLint("SetTextI18n")
        override fun onClick(v: View?) {
            if (!gameBoard!!.isGameOver) {
                val cell = Cell(i, j)
                gameBoard!!.placeMove(cell, GameBoard.PLAYER)
                countSteps++
                if (gameBoard!!.hasComputerWon()) {
                    Toast.makeText(this@MainActivity, "AI won", Toast.LENGTH_SHORT).show()
                    countCPU++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                } else if (gameBoard!!.hasPlayerWon()) {
                    Toast.makeText(this@MainActivity, "You won", Toast.LENGTH_SHORT).show()
                    countYou++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                } else if (gameBoard!!.isGameOver) {
                    Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                    resetBoard()
                } else {
                    if (gameBoard!!.availableCells.isNotEmpty()) {
                        val randomCell = gameBoard!!.availableCells[Random.nextInt(
                            gameBoard!!.availableCells.size
                        )]
                        gameBoard!!.placeMove(randomCell, GameBoard.COMPUTER)
                    }
                }
                mapBoardToUi()
            }
            when {
                gameBoard!!.hasComputerWon() -> {
                    Toast.makeText(this@MainActivity, "Computer won", Toast.LENGTH_SHORT).show()
                    countCPU++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.hasPlayerWon() -> {
                    Toast.makeText(this@MainActivity, "Player won", Toast.LENGTH_SHORT).show()
                    countYou++
                    binding.tvScore.text = "$countYou - $countCPU"
                    resetBoard()
                }
                gameBoard!!.isGameOver -> {
                    Toast.makeText(this@MainActivity, "Game tied", Toast.LENGTH_SHORT).show()
                    resetBoard()
                }
            }
        }
    }

}