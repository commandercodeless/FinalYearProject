/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author decla_000
 */
public class ServerList {
    List<Game> gameList = new ArrayList();
    int ids = 0;
    
    public void addGame(Game gameToAdd){
        gameList.add(gameToAdd);
        ids++;
    }
    
    public void removeGame(Game gameToRemove){
        gameList.remove(gameToRemove);
    }
    
    public String makeString(int game){
        String stringToSend = "Game";
        stringToSend += ":";
        stringToSend += gameList.get(game).makeString();
        stringToSend += ":END";
        return stringToSend;
    }
    
//    public void sendToClients(List<Client> clientList){
//        String stringToSend = makeString();
//        for (int i = 0; i < clientList.size(); i++){
//            try {
//                clientList.get(i).Send(stringToSend);
//            } catch (IOException ex) {
//                Logger.getLogger(ServerList.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//    
//    public void sendToClient(Client newClient){
//        String stringToSend = makeString();
//        try {
//            newClient.Send(stringToSend);
//        } catch (IOException ex) {
//            Logger.getLogger(ServerList.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
