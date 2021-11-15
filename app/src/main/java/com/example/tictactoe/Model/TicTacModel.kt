package com.example.tictactoe.Model

import android.widget.Button

data class TicTacModel(var jugadorWins: Int, var botWins: Int, var roundCount: Int){

    val field = arrayOf(
        arrayOf(0, 0, 0),
        arrayOf(0, 0, 0),
        arrayOf(0, 0, 0)
    )

    fun getFieldd(): Array<Array<Int>> {
        return this.field
    }

    fun printField(){
        for (i in 0 until field.size){
            for (j in 0 until field[i].size){
                if (j==field[i].size){
                    print(field[i][j].toString())
                }else{
                    print(field[i][j].toString()+"-")
                }

            }
            println("")
        }
    }
} // (val field: arrayOf<Array<Int>>(), val jugadorWins: Int, val botWins: Int)