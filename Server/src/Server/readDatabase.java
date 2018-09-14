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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class readDatabase{

    Connection connect;
    Statement state;
    ResultSet result;

    readDatabase(){ 
        try{    
            //connect =DriverManager.getConnection("jdbc:ucanaccess://C:\\Users\\decla_000\\Documents\\NetBeansProjects\\PRCO304Server\\database1.accdb");
            connect = DriverManager.getConnection("jdbc:ucanaccess://database1.accdb");
            state = connect.createStatement();
            result = state.executeQuery("SELECT Username FROM User");
            while (result.next()) {
                System.out.println(result.getString(1));
            }
        }
        catch(Exception e){}
    }
    
    public List<Player> getAllUsers(){
        try {
            List<Player> toReturn = new ArrayList();
            result = state.executeQuery("SELECT * FROM User");
            while (result.next()){
                Player newPlayer = new Player();
                newPlayer.userId = Integer.parseInt(result.getString(1));
                newPlayer.username = result.getString(2);
                toReturn.add(newPlayer);
            }
            return toReturn;
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void getUser(int userId){
        try {
            result = state.executeQuery("SELECT * FROM User WHERE UserId=" + userId);
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public boolean addUser(String username, String password){
        boolean valid = true;
        try {
            result = state.executeQuery("SELECT Username From User");
            while (result.next()){
                if (result.getString(1).equals(username)){
                    valid = false;
                }
            }
            if (valid == true){
                int a = state.executeUpdate("INSERT INTO User (Username, Password) VALUES ('" + username + "', '" + password + "')");
                return valid;
            }
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public int loginUser(String username, String password){
        try {
            result = state.executeQuery("SELECT UserId,Username,Password FROM User WHERE Username='" + username + "'");
            result.next();
            if (result.getString(3).equals(password)){
                return Integer.parseInt(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public void savePlayer(Player toSave, List<ChatLog> chats){    
        String gameString = Integer.toString(toSave.gamesPlayed.get(0));
        if (toSave.gamesPlayed.size() > 1){
            for (int i = 1; i < toSave.gamesPlayed.size(); i++){
                gameString += "," + Integer.toString(toSave.gamesPlayed.get(i));
            }
        }

        
        try {
            int a = state.executeUpdate("INSERT INTO User(GamesPlayed) VALUES(" + gameString + ") WHERE UserId=" + toSave.userId);
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Integer> getUserFriends(int userId){
        List<Integer> friendsList = new ArrayList<>();
        try {

            result = state.executeQuery("SELECT User2 FROM Friend WHERE User1=" + userId);
            while(result.next()){
                friendsList.add(Integer.parseInt(result.getString(1)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friendsList;
    }
    
    public void addUserFriend(int userId, int friendId){
        try{
            int a = state.executeUpdate("INSERT INTO Friend(User1, User2) VALUES('" + userId + "', '" + friendId +"')");
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeUserFriend(int userId, int friendId){
        try{
            int a = state.executeUpdate("DELETE FROM Friend WHERE User1=" + userId + " AND User2=" + friendId);
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Integer> getUserChats(int userId){
        List<Integer> chatIds = new ArrayList<>();
        
        try{
            result = state.executeQuery("SELECT Chats FROM User WHERE UserId=" + userId);
            result.next();
            String splitArray[] = result.getString(1).split(":");
            for (int i = 0; i < splitArray.length; i++){
                chatIds.add(Integer.parseInt(splitArray[i]));
            }
        } catch (SQLException ex) {
            Logger.getLogger(readDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chatIds;
    }
}