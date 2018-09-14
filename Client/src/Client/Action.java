/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author decla_000
 */
public class Action {
    int playerId;
    int objectMoved;
    int endLocation;
    
    public void classToString(){
        String text = "";
        text += Integer.toString(playerId);
        text += ":"; 
    }
    
    public void StringToClass(String input){
        String array1[]= input.split(":");
    }
}
