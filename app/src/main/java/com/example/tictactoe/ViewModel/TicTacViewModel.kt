package com.example.tictactoe.ViewModel

import androidx.lifecycle.ViewModel
import com.example.tictactoe.Model.TicTacModel

class TicTacViewModel: ViewModel() {

    // generamos un objeto TicTacModel con el mapa inicializado a 0's
    var model: TicTacModel = TicTacModel(0,0,0)


}