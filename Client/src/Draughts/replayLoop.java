/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Draughts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Client.IObserver;
import Client.ISubject;

/**
 *
 * @author decla_000
 */
public class replayLoop extends Thread implements ISubject{

    IObserver observer;
    String message = "NOT";
    public volatile boolean run = true;
    public List<Action> toReplay = new ArrayList();
    public List<GamePlayer> spectators = new ArrayList();
    
    public void run(){
        while(run){
            for (int i = 0; i < toReplay.size(); i++){
                message = toReplay.get(i).classToString();
                notifyObservers();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(replayLoop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            run = false;
        }
    }
    @Override
    public Boolean registerObserver(IObserver o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean removeObserver(IObserver o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notifyObservers() {
        observer.update(message);
    }

    
    
}
