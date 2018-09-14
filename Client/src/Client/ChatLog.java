/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author decla_000
 */
public class ChatLog {
    public int chatId;
    public List<String> messages = new ArrayList();
    public List<String> sender = new ArrayList();
    public List<Player> users = new ArrayList();
    public String name;
    
    public void SendMessage(String message,int userId){
        messages.add(message);
        sender.add(Integer.toString(userId));
        
        String toSend = "Message:ChatChannel:" + chatId + ":UserId:" + userId + ":Message:" + message  + ":END";
        connection network = new connection();
        try {
            network.output.writeBytes(toSend);
        } catch (IOException ex) {
            Logger.getLogger(ChatLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addMessage(String input){
        String[] splitArray = input.split(":");
        sender.add(splitArray[2]);
        messages.add(splitArray[6]);
    }
    public String Stringify(){
        String text = "";
        for (int i = 0; i < messages.size(); i++){
            for (int j = 0; j < users.size(); j++){
                if (users.get(j).userId == Integer.parseInt(sender.get(i))){
                    text += users.get(j).username + ": " + messages.get(i) + "\n";
                }
            }
        }
        return text;
    }
    
    public String generateName(){
//        String name = "";
//        if (users.size() > 2){
//            name = "GameChat";
//        } else{
//            name = users.get(1).username;
//        }

//        name += users.get(0).username;
//        for (int i = 1; i < users.size(); i++){
//            name += ", " + users.get(i).username;
//        }
        return name;
    }
    
    public DefaultListModel userList(){
        DefaultListModel userList = new DefaultListModel();
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).online){
                userList.addElement(users.get(i).username);
            }
        }
        return userList;
    }
    

}
