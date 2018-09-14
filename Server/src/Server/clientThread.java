/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author decla_000
 */
public class clientThread extends Thread{
    Player thisPlayer;
    Server server;
    volatile boolean run = true;
    public clientThread(){
        
    }
    
    public void run(){
        while(run){
            String message = "";
            try {
                message = (String) thisPlayer.client.network.input.readObject();
            } catch (IOException ex) {
                Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
                thisPlayer.online = false;
                thisPlayer.client = null;
                this.run = false;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            String[] splitArray = message.split(":");
            if (message.equals("")){
                thisPlayer.online = false;
                thisPlayer.client = null;
                server.offlineUser(thisPlayer);
                this.run = false;
            }
            if (splitArray[0].equals("JoinGame")){
                server.joinRequest(message);
            }
            if (splitArray[0].equals("Message")){
                server.newMessage(message);
            }
            if (splitArray[0].equals("AddFriend")){
                server.addFriend(message);
            }
            if (splitArray[0].equals("RemoveFriend")){
                server.removeFriend(message);
            }
            if (splitArray[0].equals("RequestData")){
                server.dataRequest(Integer.parseInt(splitArray[2]));
            }
            if (splitArray[0].equals("HostGame")){
                server.hostGame(message);
            }
            if (splitArray[0].equals("NumberOfPlayers")){
                server.updatePlayers(message);
            }
            if (splitArray[0].equals("Launching")){
                server.gameLaunched(message);
            }
            if (splitArray[0].equals("EndGame")){
                server.endGame(message);
            }
            if (splitArray[0].equals("NewChat")){
                server.newChat(message);
            }
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
