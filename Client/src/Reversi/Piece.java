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
public class Piece {
    public int team;
    public int x;
    public int y;

    public Piece(int a, int b, int team){
        x = a;
        y = b;
        this.team = team;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public boolean isValid(Board board, int startX, int startY, int endX, int endY){
        if (startX == endX && startY == endY){
            return false;
        }
        return true;
    }
}
