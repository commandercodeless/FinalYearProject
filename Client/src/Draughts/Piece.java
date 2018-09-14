/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Draughts;

import Chess.*;

/**
 *
 * @author decla_000
 */
public class Piece {
    public int pieceId;
    public boolean taken = false;
    public int team;
    public int x;
    public int y;
    public String name;
    
    public Piece(int id, int a, int b, int team){
        pieceId = id;
        x = a;
        y = b;
        this.team = team;
        name = "normal";
    }
    
    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
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
