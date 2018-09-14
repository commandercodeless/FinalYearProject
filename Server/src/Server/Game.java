/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author decla_000
 */
public class Game {
    List<Player> players = new ArrayList();
    List<Player> spectators = new ArrayList();
    Player host;
    GameType gameType;
    ChatLog chat;
    String gameName = "l";
    int gameId;
    boolean visible = true;
    
    public String makeString(){
        String stringToReturn = "";
        stringToReturn += gameType.gameName; //1
        stringToReturn += ":" + Integer.toString(host.userId); //2
        stringToReturn += ":" + gameId; //3
        String seen = "false";
        if (visible == true){
            seen = "true";
        }
        stringToReturn += ":" + seen; //4
        stringToReturn += ":" + Integer.toString(players.size()); //5
        stringToReturn += ":" + Integer.toString(chat.chatId);
        return stringToReturn;
    }
}
