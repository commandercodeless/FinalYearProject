/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Draughts;

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

    public boolean victory(){
        int white = 0;
        int black = 0;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (tiles[i][j].piece != null){
                    if (tiles[i][j].piece.team == 0){
                        white++;
                    }
                    if (tiles[i][j].piece.team == 1){
                        black++;
                    }
                }
            }
        }
        if (white == 0){
            return true;
        }
        if (black == 0){
            return true;
        }
        return false;
    }
    public void add(Piece pieceToAdd){
        int x = pieceToAdd.x;
        int y = pieceToAdd.y;
        tiles[x][y].piece = pieceToAdd;
    }
    
    public boolean checkValid(int startx, int starty, int endx, int endy, int team){
        int moveX = endx - startx;
        int moveY = endy - starty;
        if (tiles[endx][endy].piece != null){
            return false;
        }
        if (Math.abs(moveX) != Math.abs(moveY)){
            return false;
        }
        if (tiles[startx][starty].piece.name.equals("normal")){
            if (team == 0){
                if (moveX < 0){
                    return false;
                }
            } else{
                if (moveX > 0){
                    return false;
                }
            }
        }
        
        if (Math.abs(moveX) == 1){
            if (tiles[endx][endy].piece == null){
                return true;
            }
        }
        if (tiles[startx][starty].piece.name.equals("normal")){
            if (team == 0){
                if (startx - endx != -2){
                    return false;
                }
            } else{
                if (startx - endx != 2){
                    return false;
                }
            }
        }
        if (startx - endx == starty - endy){
            if (startx > endx){
                if (startx - endx == 1){
                    return true;
                } else{
                    for (int i = 1; i < startx - endx; i++){
                        if (tiles[startx - i][starty - i].piece == null){
                            return false;
                        } else{
                            if (tiles[startx - i][starty - i].piece.team == team){
                                return false;
                            }
                        }
                    }
                }
            } else{
                if (endx - startx == 1){
                    return true;
                } else{
                    for (int i = 1; i < endx - startx; i++){
                        if (tiles[startx + i][starty + i].piece == null){
                            return false;
                        } else{
                            if (tiles[startx + i][starty + i].piece.team == team){
                                return false;
                            }
                        }
                    }
                }
            }
        }
        if ((endx - startx == starty - endy) || (startx - endx == endy - starty)){
            if (startx > endx){
                if (startx - endx == 1){
                    return true;
                } else{
                    for (int i = 1; i < startx - endx; i++){
                        if (tiles[startx - i][starty + i].piece == null){
                            return false;
                        } else{
                            if (tiles[startx - i][starty + i].piece.team == team){
                                return false;
                            }
                        }
                    }
                }
            }else{
                if (endx - startx == 1){
                    return true;
                } else{
                    for (int i = 1; i < endx - startx; i++){
                        if (tiles[startx + i][starty - i].piece == null){
                            return false;
                        } else{
                            if (tiles[startx + i][starty - i].piece.team == team){
                                return false;
                            }   
                        }
                    }
                }
            }
        }
        return true;
    }
}
