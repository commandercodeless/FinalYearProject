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
public class Action {
    int playerId;
    int pieceMoved;
    int startLocationX;
    int startLocationY;
    int endLocationX;
    int endLocationY;
    
    
    public String classToString(){
        String text = "Turn:";
        text += Integer.toString(startLocationX);
        text += ":";
        text += Integer.toString(startLocationY);
        text += ":";
        text += Integer.toString(endLocationX);
        text += ":";
        text += Integer.toString(endLocationY);
        
        return text;
    }
    
    public Action StringToClass(String input){
        String splitArray[]= input.split(":");
        
        this.startLocationX = Integer.parseInt(splitArray[1]);
        this.startLocationY = Integer.parseInt(splitArray[2]);
        this.endLocationX = Integer.parseInt(splitArray[3]);
        this.endLocationY = Integer.parseInt(splitArray[4]);
        
        return this;
    }
}
