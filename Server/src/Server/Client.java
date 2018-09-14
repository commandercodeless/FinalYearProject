/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author decla_000
 */
public class Client {
    String ip;
    Socket MyClient;    
    ObjectOutputStream output;
    connection network;
    
    public boolean Send(String toSend) throws IOException{
        String splitString[] = toSend.split(":");
        if (splitString[splitString.length-1].equals("END")){}
        else{
            toSend += ":END";
        }
        try {
            //MyClient = new Socket(ip, 5433);
            //output = new ObjectOutputStream(MyClient.getOutputStream());
            network.output.writeObject(toSend);
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    public boolean Send(String toSend, connection openConnection) throws IOException{
        String splitString[] = toSend.split(":");
        if (splitString[splitString.length-1].equals("END")){}
        else{
            toSend += ":END";
        }
        try {
            openConnection.output.writeObject(toSend);
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
}
