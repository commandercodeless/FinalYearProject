/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author decla_000
 */
public class Player {
    String username;
    int userId;
    Client client;
    List<Integer> friends = new ArrayList();
    List<Integer> gamesPlayed;
    Boolean online = false;
    
    public void sendToPlayer(String messageToSend) throws IOException{
        client.Send(messageToSend);
    }
    public void sendToPlayer(String messageToSend, connection openConnection) throws IOException{
        client.Send(messageToSend, openConnection);
    }
}
