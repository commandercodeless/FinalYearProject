/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author decla_000
 */
public class MainView extends javax.swing.JFrame implements IObserver{

    public Client clientData;
    public int chatChannel;
    public communicationLoop mainLoop;
    /**
     * Creates new form MainView
     * @param client
     */
    public MainView() {
        //chatChannel = 0;
        initComponents();
        //GetDataFromServer(); MAYBE DON'T NEED THIS
        //displayData();
    }
    
    public void run(){
        displayData();
        mainLoop = new communicationLoop(clientData);
        mainLoop.observer = this;
        mainLoop.start();
    }

    public void sendJoinRequest(){
        int gameId = clientData.serverList.get(gameList.getSelectedIndex()).gameId;
        
        String toSend = "JoinGame:UId:" + clientData.userId + ":GId:" + gameId  + ":END";
        
        try {
            clientData.network.output.writeObject(toSend);
        } catch (IOException ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }
            try {
                connection network = new connection(false);
                network.getHostReply();
                String returned = "";
                try {
                    returned = (String) network.input.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                }
                String splitArray[] = returned.split(":");
                if (splitArray[0].equals("Joined")){
                    Player host = new Player();
                    Game game = new Game();
                    for (int i = 0; i < clientData.userList.size(); i++){
                        if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[2])){
                            host = clientData.userList.get(i);
                            host.ip = splitArray[4];
                            host.network = network;
                        }
                    }
                    for (int i = 0; i < clientData.serverList.size(); i++){
                        if (clientData.serverList.get(i).gameId == gameId){
                            game = clientData.serverList.get(i);
                            game.gameType = new GameType(splitArray[6]);
                            game.host = host;
                        }
                    }
                    game.chat.users.add(clientData.thisPlayer);
                    clientData.privateChats.add(game.chat);
                    game.chat.name = "GameChat";
                    gameView gameview = new gameView();
                    if (splitArray[7].equals("replay")){
                        gameview.replay = true;
                    } else{
                        gameview.replay = false;
                    }
                    boolean loop = true;
                    while(loop){
                        try {
                            returned = (String) network.input.readObject();
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        splitArray = returned.split(":");
                        if (splitArray[0].equals("Player")){
                            for (int i = 0; i < clientData.userList.size(); i++){
                                if (clientData.userList.get(i).userId == Integer.parseInt(splitArray[2])){
                                    game.players.add(clientData.userList.get(i));
                                    clientData.userList.get(i).role = splitArray[4];
                                    game.chat.users.add(clientData.userList.get(i));
                                }
                            }
                        }
                        if (splitArray[0].equals("ENDPLAYERS")){
                            loop = false;
                        }
                    }
                    gameview.clientData = clientData;
                    gameview.hostPlayer = host;
                    gameview.thisGame = game;
                    gameview.chatChannel = chatChannel;
                    gameview.comLoop = mainLoop;
                    mainLoop.observer = gameview;
                    clientData.thisPlayer.role = "spectator";
                    gameview.thisGame.players.add(clientData.thisPlayer);
                    gameview.previousView = this;

                    this.setVisible(false);
                    gameview.setVisible(true);
                    gameview.runSetup(network);
                }
            } catch (IOException ex) {
                Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void GetDataFromServer(){
        String textToSend = "RequestData:UserId:" + clientData.userId;
        connection network = new connection();
        try {
            network.output.writeObject(textToSend);
            //WHILE TRUE CODE FROM LOGIN ONCE THAT STARTS WORKING                                                     <-----------------DON'T FORGET - MIGHT NOT NEED
        } catch (IOException ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void displayData(){
        DefaultListModel userListModel = new DefaultListModel();
        if (chatChannel == 0){
            chat.setText(clientData.publicChat.Stringify());
            userListModel = clientData.publicChat.userList();
        } else{
            for (int i = 0; i < clientData.privateChats.size(); i++){
                if (clientData.privateChats.get(i).chatId == chatChannel){
                    chat.setText(clientData.privateChats.get(i).Stringify());
                    userListModel = clientData.privateChats.get(i).userList();
                }
            }
        }

        chatList.setModel(userListModel);
        
        userListModel = new DefaultListModel();
        for (int i = 0; i < clientData.userList.size(); i++){
            if (clientData.userList.get(i).online == true){
                userListModel.addElement(clientData.userList.get(i).username);
            }
        }
        userList.setModel(userListModel);
        
        friendsList.setModel(clientData.friendsList());
        gameList.setModel(clientData.gameList());
        chatChannels.setModel(clientData.chatChannels());
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane6 = new javax.swing.JScrollPane();
        gameList = new javax.swing.JList<>();
        btnCreateGame = new javax.swing.JButton();
        btnLogOut = new javax.swing.JButton();
        btnJoinGame = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chat = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        message = new javax.swing.JTextArea();
        btnSend = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        chatChannels = new javax.swing.JList<>();
        userPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        friendsList = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        chatList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        gameList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        gameList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(gameList);

        btnCreateGame.setText("Create Game");
        btnCreateGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateGameActionPerformed(evt);
            }
        });

        btnLogOut.setText("Log out");
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });

        btnJoinGame.setText("Join Game");
        btnJoinGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJoinGameActionPerformed(evt);
            }
        });

        chat.setEditable(false);
        chat.setColumns(20);
        chat.setRows(5);
        jScrollPane1.setViewportView(chat);

        message.setColumns(20);
        message.setRows(5);
        jScrollPane2.setViewportView(message);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        chatChannels.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        chatChannels.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        chatChannels.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                chatChannelsValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(chatChannels);

        userList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userListMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(userList);

        userPane.addTab("Online", jScrollPane3);

        friendsList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        friendsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        friendsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                friendsListMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(friendsList);

        userPane.addTab("Friends", jScrollPane5);

        chatList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        chatList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                chatListMousePressed(evt);
            }
        });
        jScrollPane7.setViewportView(chatList);

        userPane.addTab("Chat", jScrollPane7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCreateGame)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnJoinGame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userPane, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(userPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSend, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreateGame)
                            .addComponent(btnLogOut)
                            .addComponent(btnJoinGame))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateGameActionPerformed
        //create game class
        this.setVisible(false);
        gameView newGame = new gameView();
        newGame.previousView = this;
        newGame.chatChannel = chatChannel;
        mainLoop.observer = newGame;
        newGame.comLoop = mainLoop;
        newGame.clientData = clientData;
        for (int i = 0; i < clientData.userList.size(); i++){
            if(clientData.userList.get(i).userId == clientData.userId){
                newGame.thisGame.players.add(clientData.userList.get(i));
                newGame.hostPlayer = clientData.userList.get(i);
            }
        }
        newGame.setVisible(true);
        newGame.runSetupHost();
    }//GEN-LAST:event_btnCreateGameActionPerformed

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        new Login().setVisible(true);
        try {
            this.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void btnJoinGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJoinGameActionPerformed
        sendJoinRequest();
    }//GEN-LAST:event_btnJoinGameActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String messageToSend = message.getText();
        if (clientData.sendMessage(messageToSend, chatChannel)){
            displayData();
        }
        message.setText("");
    }//GEN-LAST:event_btnSendActionPerformed

    private void chatChannelsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_chatChannelsValueChanged
        if (chatChannels.getSelectedIndex() == 0){
            chatChannel = 0;
        } else{
            if (chatChannels.getSelectedIndex() - 1 >= 0){
                chatChannel = clientData.privateChats.get(chatChannels.getSelectedIndex() - 1).chatId;
            }
        }
        displayData();
    }//GEN-LAST:event_chatChannelsValueChanged

    private void userListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userListMousePressed
        if (SwingUtilities.isRightMouseButton(evt)){
            userList.setSelectedIndex(userList.locationToIndex(evt.getPoint()));

            String username = userList.getSelectedValue();
            boolean friend = false;
            JPopupMenu menu = new JPopupMenu();
            for (int i = 0; i < clientData.friends.size(); i++){
                if (clientData.friends.get(i).username.equals(username)){
                    friend = true;
                }
            }
            if (friend == false){
                JMenuItem addFriend = new JMenuItem("AddFriend");
                addFriend.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String userString = "";
                        for (int i = 0; i < clientData.userList.size(); i++){
                            if (userList.getSelectedValue().equals(clientData.userList.get(i).username)){
                                userString = Integer.toString(clientData.userList.get(i).userId);
                                clientData.friends.add(clientData.userList.get(i));
                            }
                        }
                        String toSend = "AddFriend:UserId:" + clientData.userId + ":FriendId:" + userString;
                        try {
                            clientData.network.output.writeObject(toSend);
                        } catch (IOException ex) {
                            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        displayData();
                    }
                });

                menu.add(addFriend);
            } else{
                JMenuItem removeFriend = new JMenuItem("Remove Friend");
                removeFriend.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String userString = "";
                        for (int i = 0; i < clientData.userList.size(); i++){
                            if (userList.getSelectedValue().equals(clientData.userList.get(i).username)){
                                userString = Integer.toString(clientData.userList.get(i).userId);
                                clientData.friends.remove(clientData.userList.get(i));
                            }
                        }
                        String toSend = "RemoveFriend:UserId:" + clientData.userId + ":FriendId:" + userString;
                        try {
                            clientData.network.output.writeObject(toSend);
                        } catch (IOException ex) {
                            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        displayData();
                    }
                });

                JMenuItem messageFriend = new JMenuItem("Message Friend");
                messageFriend.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        boolean sent = false;
                        for (int i = 0; i < clientData.privateChats.size(); i++){
                            if (clientData.privateChats.get(i).name != "Game Chat"){
                                if (clientData.privateChats.get(i).users.get(1).username.equals(friendsList.getSelectedValue())){
                                    chatChannel = clientData.privateChats.get(i).chatId;
                                    sent = true;
                                    displayData();
                                }
                            }
                        }
                        if (sent == false){
                            for (int i = 0; i < clientData.friends.size(); i++){
                                if (clientData.friends.get(i).username.equals(friendsList.getSelectedValue())){
                                    String toSend = "NewChat:UserId:" + Integer.toString(clientData.userId) + ":OtherId:" + Integer.toString(clientData.friends.get(i).userId) + ":END";
                                    try {
                                        clientData.network.output.writeObject(toSend);
                                    } catch (IOException ex) {
                                        Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                        }
                    }
                });
                menu.add(removeFriend);
                menu.add(messageFriend);
            }
            menu.show(userList, evt.getPoint().x, evt.getPoint().y);
        }
    }//GEN-LAST:event_userListMousePressed

    private void friendsListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendsListMousePressed
        if (SwingUtilities.isRightMouseButton(evt)){
            friendsList.setSelectedIndex(friendsList.locationToIndex(evt.getPoint()));

            JPopupMenu menu = new JPopupMenu();
            JMenuItem removeFriend = new JMenuItem("Remove Friend");
            removeFriend.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    String userString = "";
                    for (int i = 0; i < clientData.userList.size(); i++){
                        if (friendsList.getSelectedValue().equals(clientData.userList.get(i).username)){
                            userString = Integer.toString(clientData.userList.get(i).userId);
                            clientData.friends.remove(clientData.userList.get(i));
                        }
                    }
                    String toSend = "RemoveFriend:UserId:" + clientData.userId + ":FriendId:" + userString;
                    try {
                        clientData.network.output.writeObject(toSend);
                    } catch (IOException ex) {
                        Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    displayData();
                }
            });

            JMenuItem messageFriend = new JMenuItem("Message Friend");
            messageFriend.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    boolean sent = false;
                    for (int i = 0; i < clientData.privateChats.size(); i++){
                        if (clientData.privateChats.get(i).name != "GameChat"){
                            if (clientData.privateChats.get(i).users.get(1).username.equals(friendsList.getSelectedValue())){
                                chatChannel = clientData.privateChats.get(i).chatId;
                                sent = true;
                                displayData();
                            }
                        }
                    }
                    if (sent == false){
                        for (int i = 0; i < clientData.friends.size(); i++){
                            if (clientData.friends.get(i).username.equals(friendsList.getSelectedValue())){
                                String toSend = "NewChat:UserId:" + Integer.toString(clientData.userId) + ":OtherId:" + Integer.toString(clientData.friends.get(i).userId) + ":END";
                                try {
                                    clientData.network.output.writeObject(toSend);
                                } catch (IOException ex) {
                                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }

                    }
                }
            });
            menu.add(removeFriend);
            menu.add(messageFriend);
            menu.show(friendsList, evt.getPoint().x, evt.getPoint().y);
        }
    }//GEN-LAST:event_friendsListMousePressed

    private void chatListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chatListMousePressed
        if (SwingUtilities.isRightMouseButton(evt)){
            chatList.setSelectedIndex(chatList.locationToIndex(evt.getPoint()));

            String username = chatList.getSelectedValue();
            boolean friend = false;
            JPopupMenu menu = new JPopupMenu();
            for (int i = 0; i < clientData.userList.size(); i++){
                if (clientData.userList.get(i).username.equals(username)){
                    friend = true;
                }
            }
            if (friend == false){
                JMenuItem addFriend = new JMenuItem("AddFriend");
                addFriend.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String userString = "";
                        for (int i = 0; i < clientData.userList.size(); i++){
                            if (chatList.getSelectedValue().equals(clientData.userList.get(i).username)){
                                userString = Integer.toString(clientData.userList.get(i).userId);
                                clientData.friends.add(clientData.userList.get(i));
                            }
                        }
                        String toSend = "AddFriend:UserId:" + clientData.userId + ":FriendId:" + userString;
                        try {
                            clientData.network.output.writeObject(toSend);
                        } catch (IOException ex) {
                            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        displayData();
                    }
                });

                menu.add(addFriend);
            } else{
                JMenuItem removeFriend = new JMenuItem("Remove Friend");
                removeFriend.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String userString = "";
                        for (int i = 0; i < clientData.userList.size(); i++){
                            if (chatList.getSelectedValue().equals(clientData.userList.get(i).username)){
                                userString = Integer.toString(clientData.userList.get(i).userId);
                                clientData.friends.remove(clientData.userList.get(i));
                            }
                        }
                        String toSend = "RemoveFriend:UserId:" + clientData.userId + ":FriendId:" + userString;
                        try {
                            clientData.network.output.writeObject(toSend);
                        } catch (IOException ex) {
                            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        displayData();
                    }
                });

                JMenuItem messageFriend = new JMenuItem("Message Friend");
                messageFriend.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        boolean sent = false;
                        for (int i = 0; i < clientData.privateChats.size(); i++){
                            if (clientData.privateChats.get(i).name != "Game Chat"){
                                if (clientData.privateChats.get(i).users.get(1).username.equals(friendsList.getSelectedValue())){
                                    chatChannel = clientData.privateChats.get(i).chatId;
                                    sent = true;
                                    displayData();
                                }
                            }
                        }
                        if (sent == false){
                            for (int i = 0; i < clientData.friends.size(); i++){
                                if (clientData.friends.get(i).username.equals(friendsList.getSelectedValue())){
                                    String toSend = "NewChat:UserId:" + Integer.toString(clientData.userId) + ":OtherId:" + Integer.toString(clientData.friends.get(i).userId) + ":END";
                                    try {
                                        clientData.network.output.writeObject(toSend);
                                    } catch (IOException ex) {
                                        Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                        }
                    }
                });
                menu.add(removeFriend);
                menu.add(messageFriend);
            }
            menu.show(chatList, evt.getPoint().x, evt.getPoint().y);
        }
    }//GEN-LAST:event_chatListMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainView().setVisible(true);
            }
        });
    }

//    public void mainLoop(){
//        while(true){
//            String input = "";
//            String[] splitArray = input.split(":");
//            if (splitArray[0].equals("Message")){
//                if (Integer.parseInt(splitArray[2]) == 0){
//                    clientData.publicChat.addMessage(input);
//                } else{
//                    for (int i = 0; i < clientData.privateChats.size(); i++){
//                        if (clientData.privateChats.get(i).chatId == Integer.parseInt(splitArray[2])){
//                            clientData.privateChats.get(i).addMessage(input);
//                        }
//                    }
//                }
//                displayData();
//            }
//            if (splitArray[0].equals("NumberOfPlayers")){
//                for (int i = 0; i < clientData.serverList.size(); i++){
//                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[3])){
//                        clientData.serverList.get(i).numberOfPlayers = Integer.parseInt(splitArray[1]);
//                    }
//                }
//            }
//            if (splitArray[0].equals("Launching")){
//                for (int i = 0; i < clientData.serverList.size(); i++){
//                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[1])){
//                        clientData.serverList.get(i).visible = false;
//                    }
//                }
//            }
//            if (splitArray[0].equals("EndGame")){
//                for (int i = 0; i < clientData.serverList.size(); i++){
//                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[1])){
//                        clientData.serverList.remove(clientData.serverList.get(i));
//                    }
//                }
//            }
//            if(splitArray[0].equals("NewChat")){
//                ChatLog newChat = new ChatLog();
//                newChat.chatId = -1; //DO THIS - FIND A WAY TO ORGANISE CHAT IDS
//                for (int i = 0; i < clientData.userList.size(); i++){
//                    if ((clientData.userList.get(i).userId == Integer.parseInt(splitArray[2]) )|| (clientData.userList.get(i).userId == Integer.parseInt(splitArray[4]))){
//                        newChat.users.add(clientData.userList.get(i));
//                    }
//                }
//                clientData.privateChats.add(newChat);
//                chatChannel = newChat.chatId;
//                displayData();
//            }
//        }
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateGame;
    private javax.swing.JButton btnJoinGame;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnSend;
    private javax.swing.JTextArea chat;
    private javax.swing.JList<String> chatChannels;
    private javax.swing.JList<String> chatList;
    private javax.swing.JList<String> friendsList;
    private javax.swing.JList<String> gameList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea message;
    private javax.swing.JList<String> userList;
    private javax.swing.JTabbedPane userPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(String message) {
        if (message.equals("NOT")){
            displayData();
        } else{
            if (message.equals("ChangeChat")){
                chatChannel = clientData.privateChats.size() - 1;
                displayData();
            }
        }
    }
}
