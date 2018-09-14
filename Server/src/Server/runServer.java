/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author decla_000
 */
public class runServer {
    public static void main(String[] args) {
        
        System.out.print("Working");
        readDatabase test = new readDatabase();
        System.out.print("Database worked");
        Server server = new Server(test);
        System.out.print("Made Server");
        server.run();
    }
}
