/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.util.List;

/**
 *
 * @author decla_000
 */
public class GameType {
    String gameName;
    int minPlayers;
    int maxPlayers;
    String[] roles;
    
    GameType(String type){
        if (type.equals("Chess")){
            gameName = "Chess";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[1] = "White";
            roles[2] = "Black";
            roles[0] = "Spectator";
        }
        if (type.equals("TicTacToe")){
            gameName = "TicTacToe";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[1] = "Noughts";
            roles[2] = "Crosses";
            roles[0] = "Spectator";
        }
        if (type.equals("GoFish")){
            gameName = "GoFish";
            minPlayers = 2;
            maxPlayers = 4;
            roles = new String[2];
            roles[1] = "Player";
            roles[0] = "Spectator";
        }
        if (type.equals("Draughts")){
            gameName = "Draughts";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[1] = "White";
            roles[2] = "Black";
            roles[0] = "Spectator";
        }
        if (type.equals("Poker")){
            gameName = "Poker";
            minPlayers = 2;
            maxPlayers = 4;
            roles = new String[2];
            roles[1] = "Player";
            roles[0] = "Spectator";
        }
        if (type.equals("Reversi")){
            gameName = "Reversi";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[1] = "White";
            roles[2] = "Black";
            roles[0] = "Spectator";
        }
    }
    
    public Boolean validSetup(List<Player> players){
        if (gameName.equals("GoFish") || gameName.equals("Poker")){
            int numberOfPlayers = 0;
            for (int i = 0; i < players.size(); i++){
                if (players.get(i).role.equals("Player")){
                    numberOfPlayers++;
                }
            }
            if (numberOfPlayers >= minPlayers){
                if (numberOfPlayers <= maxPlayers){
                    return true;
                }
            }
        }
        if (gameName.equals("Chess") || gameName.equals("Draughts") || gameName.equals("Reversi")){
            boolean white = false;
            boolean black = false;
            for (int i = 0; i < players.size(); i++){
                if (players.get(i).role.equals("White")){
                    if (white){
                        return false;
                    } else{
                        white = true;
                    }
                }
                if (players.get(i).role.equals("Black")){
                    if (black){
                        return false;
                    } else{
                        black = true;
                    }
                }
            }
            
            if (black == true && white == true){
                return true;
            }
        }
        if (gameName.equals("TicTacToe")){
            boolean white = false;
            boolean black = false;
            for (int i = 0; i < players.size(); i++){
                if (players.get(i).role.equals("Noughts")){
                    if (white){
                        return false;
                    } else{
                        white = true;
                    }
                }
                if (players.get(i).role.equals("Crosses")){
                    if (black){
                        return false;
                    } else{
                        black = true;
                    }
                }
            }
            
            if (black == true && white == true){
                return true;
            }
        }
        return false;
    }
}
