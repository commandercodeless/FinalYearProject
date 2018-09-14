/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Chess.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author decla_000
 */
public class Board {
    public Tile[][] tiles = new Tile[8][8];
    
    public Board() {
        super();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                this.tiles[i][j] = new Tile(i, j);
            }
        }
    }
    
    public boolean validMoves(int team){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (checkValid(i,j,team)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getVictor(){
        int white = 0;
        int black = 0;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (tiles[i][j].piece != null){
                    if (tiles[i][j].piece.team == 0){
                        white++;
                    } else{
                        black++;
                    }
                }
            }
        }
        if (white == black){
            return -1;
        }
        if (white > black){
            return 0;
        }
        return 1;
    }

    public Tile getSpot(int x, int y) {
        return tiles[x][y];
    }
    
    public void add(Piece pieceToAdd){
        int x = pieceToAdd.x;
        int y = pieceToAdd.y;
        tiles[x][y].piece = pieceToAdd;
    }
    
    public boolean checkValid(int startx, int starty, int team){
        if(checkRow(startx, starty, startx, starty + 1, team)){
            return true;
        }
        if(checkRow(startx, starty, startx + 1, starty + 1, team)){
            return true;
        }
        if(checkRow(startx, starty, startx + 1, starty, team)){
            return true;
        }
        if(checkRow(startx, starty, startx + 1, starty - 1, team)){
            return true;
        }
        if(checkRow(startx, starty, startx, starty - 1, team)){
            return true;
        }
        if(checkRow(startx, starty, startx - 1, starty - 1, team)){
            return true;
        }
        if(checkRow(startx, starty, startx - 1, starty, team)){
            return true;
        }
        if(checkRow(startx, starty, startx - 1, starty + 1, team)){
            return true;
        }
        return false;
    }
    
    private boolean checkRow(int sx,int sy,int ex, int ey, int team){
        if (tiles[ex][ey].piece != null){
            if (tiles[ex][ey].piece.team != team){
                int changex = ex - sx;
                int changey = ey - sy;
                boolean found = false;
                while (ex < 8 && ex > -1 && ey > -1 && ex < 8){
                    ex += changex;
                    ey += changey;
                    if (tiles[ex][ey].piece == null)
                        return false;
                    if(tiles[ex][ey].piece.team != team){
                        //continue
                    }
                    if(tiles[ex][ey].piece.team == team)
                        return true;
                }
            }
        }
        return false;
    }
    
    public List<Piece> turnOther(int startx, int starty, int team){
        List<Piece> toReturn = new ArrayList();
        getPieces(startx, starty, startx, starty + 1, team, toReturn);
        if(checkRow(startx, starty, startx + 1, starty + 1, team)){
            getPieces(startx, starty, startx + 1, starty + 1, team, toReturn);
        }
        if(checkRow(startx, starty, startx + 1, starty, team)){
            getPieces(startx, starty, startx + 1, starty, team, toReturn);
        }
        if(checkRow(startx, starty, startx + 1, starty - 1, team)){
            getPieces(startx, starty, startx + 1, starty - 1, team, toReturn);
        }
        if(checkRow(startx, starty, startx, starty - 1, team)){
            getPieces(startx, starty, startx, starty - 1, team, toReturn);
        }
        if(checkRow(startx, starty, startx - 1, starty - 1, team)){
            getPieces(startx, starty, startx - 1, starty - 1, team, toReturn);
        }
        if(checkRow(startx, starty, startx - 1, starty, team)){
            getPieces(startx, starty, startx - 1, starty, team, toReturn);
        }
        if(checkRow(startx, starty, startx - 1, starty + 1, team)){
            getPieces(startx, starty, startx - 1, starty + 1, team, toReturn);
        }
        return toReturn;
    }
    
    public void getPieces(int sx,int sy,int ex, int ey, int team, List<Piece> toReturn){
        if (tiles[ex][ey].piece.team != team){
            toReturn.add(tiles[ex][ey].piece);
            int changex = ex - sx;
            int changey = ey - sy;
            boolean found = false;
            while (ex < 8 && ex > -1 && ey > -1 && ex < 8){
                ex += changex;
                ey += changey;
                if (tiles[ex][ey].piece == null)
                    break;
                if(tiles[ex][ey].piece.team != team){
                    toReturn.add(tiles[ex][ey].piece);
                }
                if(tiles[ex][ey].piece.team == team)
                    break;
            }
        }
    }
}
