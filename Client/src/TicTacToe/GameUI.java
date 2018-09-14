/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TicTacToe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import Client.ChatLog;
import Client.Game;
import Client.IObserver;
import Client.MainView;
import Client.clientLoop;
import Client.communicationLoop;
import Client.connection;
import Client.hostLoop;

/**
 *
 * @author decla_000
 */
public class GameUI extends javax.swing.JFrame implements IObserver{
    
    private static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static Dimension BOARD_PANEL_DIMENSION = new Dimension(320,320);
    private static Dimension TILE_PANEL_DIMENSION = new Dimension(100,100);
    
    public Board board = new Board();
    public GamePlayer noughts;
    public GamePlayer crosses;
    public GamePlayer host;
    public boolean isHost;
    public boolean takeTurn;
    public BoardPanel boardPanel;
    public List<TilePanel> tiles = new ArrayList();
    public List<Action> replay = new ArrayList();
    public List<Action> toReplay = new ArrayList();
    public Client.Game toRemove;
    public boolean doReplay = false;
    public String replayName;
    private BufferedReader in;
    
    private int turn = 0;
    public List<GamePlayer> spectators = new ArrayList();
    public int chatChannel;
    
    public Client.Client clientData;
    public MainView previousView;
    
    public communicationLoop comLoop;
    public clientLoop loop1;
    public List<hostLoop> loops = new ArrayList();

    /**
     * Creates new form GameUI
     */
    public GameUI() {
        initComponents();
        displayBoard();
    }
    
    public void replay(){
        if (doReplay == true){
            try {
                in = new BufferedReader(new FileReader(replayName)); 
                String line = in.readLine();
                
                Action newAction = new Action();
                while(line != null){
                    newAction = newAction.stringToClass(line);
                    toReplay.add(newAction);
                    line = in.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            replayLoop loop = new replayLoop();
            loop.observer = this;
            loop.spectators = spectators;
            loop.toReplay = toReplay;
            loop.start();
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
        chatChannels.setModel(clientData.chatChannels());
    }
    public void displayBoard(){
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setOpaque(true);
        gameFrame.setBackground(Color.black);
        gameFrame.add(new BoardPanel(), BorderLayout.CENTER);
    }

    @Override
    public void update(String message) {
        if (message.equals("NOT")){
            displayData();
        } else{
            if (message.equals("ChangeChat")){
                chatChannel = clientData.privateChats.size() - 1;
                displayData();
            } else{
                mainLoop(message);
            }
        }
    }
    
    public void mainLoop(String input){
        String[] splitArray = input.split(":");
        if (splitArray[0].equals("Turn")){
            if (isHost){
                if (spectators.size() > 0){
                    for (int i = 0; i < spectators.size(); i++){
                        if (spectators.get(i).player.userId != host.player.userId){
                            try {
                                spectators.get(i).player.Send(input, isHost);
                            } catch (IOException ex) {
                                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
            Action newAction = new Action();
            newAction = newAction.stringToClass(input);
            doAction(newAction);
        }
    }
    
    public void doAction(Action thisTurn){
        for (int i = 0; i < tiles.size(); i++){
            if (tiles.get(i).x == thisTurn.x && tiles.get(i).y == thisTurn.y){
                if (turn == 0){
                    try {
                        board.tiles[thisTurn.x][thisTurn.y] = 0; //Player team
                        turn = 1;
                        BufferedImage image = ImageIO.read(new File("ticTacToe\\Circle.png"));
                        JLabel picLabel = new JLabel(new ImageIcon(image));
                        tiles.get(i).add(picLabel);
                        if (crosses != null){
                            if (crosses.player.userId == clientData.userId){
                                takeTurn = true;
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else{
                    try {
                        board.tiles[thisTurn.x][thisTurn.y] = 1;
                        turn = 0;
                        BufferedImage image = ImageIO.read(new File("ticTacToe\\Cross.png"));
                        JLabel picLabel = new JLabel(new ImageIcon(image));
                        tiles.get(i).add(picLabel);
                        if (noughts != null){
                            if (noughts.player.userId == clientData.userId){
                                takeTurn = true;
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            tiles.get(i).revalidate();
            tiles.get(i).repaint();
        }
        replay.add(thisTurn);
        revalidate();
        repaint();

            
        if (thisTurn.victory){
            victory(thisTurn.player);
        }
        if (board.checkTie()){
            victory(-1);
        }
    }
    
    public void victory(int playerId){
        String winMessage = "";
        if (playerId == -1){
            winMessage = "A Tie";
        } else{
            for (int i = 0; i < clientData.userList.size(); i++){
                if (clientData.userList.get(i).userId == playerId){
                    if (playerId == clientData.userId){
                        winMessage = "You Win!";
                    } else{
                        winMessage = clientData.userList.get(i).username + " Won!";
                    }                    
                }
            }
        }
        gameFrame.removeAll();
        gameFrame.revalidate();
        gameFrame.repaint();
        javax.swing.JLabel message = new javax.swing.JLabel();
        message.setText(winMessage);
        javax.swing.JButton btnLeave = new javax.swing.JButton();
        btnLeave.setText("Leave");
        btnLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leave();
            }
        });
        javax.swing.JTextField fileNameBox = new javax.swing.JTextField();

        javax.swing.JButton btnSave = new javax.swing.JButton();
        btnSave.setText("Save Replay");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveReplay(fileNameBox.getText());
                leave();
            }
        });
        gameFrame.setBackground(Color.white);
        GridLayout experimentLayout = new GridLayout();
        gameFrame.setLayout(experimentLayout);
        gameFrame.add(message);
        gameFrame.add(btnLeave);
        gameFrame.add(fileNameBox);
        gameFrame.add(btnSave);
        gameFrame.revalidate();
        gameFrame.repaint();
    }
    
    private void saveReplay(String filename){
        try {
            PrintWriter writer = new PrintWriter(filename + ".txt", "UTF-8");
            for (int i = 0; i < replay.size(); i++){
                writer.println(replay.get(i).classToString());
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void leave(){
        if (isHost){
            try {
                clientData.network.output.writeObject("EndGame:" + toRemove.gameId + ":END");
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < clientData.privateChats.size(); i++){
            if (clientData.privateChats.get(i).name.equals("GameChat")){
                clientData.privateChats.remove(clientData.privateChats.get(i));
            }
        }
        previousView.setVisible(true);
        previousView.chatChannel = chatChannel;
        previousView.mainLoop = comLoop;
        comLoop.observer = previousView;
        clientData.serverList.remove(toRemove);
        previousView.clientData = clientData;
        previousView.displayData();
        if (loop1 != null){
            loop1.run = false;
            loop1.network.closeConnection();
                   
        }
        if (loops != null){
            for (int i = 0; i < loops.size(); i++){
                loops.get(i).run = false;
            }
        }
        this.dispose();
    }
    
    private class BoardPanel extends JPanel{
        //List<TilePanel> tiles = new ArrayList<>();
        
        BoardPanel(){

            for (int i = 0; i < 3; i++){
                for (int j = 0; j < 3; j++){
                    TilePanel toAdd = new TilePanel(i,j);
                    tiles.add(toAdd);
                    add(toAdd);
                }
            }
            boardPanel = this;
            this.setBackground(Color.black);
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();

        }
    }

    private class TilePanel extends JPanel{
        int tileId;
        int x;
        int y;
        TilePanel(int i, int j){
            super(new GridBagLayout());
            tileId = (i * 3) + j;
            x = i;
            y = j;
            setPreferredSize(TILE_PANEL_DIMENSION);
            if (board.tiles[i][j] == 0){
                try {
                    BufferedImage image = ImageIO.read(new File("ticTacToe\\Circle.png"));
                    JLabel picLabel = new JLabel(new ImageIcon(image));
                    add(picLabel);
                } catch (IOException ex) {
                    Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else{
                if (board.tiles[i][j] == 1){
                    try {
                        BufferedImage image = ImageIO.read(new File("ticTacToe\\Cross.png"));
                        JLabel picLabel = new JLabel(new ImageIcon(image));
                        add(picLabel);
                    } catch (IOException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else{
                    
                }
            }
            
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1){
                        if (takeTurn){
                            if (board.tiles[x][y] == -1){
                                if (turn == 0){
                                    try {
                                        board.tiles[x][y] = 0; //Player team
                                        turn = 1;
                                        BufferedImage image = ImageIO.read(new File("ticTacToe\\Circle.png"));
                                        JLabel picLabel = new JLabel(new ImageIcon(image));
                                        add(picLabel);
                                    } catch (IOException ex) {
                                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else{
                                    try {
                                        board.tiles[x][y] = 1;
                                        turn = 0;
                                        BufferedImage image = ImageIO.read(new File("ticTacToe\\Cross.png"));
                                        JLabel picLabel = new JLabel(new ImageIcon(image));
                                        add(picLabel);
                                    } catch (IOException ex) {
                                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                takeTurn = false;
                                Action newAction = new Action();
                                newAction.x = x;
                                newAction.y = y;
                                newAction.player = clientData.userId;
                                if (turn == 0){
                                    newAction.victory = board.checkVictory(1);
                                } else{
                                    newAction.victory = board.checkVictory(0);
                                }
                                replay.add(newAction);
                                if (isHost){
                                    if (turn == 0){
                                        try {
                                            noughts.player.Send(newAction.classToString(), isHost);
                                        } catch (IOException ex) {
                                            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else{
                                        try {
                                            crosses.player.Send(newAction.classToString(), isHost);
                                        } catch (IOException ex) {
                                            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                        }                                    
                                    }
                                    if (spectators.size() > 0){
                                        for (int i = 0; i < spectators.size(); i++){
                                            try {
                                                spectators.get(i).player.Send(newAction.classToString(), isHost);
                                            } catch (IOException ex) {
                                                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                } else{
                                    try {
                                        host.player.Send(newAction.classToString(), false);
                                    } catch (IOException ex) {
                                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                revalidate();
                                repaint();
                                TilePanel.this.revalidate();
                                TilePanel.this.repaint();
                                if (newAction.victory){
                                    victory(clientData.userId);
                                }
                                if (board.checkTie()){
                                    victory(-1);
                                }
                            }
                        }
                    }
                }
                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
                
            });
            validate();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gameFrame = new javax.swing.JPanel();
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
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout gameFrameLayout = new javax.swing.GroupLayout(gameFrame);
        gameFrame.setLayout(gameFrameLayout);
        gameFrameLayout.setHorizontalGroup(
            gameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        gameFrameLayout.setVerticalGroup(
            gameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

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

        jButton1.setText("Leave Game");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gameFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(79, 79, 79)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userPane, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
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
                            .addComponent(btnSend, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(gameFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        leave();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(GameUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JTextArea chat;
    private javax.swing.JList<String> chatChannels;
    private javax.swing.JList<String> chatList;
    private javax.swing.JList<String> friendsList;
    private javax.swing.JPanel gameFrame;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea message;
    private javax.swing.JList<String> userList;
    private javax.swing.JTabbedPane userPane;
    // End of variables declaration//GEN-END:variables
}
