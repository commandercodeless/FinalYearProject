/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author decla_000
 */
public class communicationLoop extends Thread implements ISubject{
    
    public Client clientData;
    public IObserver observer;
    String message = "NOT";
    public volatile boolean run = true;
    public communicationLoop(Client f){
        clientData = f;
    }
    
    public void run(){

        while(run){
            //clientData.network.getServerReply();
            String input = "";
            try {
                input = (String) clientData.network.input.readObject();
            } catch (IOException ex) {
                Logger.getLogger(communicationLoop.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(communicationLoop.class.getName()).log(Level.SEVERE, null, ex);
            }
            String[] splitArray = input.split(":");
            if (splitArray[0].equals("Message")){
                if (Integer.parseInt(splitArray[4]) == 0){
                    clientData.publicChat.addMessage(input);
                } else{
                    for (int i = 0; i < clientData.privateChats.size(); i++){
                        if (clientData.privateChats.get(i).chatId == Integer.parseInt(splitArray[4])){
                            clientData.privateChats.get(i).addMessage(input);
                        }
                    }
                }
                notifyObservers();
            }
            if (splitArray[0].equals("NumberOfPlayers")){
                for (int i = 0; i < clientData.serverList.size(); i++){
                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[3])){
                        clientData.serverList.get(i).numberOfPlayers = Integer.parseInt(splitArray[1]);
                    }
                }
                notifyObservers();
            }
            if (splitArray[0].equals("Launching")){
                for (int i = 0; i < clientData.serverList.size(); i++){
                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[1])){
                        clientData.serverList.get(i).visible = false;
                    }
                }
            }
            if (splitArray[0].equals("EndGame")){
                for (int i = 0; i < clientData.serverList.size(); i++){
                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[1])){
                        clientData.serverList.remove(clientData.serverList.get(i));
                    }
                }
            }
            if (splitArray[0].equals("Online")){
                for (int i = 0; i < clientData.userList.size(); i++){
                    if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[1])){
                        clientData.userList.get(i).online = true;
                        clientData.publicChat.users.add(clientData.userList.get(i));
                    }
                }
            }
            if (splitArray[0].equals("Offline")){
                for (int i = 0; i < clientData.userList.size(); i++){
                    if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[1])){
                        clientData.userList.get(i).online = false;
                        //clientData.publicChat.users.remove(clientData.userList.get(i));
                    }
                }
            }
            if (splitArray[0].equals("User")){
                Player newPlayer = new Player();
                newPlayer.userId = Integer.parseInt(splitArray[2]);
                newPlayer.username = splitArray[4];
                if (splitArray[6].equals("True")){
                    newPlayer.online = true;
                    clientData.publicChat.users.add(newPlayer);
                }
                clientData.userList.add(newPlayer);

                if (newPlayer.userId == clientData.userId){
                    clientData.thisPlayer = newPlayer;
                }
            }
            if (splitArray[0].equals("Game")){
                Game newGame = new Game();
                newGame.gameId = Integer.parseInt(splitArray[3]);
                newGame.gameType = new GameType(splitArray[1]);
                newGame.numberOfPlayers = Integer.parseInt(splitArray[5]);
                for (int i = 0; i < clientData.userList.size(); i++){
                    if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[2])){
                        newGame.host = clientData.userList.get(i);
                        newGame.hostName = clientData.userList.get(i).username;
                    }
                }
                newGame.chat.chatId = Integer.parseInt(splitArray[6]);
                clientData.serverList.add(newGame);
            }
            if (splitArray[0].equals("JoiningUser")){
                message = input;
            }
            if (splitArray[0].equals("NewChat")){
                ChatLog newChat = new ChatLog();
                newChat.chatId = Integer.parseInt(splitArray[3]);
                newChat.users.add(clientData.thisPlayer);
                for (int i = 0; i < clientData.userList.size(); i++){
                    if (Integer.parseInt(splitArray[1]) == clientData.userId){
                        if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[2])){
                            newChat.users.add(clientData.userList.get(i));
                            newChat.name = clientData.userList.get(i).username;
                        }
                    } else{
                        if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[1])){
                            newChat.users.add(clientData.userList.get(i));
                            newChat.name = clientData.userList.get(i).username;
                        }                        
                    }
                }
                clientData.privateChats.add(newChat);
                message = "ChangeChat";
            }
            notifyObservers();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(communicationLoop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    @Override
    public Boolean registerObserver(IObserver o) {
        observer = o;
        return true;
    }

    @Override
    public Boolean removeObserver(IObserver o) {
        observer = null;
        return true;
    }

    @Override
    public void notifyObservers() {
        observer.update(message);
        message = "NOT";
    }
}
