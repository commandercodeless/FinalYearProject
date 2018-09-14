/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author decla_000
 */
public class connection {
    static ServerSocket connection;
    ObjectInputStream input;
    ObjectOutputStream output;
    DataInputStream in;
    Socket serviceSocket;
    
    public connection() {
        try {
            connection = new ServerSocket(5432);
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public connection(String ip) {
        try {
            serviceSocket = new Socket(ip, 5432);
            input = new ObjectInputStream(serviceSocket.getInputStream());
            in = new DataInputStream(serviceSocket.getInputStream());
            output = new ObjectOutputStream(serviceSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getClient(){
        try {
            serviceSocket = connection.accept();
            output = new ObjectOutputStream(serviceSocket.getOutputStream());
            input = new ObjectInputStream(serviceSocket.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void closeConnection(){
        try {
            output.close();
            input.close();
            serviceSocket.close();
            //connection.close();
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
