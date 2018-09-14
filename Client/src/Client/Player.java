/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author decla_000
 */
public class Player {
    public String username;
    public int userId;
    public String ip;
    public String role = "Spectator";
    public boolean ready = false;
    public boolean online = false;
    public connection network;
    
    public boolean Send(String toSend, boolean host) throws IOException{
        //write code ot send messages to the users ip;
        String splitString[] = toSend.split(":");
        if (splitString[splitString.length-1].equals("END")){}
        else{
            toSend += ":END";
        }
        if (network == null){
            if (host){
                connection tempNetwork = new connection(ip, 5434);
                tempNetwork.output.writeObject(toSend);
                tempNetwork.closeConnection();
            } else{
                connection tempNetwork = new connection(ip, 5435);
                tempNetwork.output.writeObject(toSend);
                tempNetwork.closeConnection();
            }
        } else{
            network.output.writeObject(toSend);
        }
        return true;
    }
}
