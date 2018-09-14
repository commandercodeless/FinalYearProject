/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author decla_000
 */
public class Client {

    public List<Player> friends = new ArrayList();
    public Game gameInfo;
    public ChatLog publicChat = new ChatLog();
    public List<ChatLog> privateChats = new ArrayList();
    public String username = "";
    public int userId;
    public String serverIp = "LocalHost";
    public List<Game> serverList =  new ArrayList();
    public List<Player> userList = new ArrayList();
    public connection network;
    public Player thisPlayer;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    public DefaultListModel friendsList(){
        DefaultListModel friendsList = new DefaultListModel();
        for (int i = 0; i < friends.size(); i++){
            friendsList.addElement(friends.get(i).username);
        }
        return friendsList;
    }
    
    public DefaultListModel gameList(){
        DefaultListModel gameList = new DefaultListModel();
        for (int i = 0; i < serverList.size(); i++){
            if (serverList.get(i).visible == true){
                gameList.addElement(serverList.get(i).hostName + ": " + serverList.get(i).gameType.gameName + ":" +serverList.get(i).numberOfPlayers);
            }
        }
        return gameList;
    }
    
    public DefaultListModel chatChannels(){
        DefaultListModel chatList = new DefaultListModel();
        chatList.addElement("Public Chat");
        
        for (int i = 0; i < privateChats.size(); i++){
            chatList.addElement(privateChats.get(i).generateName());
        }
        return chatList;
    }
    
    public boolean sendMessage(String messageToSend, int channel){
        String toSend = "Message:UserId:" + userId + ":Channel:" + Integer.toString(channel) + ":Message:" + messageToSend + ":END";
        
        try {
            network.output.writeObject(toSend);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
        
    public void addFriend(int userId){
        connection network = new connection();
        String toSend = "AddFriend:ThisUser:" + this.userId + "NewFriend:" + userId  + ":END";
        try {
            network.output.writeObject(toSend);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        network.closeConnection();
    }
    public void displayServerList(){
        
    }
}
