/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Chess.*;

/**
 *
 * @author decla_000
 */
public class Tile {
    int x;
    int y;
    Piece piece;
    
    public Tile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        piece = null;
    }
}
