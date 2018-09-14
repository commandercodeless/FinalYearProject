/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

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
            roles[0] = "White";
            roles[1] = "Black";
            roles[2] = "Spectator";
        }
        if (type.equals("TicTacToe")){
            gameName = "TicTacToe";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[0] = "Noughts";
            roles[1] = "Crosses";
            roles[2] = "Spectator";
        }
        if (type.equals("GoFish")){
            gameName = "GoFish";
            minPlayers = 2;
            maxPlayers = 4;
            roles = new String[2];
            roles[0] = "Player";
            roles[1] = "Spectator";
        }
        if (type.equals("Draughts")){
            gameName = "Draughts";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[0] = "White";
            roles[1] = "Black";
            roles[2] = "Spectator";
        }
        if (type.equals("Poker")){
            gameName = "Poker";
            minPlayers = 2;
            maxPlayers = 4;
            roles = new String[2];
            roles[0] = "Player";
            roles[1] = "Spectator";
        }
        if (type.equals("Reversi")){
            gameName = "Reversi";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[0] = "White";
            roles[1] = "Black";
            roles[2] = "Spectator";
        }
        if (type.equals("Whist")){
            gameName = "Whist";
            minPlayers = 4;
            maxPlayers = 4;
            roles = new String[3];
            roles[0] = "Team1";
            roles[1] = "Team2";
            roles[2] = "Spectator";
        }
        if (type.equals("Go")){
            gameName = "Go";
            minPlayers = 2;
            maxPlayers = 2;
            roles = new String[3];
            roles[0] = "White";
            roles[1] = "Black";
            roles[2] = "Spectator";
        }
    }
}