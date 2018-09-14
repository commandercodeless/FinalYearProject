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
public class ChatLog {
    int chatId;
    List<String> messages = new ArrayList();
    List<Integer> sender = new ArrayList();
    List<Player> users = new ArrayList(); 
    
    public void addMessage(int userId, String message){
        messages.add(message);
        sender.add(userId);
    }
    
    public String generateName(){
        String name = "";
        name += users.get(0).username;
        for (int i = 1; i < users.size(); i++){
            name += ", " + users.get(i).username;
        }
        return name;
    }
    
    public List<Integer> generateUserList(){
        List<Integer> toReturn = new ArrayList();
        for (int i = 0; i < users.size(); i++){
            toReturn.add(users.get(i).userId);
        }
        return toReturn;
    }
}
