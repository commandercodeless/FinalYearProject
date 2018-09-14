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
public class Action {
    int playerId;
    int pieceMoved;
    int startLocationX;
    int startLocationY;
    List<Integer> flippedX = new ArrayList<>();
    List<Integer> flippedY = new ArrayList<>();
    
    public String classToString(){
        String text = "Turn:";
        text += Integer.toString(startLocationX);
        text += ":";
        text += Integer.toString(startLocationY);
        for (int i = 0; i < flippedX.size(); i++){
            text += ":";
            text += Integer.toString(flippedX.get(i));
            text += ":";
            text += Integer.toString(flippedY.get(i));
        }
        return text;
    }
    
    public Action StringToClass(String input){
        String splitArray[]= input.split(":");
        
        this.startLocationX = Integer.parseInt(splitArray[1]);
        this.startLocationY = Integer.parseInt(splitArray[2]);
        for (int i = 3; i < splitArray.length; i++){
            this.flippedX.add(Integer.parseInt(splitArray[i]));
            this.flippedY.add(Integer.parseInt(splitArray[i+1]));
            i++;
        }
        
        return this;
    }
}
