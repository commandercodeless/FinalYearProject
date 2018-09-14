/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

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
    Socket MyClient;
    ServerSocket connection;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public connection() {
        try {
            MyClient = new Socket("LocalHost", 5432);
            input = new ObjectInputStream(MyClient.getInputStream());
            output = new ObjectOutputStream(MyClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public connection(boolean message){
        try {
            connection = new ServerSocket(5434);
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }
    
    public connection(String ip, int port){
        try {
            MyClient = new Socket("LocalHost",port);
            //input = new ObjectInputStream(MyClient.getInputStream());
            output = new ObjectOutputStream(MyClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public connection(String ip) {
        try {
            MyClient = new Socket(ip, 5432);
            input = new ObjectInputStream(MyClient.getInputStream());
            output = new ObjectOutputStream(MyClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getServerReply(){
        try {
            connection = new ServerSocket(5433);
            MyClient = connection.accept();
            input = new ObjectInputStream(MyClient.getInputStream());
            output = new ObjectOutputStream(MyClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public void getHostReply(){
        try {
            //connection = new ServerSocket(5434);
            MyClient = connection.accept();
            input = new ObjectInputStream(MyClient.getInputStream());
            output = new ObjectOutputStream(MyClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getClientReply(){
        try {
            connection = new ServerSocket(5435);
            MyClient = connection.accept();
            input = new ObjectInputStream(MyClient.getInputStream());
            output = new ObjectOutputStream(MyClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void closeConnection(){
        try {
            
            output.close();
            input.close();
            MyClient.close();
            if (connection != null){
                connection.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
