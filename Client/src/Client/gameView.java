/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author decla_000
 */
public class gameView extends javax.swing.JFrame implements IObserver{
    Game thisGame = new Game();
    Client clientData;
    Player hostPlayer;
    public int chatChannel;
    communicationLoop comLoop;
    clientLoop loop1;
    boolean replay = false;
    public String replayName;
    List<hostLoop> loops = new ArrayList();
    
    MainView previousView;
    /**
     * Creates new form gameView
     * @param client
     */
    public gameView() {
        initComponents();
        thisGame.gameType = new GameType("TicTacToe");
        String[] gameArray = new String[2];
        //gameArray[0] = "Chess";
        gameArray[0] = "TicTacToe";
        gameArray[1] = "Draughts";
        //gameArray[2] = "Reversi";
        DefaultComboBoxModel model = new DefaultComboBoxModel(gameArray);
        gameTypeComboBox.setModel(model);
        
        model = new DefaultComboBoxModel(thisGame.gameType.roles);
        roleComboBox.setModel(model);
    }
    
    public void runSetup(connection network){
        //hostPlayer = thisGame.players.get(thisGame.hostId);
        displayData();

        gameTypeComboBox.setSelectedItem(thisGame.gameType.gameName);
        
        if (replay){
            String[] string = new String[1];
            string[0] = "Spectator";
            DefaultComboBoxModel model = new DefaultComboBoxModel(string);
            roleComboBox.setModel(model);
        } else{
            DefaultComboBoxModel model = new DefaultComboBoxModel(thisGame.gameType.roles);
            roleComboBox.setModel(model);
        }
        gameTypeComboBox.setEnabled(false);
        replayBox.setEnabled(false);
        fileChooseButton.setEnabled(false);
        
        btnConfirm.setText("Ready");

        loop1 = new clientLoop();
        loop1.observer = this;
        loop1.network = network;
        loop1.start();
        displayData();
    }
    
    public void runSetupHost(){
        displayData();
    }
    
    public void changeUserRole(String sentText){
        String splitArray[] = sentText.split(":");
        int id = Integer.parseInt(splitArray[1]);
        for (int i = 0; i < thisGame.players.size(); i++){
            if (id == thisGame.players.get(i).userId){
                thisGame.players.get(i).role = splitArray[2];
            }
        }
        displayData();
    }
    
    public void addPlayer(String sentText){
        String splitArray[] = sentText.split(":");
        if (splitArray[0].equals("JoiningUser")){
            String isReplay = "";
            if (replayBox.isSelected()){
                isReplay = "replay";
            } else{
                isReplay = "notReplay";
            }
            String toSend = "Joined:HostId:" + Integer.toString(clientData.userId) + ":HostIp:" + splitArray[8] + ":GameType:" + thisGame.gameType.gameName + ":" + isReplay + ":END";
            connection network = new connection(splitArray[4], 5434);
            try {
                network.output.writeObject(toSend);
                for (int i = 0; i < thisGame.players.size(); i++){
                    toSend = "Player:UserId:" + thisGame.players.get(i).userId + ":Role:" + thisGame.players.get(i).role +":END";
                    network.output.writeObject(toSend);

                }

                network.output.writeObject("ENDPLAYERS");
                
                //need to also send game data
                Player newPlayer = new Player();
                newPlayer.userId = Integer.parseInt(splitArray[2]);
                newPlayer.ip = splitArray[4];
                newPlayer.username = splitArray[6];
                newPlayer.role = "Spectator";
                newPlayer.network = network;
                
                try {
                    network.input = new ObjectInputStream(network.MyClient.getInputStream());
                } catch (IOException ex) {
                    Logger.getLogger(hostLoop.class.getName()).log(Level.SEVERE, null, ex);
                }
                        
                hostLoop newThread = new hostLoop();
                newThread.player = newPlayer;
                newThread.observer = this;
                loops.add(newThread);
                thisGame.players.add(newPlayer);
                thisGame.chat.users.add(newPlayer);
                newThread.start();
            } catch (IOException ex) {
                Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (thisGame.players.size() > 1){
            for (int i = 1; i < thisGame.players.size(); i++){
                try {
                    thisGame.players.get(i).Send(sentText, true);
                } catch (IOException ex) {
                    Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }        
        }

        //connection network = new connection();
//        try {
//            clientData.network.output.writeObject("NumberOfPlayers:" + Integer.toString(thisGame.players.size()) + ":Id:" + thisGame.gameId + ":END");
//        } catch (IOException ex) {
//            Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
//        }
        displayData();
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
        chatChannels.setModel(clientData.chatChannels());
        
        userListModel = new DefaultListModel();
        for (int i = 0; i < thisGame.players.size(); i++){
            userListModel.addElement(thisGame.players.get(i).username + ": " + thisGame.players.get(i).role);
        }
        playerList.setModel(userListModel);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        btnConfirm = new javax.swing.JButton();
        btnLeave = new javax.swing.JButton();
        gameTypeComboBox = new javax.swing.JComboBox<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        playerList = new javax.swing.JList<>();
        roleComboBox = new javax.swing.JComboBox<>();
        replayBox = new javax.swing.JCheckBox();
        fileField = new javax.swing.JTextField();
        fileChooseButton = new javax.swing.JButton();
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

        btnConfirm.setText("Host Game");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        btnLeave.setText("Leave");
        btnLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveActionPerformed(evt);
            }
        });

        gameTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gameTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameTypeComboBoxActionPerformed(evt);
            }
        });

        playerList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        playerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                playerListMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(playerList);

        roleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        roleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roleComboBoxActionPerformed(evt);
            }
        });

        replayBox.setText("Replay");
        replayBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replayBoxActionPerformed(evt);
            }
        });

        fileField.setEditable(false);

        fileChooseButton.setText("...");
        fileChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooseButtonActionPerformed(evt);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane6)
                            .addComponent(gameTypeComboBox, 0, 150, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(replayBox)
                                .addGap(98, 98, 98))
                            .addComponent(roleComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fileField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fileChooseButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLeave, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userPane, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(gameTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(replayBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(fileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fileChooseButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLeave)
                            .addComponent(btnConfirm))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (btnConfirm.getText().equals("Launch Game")){
            //DO THIS - Need to make sure all roles are fulled
            boolean playersReady = true;
            thisGame.players.get(0).ready = true;
            for (int i = 0; i < thisGame.players.size(); i++){
                if (thisGame.players.get(i).ready == false){
                    playersReady = false;
                }
            }
            if (replay == false){
                playersReady = thisGame.gameType.validSetup(thisGame.players);
            }
            if (playersReady == true){
                if (thisGame.players.size() > 1){
                    for (int i = 1; i < thisGame.players.size(); i++){
                        try {
                            thisGame.players.get(i).Send("Launch:END", true);
                        } catch (IOException ex) {
                            Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                try {
                    clientData.network.output.writeObject("Lauching:" + Integer.toString(thisGame.gameId) + ":END");
                } catch (IOException ex) {
                    Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                }
                mainloop("Launch:END");
            }
        }
        if (btnConfirm.getText().equals("Ready")){
            //send to host ready
            String toSend = "READY:" + Integer.toString(clientData.userId) + ":END";
            try {
                hostPlayer.Send(toSend, false);
            } catch (IOException ex) {
                Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
            }
            btnConfirm.setEnabled(false);
        }
        if (btnConfirm.getText().equals("Host Game")){
            //send game to server
            String isReplay;
            if (replayBox.isSelected()){
                isReplay = "replay";
            } else{
                isReplay = "notReplay";
            }
            String toSend = "HostGame:UserId:" + clientData.userId + ":GameType:" + gameTypeComboBox.getSelectedItem().toString() + ":" + isReplay + ":END";          //<----------THIS
            try {
                clientData.network.output.writeObject(toSend);
            } catch (IOException ex) {
                Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
            }
            gameTypeComboBox.setEnabled(false);
            replayBox.setEnabled(false);
            thisGame.gameType = new GameType(gameTypeComboBox.getSelectedItem().toString());
            thisGame.host = clientData.thisPlayer;
            thisGame.chat.users.add(hostPlayer);
            clientData.privateChats.add(thisGame.chat);
            thisGame.chat.name = "GameChat";
            displayData();
            //thisGame.spectators.add(clientData.thisPlayer);
            //thisGame.players.add(clientData.thisPlayer);
            hostPlayer = clientData.thisPlayer;
            thisGame.gameName = thisGame.gameType.gameName;
            btnConfirm.setText("Launch Game");
        }
        
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void btnLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveActionPerformed
        if (hostPlayer.userId == clientData.userId){
            try {
                clientData.network.output.writeObject("EndGame:" + thisGame.gameId + ":END");
            } catch (IOException ex) {
                Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (thisGame.players.size() > 1){
                for (int i = 1; i < thisGame.players.size(); i++){
                    try {
                        thisGame.players.get(i).Send("EndGame:END", true);
                    } catch (IOException ex) {
                        Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else{
            try {
                hostPlayer.Send("Leave:" + Integer.toString(clientData.userId) + ":END", false);
            } catch (IOException ex) {
                Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            previousView.setVisible(true);
            comLoop.observer = previousView;
            previousView.mainLoop = comLoop;
            if (loop1 != null){
                loop1.run = false;
            }
            if (loops != null){
                for (int i = 0; i < loops.size(); i++){
                    loops.get(i).run = false;
                }
            }
            this.dispose();
        } catch (Throwable ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLeaveActionPerformed
  
    private void playerListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerListMousePressed
        if (SwingUtilities.isRightMouseButton(evt)){
            userList.setSelectedIndex(userList.locationToIndex(evt.getPoint()));
            
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addFriend = new JMenuItem("AddFriend");               //<----------THIS
            addFriend.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    //code in here
                    //send to server to add that userid to friends
                    String toSend = "AddFriend:UserId:" + clientData.userId + ":FriendId:" + thisGame.players.get(playerList.getSelectedIndex()).userId;
                    connection network = new connection();
                    try {
                        network.output.writeObject(toSend);
                    } catch (IOException ex) {
                        Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            menu.add(addFriend);
            
            if (hostPlayer.userId == clientData.userId){
                JMenuItem kickPlayer = new JMenuItem("Kick Player");
                kickPlayer.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        //remove player from player list, and send "kicked" message
                        String toSend = "Kicked:UserId:" + clientData.userId + ":END";
                        int id = thisGame.players.get(playerList.getSelectedIndex()).userId;
                        for (int i = 0; i < thisGame.players.size(); i++){
                            if (thisGame.players.get(i).userId == id){
                                try {
                                    thisGame.players.get(i).Send(toSend, true);
                                    thisGame.players.remove(i);
                                } catch (IOException ex) {
                                    Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                });
            }
            menu.show(userList, evt.getPoint().x, evt.getPoint().y);
        }
    }//GEN-LAST:event_playerListMousePressed

    private void gameTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameTypeComboBoxActionPerformed
        thisGame.gameType = new GameType(gameTypeComboBox.getSelectedItem().toString());
        DefaultComboBoxModel model = new DefaultComboBoxModel(thisGame.gameType.roles);
        roleComboBox.setModel(model);
        displayData();
    }//GEN-LAST:event_gameTypeComboBoxActionPerformed

    private void roleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roleComboBoxActionPerformed
        if (thisGame.checkRole(roleComboBox.getSelectedItem().toString(), clientData.userId)){
            for (int i = 0; i < thisGame.players.size(); i++){
                if (thisGame.players.get(i).userId == clientData.userId){
                    thisGame.players.get(i).role = roleComboBox.getSelectedItem().toString();
                    try {
                        if (clientData.userId == hostPlayer.userId){
                            if (thisGame.players.size() > 1){
                                for (int j = 0; j < thisGame.players.size(); j++){
                                    if (thisGame.players.get(j).userId != clientData.userId){
                                        thisGame.players.get(j).Send("RoleUpdate:" + Integer.toString(clientData.userId) + ":" + thisGame.players.get(i).role + ":END", true);
                                    }
                                }
                            }
                        }else{
                            hostPlayer.Send("RoleUpdate:" + Integer.toString(clientData.userId) + ":" + thisGame.players.get(i).role + ":END", false);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        displayData();
    }//GEN-LAST:event_roleComboBoxActionPerformed

    private void fileChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooseButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(gameView.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            replayName = file.getAbsolutePath();
            fileField.setText(replayName);
        }
    }//GEN-LAST:event_fileChooseButtonActionPerformed

    private void replayBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replayBoxActionPerformed
        if (replayBox.isSelected()){
            replay = true;
            fileChooseButton.setEnabled(true);
            String[] string = new String[1];
            string[0] = "Spectator";
            DefaultComboBoxModel model = new DefaultComboBoxModel(string);
            roleComboBox.setModel(model);
            displayData();
        } else{
            replay = false;
            fileChooseButton.setEnabled(false);
            thisGame.gameType = new GameType(gameTypeComboBox.getSelectedItem().toString());
            DefaultComboBoxModel model = new DefaultComboBoxModel(thisGame.gameType.roles);
            roleComboBox.setModel(model);
            displayData();
        }
    }//GEN-LAST:event_replayBoxActionPerformed

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
            java.util.logging.Logger.getLogger(gameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(gameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(gameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(gameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new gameView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnLeave;
    private javax.swing.JButton btnSend;
    private javax.swing.JTextArea chat;
    private javax.swing.JList<String> chatChannels;
    private javax.swing.JList<String> chatList;
    private javax.swing.JButton fileChooseButton;
    private javax.swing.JTextField fileField;
    private javax.swing.JList<String> friendsList;
    private javax.swing.JComboBox<String> gameTypeComboBox;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea message;
    private javax.swing.JList<String> playerList;
    private javax.swing.JCheckBox replayBox;
    private javax.swing.JComboBox<String> roleComboBox;
    private javax.swing.JList<String> userList;
    private javax.swing.JTabbedPane userPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(String message) {
        if (message.equals("NOT")){
            displayData();
        } else{
            mainloop(message);
        }
    }
    
    public void mainloop(String input){
        String splitArray[] = input.split(":");
        if (hostPlayer.userId == clientData.userId){
        //listen for server commands

        if (splitArray[0].equals("JoiningUser")){
            addPlayer(input);
        }
        if (splitArray[0].equals("RoleUpdate")){       //"RoleUpdate:" + Integer.toString(clientData.userId) + ":" + thisGame.players.get(i).role + ":END");
            changeUserRole(input);
            for (int i = 1; i < thisGame.players.size(); i++){
                if (thisGame.players.get(i).userId != Integer.parseInt(splitArray[1])){
                    try {
                        thisGame.players.get(i).Send(input, true);
                    } catch (IOException ex) {
                        Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (splitArray[0].equals("READY")){
            for (int i = 0; i < thisGame.players.size(); i++){
                if (thisGame.players.get(i).userId == Integer.parseInt(splitArray[1])){
                    thisGame.players.get(i).ready = true;
                }
            }
        }
        if (splitArray[0].equals("Leave")){
            for (int i = 0; i < thisGame.players.size(); i++){
                if (thisGame.players.get(i).userId == Integer.parseInt(splitArray[1])){
                    thisGame.players.remove(thisGame.players.get(i));
                } else{
                    try {
                        thisGame.players.get(i).Send(input, true);
                    } catch (IOException ex) {
                        Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        } else{
            //listen for host commands                         //<-----------------------PUT IN THREAD - DONE
            if (splitArray[0].equals("RoleUpdate")){       //"RoleUpdate:" + Integer.toString(clientData.userId) + ":" + thisGame.players.get(i).role + ":END");
                changeUserRole(input);
            }
            if (splitArray[0].equals("Message")){
                if (Integer.parseInt(splitArray[2]) == chatChannel){
                    thisGame.chat.addMessage(input);
                } else{
                    if (Integer.parseInt(splitArray[2]) == 0){
                        clientData.publicChat.addMessage(input);
                    } else{
                        for (int i = 0; i < clientData.privateChats.size(); i++){
                            if (clientData.privateChats.get(i).chatId == Integer.parseInt(splitArray[2])){
                                clientData.privateChats.get(i).addMessage(input);
                            }
                        }
                    }
                }
                displayData();
            }
            if (splitArray[0].equals("Leave")){
                for (int i = 0; i < thisGame.players.size(); i++){
                    if (thisGame.players.get(i).userId == Integer.parseInt(splitArray[1])){
                        thisGame.players.remove(thisGame.players.get(i));
                    } 
                }
            }
            if (splitArray[0].equals("EndGame")){
                for (int i = 0; i < clientData.serverList.size(); i++){
                    if (clientData.serverList.get(i).gameId == Integer.parseInt(splitArray[1])){
                        clientData.serverList.remove(clientData.serverList.get(i));
                    }
                }
                try {
                    this.dispose();
                } catch (Throwable ex) {
                    Logger.getLogger(gameView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (splitArray[0].equals("Launch")){
                //LAUNCH DO THIS
                if (thisGame.gameType.gameName.equals("Chess")){
                    Chess.GameUI newUI = new Chess.GameUI();
                    newUI.clientData = clientData;
                    newUI.chatChannel = chatChannel;
                    newUI.toRemove = thisGame;
                    for (int i = 0; i < thisGame.players.size(); i++){
                        Chess.GamePlayer newPlayer = new Chess.GamePlayer();
                        newPlayer.player = thisGame.players.get(i);
                        newPlayer.team = thisGame.players.get(i).role;
                        if (hostPlayer.userId == newPlayer.player.userId){
                            newUI.host = newPlayer;
                            if (hostPlayer.userId == clientData.userId){
                                newUI.isHost = true;
                            }
                        }
                        if (newPlayer.team.equals("White")){
                            newUI.white = newPlayer;
                            if (newPlayer.player.userId == clientData.userId){
                                newUI.takeTurn = true;
                            }
                        }
                        if (newPlayer.team.equals("Black")){
                            newUI.black = newPlayer;
                        }
                        if (thisGame.players.get(i).role.equals("Spectator")){
                            newUI.spectators.add(newPlayer);
                        }
                        
                        //is this player included in that list?
                    }
                    newUI.chatChannel = chatChannel;
                    newUI.comLoop = comLoop;
                    comLoop.observer = newUI;
                    if (loop1 != null){
                        newUI.loop1 = loop1;
                        loop1.observer = newUI;
                    }
                    if (loops != null){
                        for (int i = 0; i < loops.size(); i++){
                            newUI.loops.add(loops.get(i));
                            loops.get(i).observer = newUI;
                        }
                    }
                    if (replay = true && (hostPlayer.userId == clientData.userId)){
                        newUI.replayName = replayName;
                        newUI.doReplay = true;
                        newUI.replay();
                    }
                    newUI.previousView = previousView;
                    this.setVisible(false);
                    newUI.setVisible(true);
                    newUI.run();
                }
                if (thisGame.gameType.gameName.equals("TicTacToe")){
                    TicTacToe.GameUI newUI = new TicTacToe.GameUI();
                    newUI.clientData = clientData;
                    newUI.toRemove = thisGame;
                    if (hostPlayer.userId == clientData.userId){
                        newUI.isHost = true;
                    } else{
                        newUI.isHost = false;
                    }
                    for (int i = 0; i < thisGame.players.size(); i++){
                        TicTacToe.GamePlayer newPlayer = new TicTacToe.GamePlayer();
                        newPlayer.player = thisGame.players.get(i);
                        if (thisGame.players.get(i).role.equals("Noughts")){
                            newPlayer.team = 0;
                            newUI.noughts = newPlayer;
                            if (thisGame.players.get(i).userId == clientData.userId){
                                newUI.takeTurn = true;
                            }
                        }
                        if (thisGame.players.get(i).role.equals("Crosses")){
                            newPlayer.team = 1;
                            newUI.crosses = newPlayer;
                        }
                        if (thisGame.players.get(i).role.equals("Spectator")){
                            if (newPlayer.player.userId != hostPlayer.userId){
                                newUI.spectators.add(newPlayer);
                            }
                        }
                        if (thisGame.players.get(i).userId == hostPlayer.userId){
                            newUI.host = newPlayer;
                        }
                    }
                    newUI.chatChannel = chatChannel;
                    newUI.previousView = previousView;
                    newUI.comLoop = comLoop;
                    comLoop.observer = newUI;
                    if (loop1 != null){
                        newUI.loop1 = loop1;
                        loop1.observer = newUI;
                    }
                    if (loops != null){
                        for (int i = 0; i < loops.size(); i++){
                            newUI.loops.add(loops.get(i));
                            loops.get(i).observer = newUI;
                        }
                    }
                    newUI.displayData();
                    this.setVisible(false);
                    newUI.setVisible(true);
                    if (replay = true && (hostPlayer.userId == clientData.userId)){
                        newUI.replayName = replayName;
                        newUI.doReplay = true;
                        newUI.replay();
                    }

                    
                }
                if (thisGame.gameType.gameName.equals("Draughts")){
                    Draughts.GameUI newUI = new Draughts.GameUI();
                    newUI.clientData = clientData;
                    newUI.chatChannel = chatChannel;
                    newUI.toRemove = thisGame;
                    for (int i = 0; i < thisGame.players.size(); i++){
                        Draughts.GamePlayer newPlayer = new Draughts.GamePlayer();
                        newPlayer.player = thisGame.players.get(i);
                        newPlayer.team = thisGame.players.get(i).role;
                        if (hostPlayer.userId == newPlayer.player.userId){
                            newUI.host = newPlayer;
                            if (hostPlayer.userId == clientData.userId){
                                newUI.isHost = true;
                            }
                        }
                        if (newPlayer.team.equals("White")){
                            newUI.white = newPlayer;
                            if (newPlayer.player.userId == clientData.userId){
                                newUI.takeTurn = true;
                            }
                        }
                        if (newPlayer.team.equals("Black")){
                            newUI.black = newPlayer;
                        }
                        if (thisGame.players.get(i).role.equals("Spectator")){
                            newUI.spectators.add(newPlayer);
                        }
                        
                        //is this player included in that list?
                    }
                    newUI.chatChannel = chatChannel;
                    newUI.comLoop = comLoop;
                    comLoop.observer = newUI;
                    if (loop1 != null){
                        newUI.loop1 = loop1;
                        loop1.observer = newUI;
                    }
                    if (loops != null){
                        for (int i = 0; i < loops.size(); i++){
                            newUI.loops.add(loops.get(i));
                            loops.get(i).observer = newUI;
                        }
                    }
                    if (replay == true && (hostPlayer.userId == clientData.userId)){
                        newUI.replayName = replayName;
                        newUI.doReplay = true;
                        newUI.replay();
                    }
                    newUI.previousView = previousView;
                    newUI.displayData();
                    this.setVisible(false);
                    newUI.setVisible(true);
                    //newUI.run();
                }
                if (thisGame.gameType.gameName.equals("Reversi")){
                    Reversi.GameUI newUI = new Reversi.GameUI();
                    newUI.clientData = clientData;
                    newUI.chatChannel = chatChannel;
                    newUI.toRemove = thisGame;
                    for (int i = 0; i < thisGame.players.size(); i++){
                        Reversi.GamePlayer newPlayer = new Reversi.GamePlayer();
                        newPlayer.player = thisGame.players.get(i);
                        newPlayer.team = thisGame.players.get(i).role;
                        if (hostPlayer.userId == newPlayer.player.userId){
                            newUI.host = newPlayer;
                            if (hostPlayer.userId == clientData.userId){
                                newUI.isHost = true;
                            }
                        }
                        if (newPlayer.team.equals("White")){
                            newUI.white = newPlayer;
                            if (newPlayer.player.userId == clientData.userId){
                                newUI.takeTurn = true;
                            }
                        }
                        if (newPlayer.team.equals("Black")){
                            newUI.black = newPlayer;
                        }
                        if (thisGame.players.get(i).role.equals("Spectator")){
                            newUI.spectators.add(newPlayer);
                        }
                        
                        //is this player included in that list?
                    }
                    newUI.chatChannel = chatChannel;
                    newUI.comLoop = comLoop;
                    comLoop.observer = newUI;
                    if (loop1 != null){
                        newUI.loop1 = loop1;
                        loop1.observer = newUI;
                    }
                    if (loops != null){
                        for (int i = 0; i < loops.size(); i++){
                            newUI.loops.add(loops.get(i));
                            loops.get(i).observer = newUI;
                        }
                    }
                    if (replay = true && (hostPlayer.userId == clientData.userId)){
                        newUI.replayName = replayName;
                        newUI.doReplay = true;
                        newUI.replay();
                    }
                    newUI.previousView = previousView;
                    newUI.displayData();
                    this.setVisible(false);
                    newUI.setVisible(true);
                    newUI.run();
                }
                for (int j = 0; j < clientData.serverList.size(); j++){
                    if (clientData.serverList.get(j).host.userId == hostPlayer.userId){
                        clientData.serverList.get(j).visible = false;
                    }
                }
                this.dispose();
            }
        displayData();
    }
}
