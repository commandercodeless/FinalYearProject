/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author decla_000
 */
public class clientLoop extends Thread implements ISubject{
    
    public connection network;
    IObserver observer;
    String message = "";
    public volatile boolean run = true;
    
    public void run(){
        while (run){
            //network.getHostReply();
            try {
                message = (String) network.input.readObject();
            } catch (IOException ex) {
                Logger.getLogger(hostLoop.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(hostLoop.class.getName()).log(Level.SEVERE, null, ex);
            }
            notifyObservers();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(communicationLoop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        network.closeConnection();
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
    }
    
}
