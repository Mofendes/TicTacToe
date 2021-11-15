package com.example.tictactoe.View


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tictactoe.R
import com.example.tictactoe.ViewModel.TicTacViewModel
import com.example.tictactoe.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {



    // CLG - 2021-11-13 - Añado viewBinding
    private lateinit var binding:ActivityMainBinding

    private val buttons = Array(3) {
        arrayOfNulls<Button>(
            3
        )
    }

    private var player1Turn = true
    private var playing = true

    private var roundCount = 1

    private var player1Points = 0
    private var player2Points = 0

    private var textViewPlayer1: TextView? = null
    private var textViewPlayer2: TextView? = null


    // CLG ---
    private lateinit var campo: Array<Array<Int>>
    private lateinit var viewModel: TicTacViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(TicTacViewModel::class.java)
        // CLG - 2021-11-13 - Añado ViewModel
        //var viewModel = ViewModelProvider(this).get(TicTacViewModel::class.java)


        player1Points = viewModel.model.jugadorWins
        player2Points = viewModel.model.botWins

                // CLG - 2021-11-13 - Añado viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_main)
        textViewPlayer1 = findViewById(R.id.text_view_p1)
        textViewPlayer2 = findViewById(R.id.text_view_p2)

        // CLG - 2021-11-13 - Añado inicialización de los textos para la primera vez que arranca la app.
        updatePointsText()

        for (i in 0..2) {
            for (j in 0..2) {

                campo =  viewModel.model.getFieldd()

                val buttonID = "button_$i$j"
                val resID = resources.getIdentifier(buttonID, "id", packageName)
                buttons[i][j] = findViewById(resID)
                buttons[i][j]?.text = "" // CLG - 2021-11-13 - Añado inicialización de los textos para la primera vez que arranca la app.
                buttons[i][j]?.setOnClickListener(this)


            }
        }
        // La función mapea el texto de los botones en función del array 2D Int del ViewModel
        mapeaBotones(viewModel.model.field)

        val buttonReset = findViewById<Button>(R.id.button_reset)
        buttonReset.setOnClickListener { resetGame(viewModel) }


        val j = GFG.findBestMove(fieldToBoard()).col
        val i = GFG.findBestMove(fieldToBoard()).row
        buttons[i][j]?.setText("X")

        }


    override fun onClick(v: View) {
        if ((v as Button).text.toString() != "" || !playing) {
            if(!playing){
                playing = true
                resetBoard()
            }
            return
        }

        jugadaP1(v)

        if(checkForWin()){
            player1Wins()
            return
        }else if(roundCount == 9){
            draw()
            return
        }

        jugadaBot()

        if(checkForWin()){
            player2Wins()
            return
        }else if(roundCount == 9){
            draw()
            return
        }

    }


    /**
     * funciones static del minimax
     */
    companion object GFG{
        var player = 1
        var opponent = 2
        //sin marcar = 0

        //Esta funcion devuelve true si quedan movimientos en el tablero y false si no.
        fun isMovesLeft(board: Array<Array<Int>>): Boolean {
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == 0) {
                        return true
                    }
                }
            }
            return false
        }

        // esta funcion devuelve 10 si gana el jugador 1, -10 si pierde el jugador 1 o 0 si ninguno gana
        fun evaluate(b:  Array<Array<Int>>): Int {
            // mira si hay victoria posible en las filas
            for (row in 0..2) {
                if (b[row][0] == b[row][1]
                    && b[row][1] == b[row][2]
                ) {
                    if (b[row][0] == player) {
                        return +10
                    } else if (b[row][0] == opponent) {
                        return -10
                    }
                }
            }

            // victoria posible en las columnas
            for (col in 0..2) {
                if (b[0][col] == b[1][col]
                    && b[1][col] == b[2][col]
                ) {
                    if (b[0][col] == player) {
                        return +10
                    } else if (b[0][col] == opponent) {
                        return -10
                    }
                }
            }

            // victoria posible en la diagonal
            if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
                if (b[0][0] == player) {
                    return +10
                } else if (b[0][0] == opponent) {
                    return -10
                }
            }
            if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
                if (b[0][2] == player) {
                    return +10
                } else if (b[0][2] == opponent) {
                    return -10
                }
            }

            // si no gana nadie devuelve 0
            return 0
        }

        // Algoritmo minimax
        fun minimax(
            board: Array<Array<Int>>,
            depth: Int, isMax: Boolean
        ): Int {
            var best: Int
            val score = evaluate(board)
            if (score == 10) {
                return score
            }
            if (score == -10) {
                return score
            }
            if (isMovesLeft(board) == false) {
                return 0
            }
            if (isMax) {
                best = -1000
                for (i in 0..2) {
                    for (j in 0..2) {
                        if (board[i][j] == 0) {
                            board[i][j] = player
                            best = Math.max(
                                best, minimax(
                                    board,
                                    depth + 1, !isMax
                                )
                            )
                            board[i][j] = 0
                        }
                    }
                }
                return best
            } // If this minimizer's move
            else {
                best = 1000
                // Traverse all cells
                for (i in 0..2) {
                    for (j in 0..2) {
                        // Check if cell is empty
                        if (board[i][j] == 0) {
                            // Make the move
                            board[i][j] = opponent
                            // Call minimax recursively and choose
                            // the minimum value
                            best = Math.min(
                                best, minimax(
                                    board,
                                    depth + 1, !isMax
                                )
                            )
                            // Undo the move
                            board[i][j] = 0
                        }
                    }
                }
                return best
            }
        }

        // devuelve la mejor posicion posible
        fun findBestMove(board: Array<Array<Int>>): Coord {
            var bestVal = -1000
            val bestMove = Coord()
            bestMove.row = -1
            bestMove.col = -1

            /**
             * Traverse all cells, evaluate minimax function
             * for all empty cells. And return the cell
             * with optimal value.
             */
            for (i in 0..2) {
                for (j in 0..2) {
                    // Check if cell is empty
                    if (board[i][j] == 0) {
                        // Make the move
                        board[i][j] = player
                        // compute evaluation function for this
                        // move.
                        val moveVal = minimax(board, 0, false)
                        // Undo the move
                        board[i][j] = 0
                        /**
                         * If the value of the current move is
                         * more than the best value, then updatebest
                         * */

                        if (moveVal > bestVal) {
                            bestMove.row = i
                            bestMove.col = j
                            bestVal = moveVal
                        }
                    }
                }
            }
            System.out.printf(
                "The value of the best Move "
                        + "is : %d\n\n", bestVal
            )
            return bestMove
        }

        class Coord() {
            var row = 0
            var col = 0
        }

    } // Fin del companion object


    private fun jugadaP1(v: View) {
        (v as Button).text.toString()
        v.setText("O")
        //roundCount++
        viewModel.model.roundCount++

    }

    private fun jugadaBot() {
        val j = GFG.findBestMove(fieldToBoard()).col
        val i = GFG.findBestMove(fieldToBoard()).row
        buttons[i][j]?.setText("X")
        //roundCount++
        viewModel.model.roundCount++
    }

    private fun checkForWin(): Boolean {
        val field = retField()

        for (i in 0..2) {
            if (field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] != "") {
                return true
            }
        }
        for (i in 0..2) {
            if (field[0][i] == field[1][i] && field[0][i] == field[2][i] && field[0][i] != "") {
                return true
            }
        }
        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != "") {
            return true
        }
        return field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2] != ""
    }

     private fun player1Wins() {
        //player1Points++
        viewModel.model.jugadorWins++
        updatePointsText()
        playing = false
        Toast.makeText(this, "PLAYER 1 WINS!", Toast.LENGTH_SHORT).show()
    }

    private fun player2Wins() {
        player2Points++
        updatePointsText()
        playing = false
        Toast.makeText(this, "BOT WINS!", Toast.LENGTH_SHORT).show()
    }

    private fun draw() {
        playing = false
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun updatePointsText(viewModel: TicTacViewModel) {
        //textViewPlayer1!!.text = "Player 1: $player1Points"
        textViewPlayer1!!.text = "Player 1: $viewModel.model.jugadorWins"
        //textViewPlayer2!!.text = "BOT: $player2Points"
        textViewPlayer2!!.text = "BOT: $viewModel.model.botWins"
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]!!.text = ""
            }
        }
        roundCount = 0
        player1Turn = true
        playing = true
    }

    private fun resetGame() {
        //player1Points = 0
        //player2Points = 0
        viewModel.model.resetPunts()
        updatePointsText()
        resetBoard()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("roundCount", viewModel.model.roundCount)
        outState.putInt("player1Points", player1Points)
        outState.putInt("player2Points", player2Points)
        outState.putBoolean("player1Turn", player1Turn)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        roundCount = savedInstanceState.getInt("roundCount")
        player1Points = savedInstanceState.getInt("player1Points")
        player2Points = savedInstanceState.getInt("player2Points")
        player1Turn = savedInstanceState.getBoolean("player1Turn")
    }




    //COMPATIBILIDAD DEL JUEGO AL MIXIMAX

    fun fieldToBoard(): Array<Array<Int>> {
        // inicializa un array 2D de caracterss
        val board  = arrayOf(
            arrayOf(0, 0, 0),
            arrayOf(0, 0, 0),
            arrayOf(0, 0, 0)
        )
        /**
        val board = arrayOf(
            charArrayOf('_','_', '_'
            ),
            charArrayOf('_','_','_'
            ),
            charArrayOf('_','_', '_'
            )
        )
        */
        // Obtiene el actual "board" de los textos de los botones
        val field = retField()

        // Actualizo mi Array en función de los textos de los botones.
        for (i in 0 until field.size) {
            for (j in 0 until field.get(0).size) {
                if (field[i][j] == "X"){
                    board[i][j] = 1
                }else if (field[i][j] == "O"){
                    board[i][j] = 2
                }
            }
        }


        return board
    }

    // Mapea los botones a un array de strings
    fun retField(): Array<Array<String?>> {
        val field = Array(3) {
            arrayOfNulls<String>(
                3
            )
        }

        for (i in 0..2) {
            for (j in 0..2) {
                field[i][j] = buttons[i][j]!!.text.toString()
            }
        }
        return field
    }

    fun mapeaBotones(mapa: Array<Array<Int>>){
        for (i in 0..2) {
            for (j in 0..2) {
                //mapa[i][j] = buttons[i][j]!!.text.toString()

                    if (mapa[i][j] == 1){
                        buttons[i][j]!!.text = "X";
                    }else if(mapa[i][j] == 2){
                        buttons[i][j]!!.text = "O"
                    }else{
                        buttons[i][j]!!.text = ""
                    }

            }
        }
    }


}

