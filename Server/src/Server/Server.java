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
public class Server {

    List<Client> clientList = new ArrayList<Client>();
    ChatLog publicChat;
    List<ChatLog> privateChats = new ArrayList<ChatLog>();
    List<Player> users = new ArrayList<Player>();
    ServerList serverList = new ServerList();
    readDatabase database;
    connection openConnection = new connection();
    String message;
    int chatIds;
    List<clientThread> threads = new ArrayList<>();
    /**
     * @param args the command line arguments
     */
    Server(readDatabase data){
        database = data;
        users = database.getAllUsers();
        publicChat = new ChatLog();
        publicChat.chatId = 0;
        chatIds = 1;
    } 
    
    public void run(){
        String splitArray[];
        while(true){
            try {
                openConnection = new connection();
                openConnection.getClient();
                String message = "";
                try {
                    message = (String) openConnection.input.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                splitArray = message.split(":");
                if (splitArray[0].equals("Register") || splitArray[0].equals("Login")){
                    login(message);
                }
                if (splitArray[0].equals("JoinGame")){
                    joinRequest(message);
                }
                if (splitArray[0].equals("Message")){
                    newMessage(message);
                }
                if (splitArray[0].equals("AddFriend")){
                    addFriend(message);
                }
                if (splitArray[0].equals("RemoveFriend")){
                    removeFriend(message);
                }
                if (splitArray[0].equals("RequestData")){
                    dataRequest(Integer.parseInt(splitArray[2]));
                }
                if (splitArray[0].equals("HostGame")){
                    hostGame(message);
                }
                if (splitArray[0].equals("NumberOfPlayers")){
                    updatePlayers(message);
                }
                if (splitArray[0].equals("Launching")){
                    gameLaunched(message);
                }
                if (splitArray[0].equals("EndGame")){
                    endGame(message);
                }
                if (splitArray[0].equals("NewChat")){
                    newChat(message);
                }
                //openConnection.closeConnection();
                message = "";
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void endGame(String input){
        String[] splitArray = input.split(":");
        for (int i = 0; i < serverList.gameList.size(); i++){
            if (serverList.gameList.get(i).gameId == Integer.parseInt(splitArray[1])){
                serverList.gameList.remove(serverList.gameList.get(i));
            }
        }
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).online){
                try {
                    users.get(i).sendToPlayer(input);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void newChat(String input){
        String[] splitArray = input.split(":");
        ChatLog newChat = new ChatLog();
        newChat.chatId = chatIds;
        chatIds++;
        for (int i = 0; i < users.size(); i++){
            if ((users.get(i).userId == Integer.parseInt(splitArray[2]) )|| (users.get(i).userId == Integer.parseInt(splitArray[4]))){
                newChat.users.add(users.get(i));
            }
        }
        String toSend = "NewChat:" + splitArray[2] + ":" + splitArray[4] + ":" + Integer.toString(newChat.chatId);
        for (int i = 0; i < 2; i++){
            if (newChat.users.get(i).online){
                try {
                    newChat.users.get(i).sendToPlayer(toSend);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        privateChats.add(newChat);
    }
    public void gameLaunched(String input){
        String[] splitArray = input.split(":");
        for (int i = 0; i < serverList.gameList.size(); i++){
            if (serverList.gameList.get(i).gameId == Integer.parseInt(splitArray[1])){
                serverList.gameList.get(i).visible = false;
            }
        }
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).online){
                try {
                    users.get(i).sendToPlayer(input);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void updatePlayers(String input){
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).online){
                try {
                    users.get(i).sendToPlayer(input);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void newMessage(String input){
        String splitArray[] = input.split(":");
        if (splitArray[4].equals("0")){ //whatever public chat has as its id
            publicChat.addMessage(Integer.parseInt(splitArray[2]), splitArray[6]);
            for (int i = 0; i < users.size(); i++){
                if (users.get(i).online){
                    try {
                        users.get(i).sendToPlayer(input);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }else{
            for (int i = 0; i < privateChats.size(); i++){
                if (privateChats.get(i).chatId == Integer.parseInt(splitArray[4])){
                    privateChats.get(i).addMessage(Integer.parseInt(splitArray[2]), splitArray[6]);
                    for (int j = 0; j < privateChats.get(i).users.size(); j++){
                        if (privateChats.get(i).users.get(j).online){
                            try {
                                privateChats.get(i).users.get(j).sendToPlayer(input);
                            } catch (IOException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
        
        
    }
    
    public void dataRequest(int userId){
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).userId == userId){
//                try {
//                    users.get(i).sendToPlayer("CLIENTDATA:END", openConnection);
//                } catch (IOException ex) {
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                }
                onlineUsers(users.get(i));
                friendsList(users.get(i));
                sendServer(users.get(i));
                try {
                    users.get(i).sendToPlayer("DATAEND:END");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void joinRequest(String input){
        String splitArray[] = input.split(":");
        String toSend = "JoiningUser:UserId:" + splitArray[2];
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).userId == Integer.parseInt(splitArray[2])){
                toSend += ":UserIp:" + users.get(i).client.ip;
                toSend += ":Username:" + users.get(i).username;
            }
        }
        for (int i = 0; i < serverList.gameList.size(); i++){
            if (serverList.gameList.get(i).gameId == Integer.parseInt(splitArray[4])){
                toSend += ":HostIp:" + serverList.gameList.get(i).host.client.ip + ":END";
                for (int j = 0; j < users.size(); j++){
                    if (users.get(j).userId == Integer.parseInt(splitArray[2])){
                        serverList.gameList.get(i).chat.users.add(users.get(j));
                        serverList.gameList.get(i).players.add(users.get(j));
                    }
                }
                try {
                    serverList.gameList.get(i).host.sendToPlayer(toSend);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
//        for (int i = 0; i < users.size(); i++){
//            if (users.get(i).userId == Integer.parseInt(splitArray[2])){
//                try {
//                    users.get(i).sendToPlayer(toSend);
//                } catch (IOException ex) {
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
    }
    
    
    public void onlineUsers(Player user){
        String toSend;
        for (int i = 0; i < users.size(); i++){
            //if (users.get(i).userId != user.userId){
                try {
                    String online = "False";
                    if (users.get(i).online == true){
                        online = "True";
                    }
                    toSend = "User:UserId:" + Integer.toString(users.get(i).userId) + ":Username:" + users.get(i).username + ":Online:" + online +":END";
                    user.client.Send(toSend);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            //}
            //}
        }
    }
    
    public void friendsList(Player user){
        List<Integer> friendsList = database.getUserFriends(user.userId);
        String toSend;
        for (int i = 0; i < friendsList.size(); i++){
            try {
                toSend = "Friend:UserId:" + Integer.toString(friendsList.get(i)) + ":END";
                user.client.Send(toSend);
                user.friends.add(friendsList.get(i));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void sendChat(Player user){
        String toSend = "ChatChannel:ChatId:0:END";
        try {
            user.sendToPlayer(toSend, openConnection);
            //openConnection.output.writeObject(publicChat.generateUserList());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //List<Integer> chatList = database.getUserChats(user.userId);
        
        //List<Integer> chatList = database.getUserChats(user.userId);
    }
    
    public void sendServer(Player user){
        String toSend;
        if (serverList.gameList.size() > 0){
            for (int i = 0; i < serverList.gameList.size(); i++){
                try {
                    user.sendToPlayer(serverList.makeString(i));
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void addFriend(String message){
        String splitArray[] = message.split(":");
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).userId == Integer.parseInt(splitArray[2])){
                users.get(i).friends.add(Integer.parseInt(splitArray[4]));
                database.addUserFriend(Integer.parseInt(splitArray[2]), Integer.parseInt(splitArray[4]));
            }
        }
    }
    
    public void removeFriend(String message){
        String splitArray[] = message.split(":");
        database.removeUserFriend(Integer.parseInt(splitArray[2]), Integer.parseInt(splitArray[4]));
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).userId == Integer.parseInt(splitArray[2])){
                users.get(i).friends.remove(Integer.parseInt(splitArray[4]));
            }
        }
    }
    public void login(String request){
        String splitArray[] = request.split(":");
        if (splitArray[0].equals("Register")){
            if(database.addUser(splitArray[2], splitArray[4])){
                int id = database.loginUser(splitArray[2], splitArray[4]);
                Player newPlayer = new Player();
                newPlayer.userId = id;
                newPlayer.username = splitArray[2];
                for (int i = 0; i < users.size(); i++){
                    if (users.get(i).online){
                        String toSend = "User:UserId:" + Integer.toString(id) + ":Username:" + newPlayer.username + ":Online:" + "False" +":END";
                        try {
                            users.get(i).client.Send(toSend);
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                users.add(newPlayer);
            }
        }
        if (splitArray[0].equals("Login")){
            int id = database.loginUser(splitArray[2], splitArray[4]);
            if (id > -1){
                for (int i = 0; i < users.size(); i++){
                    Player newPlayer;
                    if (id == users.get(i).userId){
                        newPlayer = users.get(i);
                        newPlayer.username = splitArray[2];
                        newPlayer.online = true;
                        Client newClient = new Client();
                        newClient.ip = "LocalHost";
                        newClient.network = openConnection;
                        newPlayer.client = newClient;
                        //users.add(newPlayer);
                        try {
                            newPlayer.sendToPlayer("Login:Id:" + Integer.toString(id), openConnection);
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        clientThread newThread = new clientThread();
                        newThread.thisPlayer = newPlayer;
                        newThread.server = this;
                        threads.add(newThread);
                        newThread.start();
                    }
                }
                //dataRequest(id);
                for (int i = 0; i < users.size(); i++){
                    if (id != users.get(i).userId && users.get(i).online){
                        try {
                            users.get(i).sendToPlayer("Online:" + Integer.toString(id) + ":END");
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            if (id == -1){
                Client toReturn = new Client();
                toReturn.ip = "LocalHost";
                try {
                    toReturn.Send("Failed", openConnection);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void hostGame(String request){
        String splitArray[] = request.split(":");
        Game newGame = new Game();
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).userId == Integer.parseInt(splitArray[2])){
                newGame.host = users.get(i);
                newGame.gameId = serverList.ids;
                newGame.gameName = splitArray[4];
                newGame.gameType = new GameType(splitArray[4]);
                newGame.players.add(newGame.host);
                newGame.chat = new ChatLog();
                newGame.chat.users.add(newGame.host);
                newGame.chat.chatId = chatIds;
                chatIds++;
                privateChats.add(newGame.chat);
            }
        }
        serverList.addGame(newGame);
        String toSend = "Game:" + newGame.makeString() + ":END";
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).online){
                try {
                    users.get(i).sendToPlayer(toSend);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void offlineUser(Player offline){
        if (serverList.gameList.size() > 0){
            for (int i = 0; i < serverList.gameList.size(); i++){
                if (serverList.gameList.get(i).host.userId == offline.userId){
                    serverList.gameList.remove(serverList.gameList.get(i));
                }
            }
        }
        if (users.size() > 0){
            for (int i = 0; i < users.size(); i++){
                if (users.get(i).online){
                    try {
                        users.get(i).sendToPlayer("Offline:" + Integer.toString(offline.userId));
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}