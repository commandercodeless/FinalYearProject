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
public class Action {
    int player;
    int x;
    int y;
    boolean victory;
    
    public String classToString(){
        String win = "False";
        if (victory){
            win = "True";
        }
        String toReturn = "Turn:" + Integer.toString(player) + ":" + Integer.toString(x) + ":" + Integer.toString(y) +":" + win + ":END";
        return toReturn;
    }
    
    public Action stringToClass(String input){
        String[] splitArray = input.split(":");
        Action toReturn = new Action();
        toReturn.player = Integer.parseInt(splitArray[1]);
        toReturn.x = Integer.parseInt(splitArray[2]);
        toReturn.y = Integer.parseInt(splitArray[3]);
        if (splitArray[4].equals("True")){
            toReturn.victory = true;
        } else{
            toReturn.victory = false;
        }
        return toReturn;
    }
}
