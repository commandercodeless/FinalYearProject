/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TicTacToe;

/**
 *
 * @author decla_000
 */
public class Board {
    public int tiles[][] = new int[3][3];
    //0 = o; 1 = x
    public Board(){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                tiles[i][j] = -1;
            }
        }
    }
    
    public boolean checkVictory(int team){
        int found = 0;
        
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (tiles[i][j] == team){
                    found++;
                }
            }
            if (found == 3){
                return true;
            }
            found = 0;
        }
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (tiles[j][i] == team){
                    found++;
                }
            }
            if (found == 3){
                return true;
            }
            found = 0;
        }
        if (tiles[0][0] == team && tiles[1][1] == team && tiles[2][2] == team){
            return true;
        }
        if (tiles[2][0] == team && tiles[1][1] == team && tiles[0][2] == team){
            return true;
        }        
        return false;
    }
    
    public boolean checkTie(){
        int found = 0;
        
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (tiles[i][j] > -1){
                    found++;
                }
            }
        }
        if (found == 9){
            return true;
        }
        return false;
    }
}
