/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Chess.*;
import java.util.List;

/**
 *
 * @author decla_000
 */
public class Game {
    private Board board = new Board();
    private List<Piece> pieces;
    private GamePlayer white;
    private GamePlayer black;
    public Game() {
        super();
    }
}