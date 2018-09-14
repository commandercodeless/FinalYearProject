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
public class ReceiveData {
    
    public ChatLog readChatString(String stringToRead, List<Player> userList){
        ChatLog logToReturn = new ChatLog();
        String splitArray[]= stringToRead.split(":");
        if (splitArray[0].equals("Chat")){
            int userStart = 0;
            for (int i = 0; i < splitArray.length; i++){
                if (splitArray[i].equals("Users")){
                    userStart = i;
                }
            }
            int numberOfMessages = (userStart - 1) / 3;
            for (int i = 1; i < numberOfMessages + 1; i++){
                logToReturn.messages.add(splitArray[i + 2]);
                logToReturn.sender.add(splitArray[i + 1]);
                i += 2;
            }
            for (int i = userStart; i < splitArray.length; i++){
                for (int j = 0; j < userList.size(); j++){
                    if (Integer.toString(userList.get(j).userId).equals(splitArray[i+1])){
                        logToReturn.users.add(userList.get(j));
                    }
                }
                i++;
            }
        }
        
        return logToReturn;
    }
    
    public List<Game> readGameString(String stringToRead, List<Player> userList){
        List<Game> serverList = new ArrayList<>();
        String splitArray[] = stringToRead.split(":");
        if (splitArray[0].equals("GameList")){
            for (int i = 1; i < splitArray.length; i++){
                Game newGame = new Game();
                newGame.gameName = splitArray[i + 1];
                newGame.hostName = splitArray[i + 2];
                newGame.gameId = Integer.parseInt(splitArray[i + 3]);
                newGame.gameType.gameName = splitArray[i + 4];
                newGame.numberOfPlayers = Integer.parseInt(splitArray[i + 5]);
                i += 5;
                serverList.add(newGame);
            }
        }
        return serverList;

    }
}
