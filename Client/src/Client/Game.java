/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author decla_000
 */
public class Game {
    List<Player> players = new ArrayList();
    //List<Player> spectators = new ArrayList();
    GameType gameType;
    GameState gameState;
    ChatLog chat = new ChatLog();
    GameState initialState;
    List<Action> listOfActions;
    String gameName;
    Player host;
    String hostName;
    int numberOfPlayers = 1;
    public int gameId;
    boolean visible = true;
    
    public boolean checkRole(String role, int userId){
        if (gameType == null){
            gameType =  new GameType(gameName);
        }
        
        if (gameType.maxPlayers == gameType.roles.length - 1){
            for (int i = 0; i < players.size(); i++){
                if (players.get(i).role.equals(role)){
                    return false;
                }
            }
            return true;
        } else{
            int setPlayers = 0;
            for (int i = 0; i < players.size(); i++){
                if (players.get(i).userId != userId){
                    if (players.get(i).role.equals("Player")){
                        setPlayers++;
                    }
                }
            }
            
            if (setPlayers < gameType.maxPlayers){
                return true;
            }
        }
        return false;
    }
}
