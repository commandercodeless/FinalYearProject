/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import Client.MainView;
import Client.clientLoop;
import Client.communicationLoop;
import Client.hostLoop;

/**
 *
 * @author decla_000
 */
public class GameUI extends javax.swing.JFrame implements Client.IObserver{

    private static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,400);
    private static Dimension TILE_PANEL_DIMENSION = new Dimension(45,45);
    private Board board = new Board();
    private List<Piece> pieces = new ArrayList();
    public GamePlayer white;
    public GamePlayer black;
    private int ids = 0;
    public GamePlayer host;
    public boolean isHost;
    private int turn = 0;
    public boolean takeTurn;
    
    private TilePanel oldTile;
    private int startx = -1;
    private int starty = -1;
    private int endx = -1;
    private int endy = -1;
    private BoardPanel boardPanel;
    
    public boolean doReplay = false;
    public String replayName;
    private BufferedReader in;
    private List<Action> toReplay = new ArrayList();
    public List<GamePlayer> spectators = new ArrayList();
    private List<Action> replay = new ArrayList();
    
    public Client.Client clientData;
    public int chatChannel;

    public communicationLoop comLoop;
    public clientLoop loop1;
    public List<hostLoop> loops = new ArrayList();
    public MainView previousView;
    public List<TilePanel> tiles = new ArrayList<>();
    public Client.Game toRemove;
    /**
     * Creates new form GameUI
     */
    public GameUI(){
        initComponents();
        makePieces();
        displayBoard();
        //mainLoop();
    }
    
    public void run(){
        displayData();
        displayBoard();
        if (doReplay == true){
            try {
                in = new BufferedReader(new FileReader(replayName)); 
                String line = in.readLine();
                
                Action newAction = new Action();
                while(line != null){
                    toReplay.add(newAction.StringToClass(line));
                    line = in.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    public void replay(){
        if (doReplay == true){
            try {
                in = new BufferedReader(new FileReader(replayName)); 
                String line = in.readLine();
                
                Action newAction = new Action();
                while(line != null){
                    newAction = newAction.StringToClass(line);
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
    
    public void doReplay(){
                            for (int i = 0; i < toReplay.size(); i++){
                        for (int j = 0; j < spectators.size(); j++){
                            try {
                                spectators.get(j).player.Send(toReplay.get(i).classToString(), true);
                            } catch (IOException ex) {
                                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                            }    
                        }
                        try {
                            wait(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
    }
    
    public void mainLoop(String input){
        if (isHost){
            if (doReplay == false){
                String[] splitArray = input.split(":");
                if (splitArray[0].equals("Turn")){
                    Action thisTurn = new Action();
                    thisTurn.StringToClass(input);
                    try {

                        if (spectators.size() > 0){
                            for (int i = 0; i < spectators.size(); i++){
                                spectators.get(i).player.Send(input, true);
                            }
                        }
                        if (turn == 1 && host.player.userId != white.player.userId){
                            white.player.Send(input, true);
                            if (black.player.userId != host.player.userId){
                                //black.player.Send("TakeTurn:END", true);
                            } else{
                                takeTurn = true;
                            }
                        }
                        if (turn == 0 && host.player.userId != black.player.userId){
                            black.player.Send(input, true);
                            if (white.player.userId != host.player.userId){
                                //white.player.Send("TakeTurn:END", true);
                            } else{
                                takeTurn = true;
                            }
                        }
                        doTurn(thisTurn);
                    } catch (IOException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            //coordinate game
        } else{
            String[] splitArray = input.split(":");
            if (splitArray[0].equals("Turn")){
                Action thisTurn = new Action();
                thisTurn.StringToClass(input);
                doTurn(thisTurn);
            }
            if (splitArray[0].equals("TakeTurn")){
                takeTurn = true;
            }
        }
    }
    
    private void doTurn(Action thisTurn){
        Tile old = board.tiles[thisTurn.startLocationX][thisTurn.startLocationY];
        Tile newTile = board.tiles[thisTurn.endLocationX][thisTurn.endLocationY];
        newTile.piece = old.piece;
        old.piece = null;

        TilePanel newPanel = tiles.get((thisTurn.endLocationX * 8) + thisTurn.endLocationY);
        TilePanel oldPanel = tiles.get((thisTurn.startLocationX * 8) + thisTurn.startLocationY);
        try {
            BufferedImage image;
            String teamString;
            if (board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece.team == 0){
                teamString = "White";
                if (black.player.userId == clientData.userId){
                    takeTurn = true;
                }
            } else{
                teamString = "Black";
                if (white.player.userId == clientData.userId){
                    takeTurn = true;
                }
            }
            image = ImageIO.read(new File("chess\\" + teamString + board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece.pieceName + ".png"));
            JLabel picLabel = new JLabel(new ImageIcon(image));
            
            oldPanel.remove(oldPanel.picLabel);
            oldPanel.setBackground(oldPanel.normalColour);
            if (newPanel.picLabel != null){
                newPanel.remove(newPanel.picLabel);
            }
            newPanel.add(picLabel);
            newPanel.picLabel = picLabel;
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }

            revalidate();
            repaint();
    }

    
    private void makePieces(){
        addPiece(new Piece(ids,0,0,0,"Rook"));
        addPiece(new Piece(ids,0,1,0,"Knight"));
        addPiece(new Piece(ids,0,2,0,"Bishop"));
        addPiece(new Piece(ids,0,3,0,"King"));
        addPiece(new Piece(ids,0,4,0,"Queen"));
        addPiece(new Piece(ids,0,5,0,"Bishop"));
        addPiece(new Piece(ids,0,6,0,"Knight"));
        addPiece(new Piece(ids,0,7,0,"Rook"));
        for (int i = 0; i < 8; i++){
            addPiece(new Piece(ids,1,i,0,"Pawn"));
        }
        addPiece(new Piece(ids,7,0,1,"Rook"));
        addPiece(new Piece(ids,7,1,1,"Knight"));
        addPiece(new Piece(ids,7,2,1,"Bishop"));
        addPiece(new Piece(ids,7,3,1,"King"));
        addPiece(new Piece(ids,7,4,1,"Queen"));
        addPiece(new Piece(ids,7,5,1,"Bishop"));
        addPiece(new Piece(ids,7,6,1,"Knight"));
        addPiece(new Piece(ids,7,7,1,"Rook"));
        for (int i = 0; i < 8; i++){
            addPiece(new Piece(ids,6,i,1,"Pawn"));
        }
    }
    
    private void addPiece(Piece pieceToAdd){
        pieces.add(pieceToAdd);
        board.add(pieceToAdd);
        ids++;
    }
    
    public void doAction(Action thisTurn){
        Piece movedPiece = pieces.get(thisTurn.pieceMoved);
        board.tiles[movedPiece.x][movedPiece.y].piece = null;
        
        if (board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece != null){
            board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece.taken = true;
        }
        movedPiece.x = thisTurn.endLocationX;
        movedPiece.y = thisTurn.endLocationY;
        
        board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece = movedPiece;
        pieces.set(thisTurn.pieceMoved, movedPiece);
        
        displayBoard();
    }

    public void displayBoard(){
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setBackground(Color.white);
        gameFrame.add(new BoardPanel(), BorderLayout.CENTER);
    }
    
    private class BoardPanel extends JPanel{

        
        BoardPanel(){
            for (int i = 0; i < 8; i++){
                for (int j = 0; j < 8; j++){
                    TilePanel toAdd = new TilePanel(i,j);
                    tiles.add(toAdd);
                    add(toAdd);
                }
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            boardPanel = BoardPanel.this;
            validate();
        }
    }

    private class TilePanel extends JPanel{
        int tileId;
        int x;
        int y;
        JLabel picLabel;
        Color normalColour;
        
        TilePanel(int i, int j){
            super(new GridBagLayout());
            tileId = (i * 8) + j;
            x = i;
            y = j;
            setPreferredSize(TILE_PANEL_DIMENSION);
            tileColour(i,j);
            if (board.tiles[i][j].piece != null){
                try {
                    String teamString;
                    if (board.tiles[i][j].piece.team == 0){
                        teamString = "White";
                    } else{
                        teamString = "Black";
                    }
                    BufferedImage image = ImageIO.read(new File("chess\\" + teamString +board.tiles[i][j].piece.pieceName + ".png"));
                    picLabel = new JLabel(new ImageIcon(image));
                    add(picLabel);
                } catch (IOException ex) {
                    Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (takeTurn){
                    if (e.getButton() == 2){
                        if (startx == -1){
                            
                        }else
                        {
                            startx = -1;
                            starty = -1;
                        }
                    } else{
                        if (e.getButton() == 1){
                            if (startx == -1){
                                if (board.tiles[x][y].isOccupied()){
                                    if (turn == board.tiles[x][y].piece.team){
                                        startx = x;
                                        starty = y;
                                        TilePanel.this.setBackground(Color.green);
                                        oldTile = TilePanel.this;
                                    }
                                }
                            } else{
                                endx = x;
                                endy = y;
                                if (board.checkValid(startx, starty, endx, endy, board.tiles[startx][starty].piece.pieceName ,board.tiles[startx][starty].piece.team)){
                                    board.tiles[endx][endy].piece = board.tiles[startx][starty].piece; //do take piece stuff
                                    board.tiles[startx][starty].piece = null;
                                    //GENERATE AN ACTION AND SEND TO HOST
                                    Action thisTurn = new Action();
                                    thisTurn.startLocationX = startx;
                                    thisTurn.startLocationY = starty;
                                    thisTurn.endLocationX = endx;
                                    thisTurn.endLocationY = endy;
                                    
                                    replay.add(thisTurn);
                                    if (isHost){
                                        try {
                                            //send to other players
                                            if (spectators.size() > 0){
                                                for (int i = 0; i < spectators.size(); i++){
                                                    spectators.get(i).player.Send(thisTurn.classToString(), true);
                                                }
                                            }
                                            if (turn == 1){
                                                white.player.Send(thisTurn.classToString(), true);
                                                //white.player.Send("TakeTurn:END", true);
                                                takeTurn = false;
                                            }
                                            if (turn == 0){
                                                black.player.Send(thisTurn.classToString(), true);
                                                //black.player.Send("TakeTurn:END", true);
                                                takeTurn = false;
                                            }
                                        } catch (IOException ex) {
                                            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else{
                                        try {
                                            //send to host
                                            host.player.Send(thisTurn.classToString(), false);
                                            takeTurn = false;
                                        } catch (IOException ex) {
                                            Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    
                                    startx = -1;
                                    starty = -1;
                                    if (turn == 0){
                                        turn = 1;
                                    } else{
                                        if (turn == 1){
                                            turn = 0;
                                        }
                                    }

                                    try {
                                        BufferedImage image;
                                        String teamString;
                                        if (board.tiles[i][j].piece.team == 0){
                                            teamString = "White";
                                        } else{
                                            teamString = "Black";
                                        }
                                        image = ImageIO.read(new File("chess\\" + teamString +board.tiles[i][j].piece.pieceName + ".png"));
                                        JLabel picLabel = new JLabel(new ImageIcon(image));
                                        
                                        oldTile.remove(oldTile.picLabel);
                                        oldTile.setBackground(oldTile.normalColour);
                                        if (TilePanel.this.picLabel != null){
                                            TilePanel.this.remove(TilePanel.this.picLabel);
                                        }
                                        TilePanel.this.add(picLabel);
                                        TilePanel.this.picLabel = picLabel;
                                    } catch (IOException ex) {
                                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    revalidate();
                                    repaint();
                                } else{
                                    startx = -1;
                                    starty = -1;
                                    oldTile.setBackground(oldTile.normalColour);
                                }
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
            this.setOpaque(true);
            validate();
        }
        
        private void tileColour(int i, int j){
            if (i%2 == 0){
                if (j%2 == 0){
                    setBackground(Color.white);
                    normalColour = Color.white;
                } else{
                    setBackground(new Color(139,69,19));
                    normalColour = new Color(139,69,19);
                }
            } else{
                if (j%2 == 0){
                    setBackground(new Color(139,69,19));
                    normalColour = new Color(139,69,19);
                }else{
                    setBackground(Color.white);
                    normalColour = Color.white;
                }
            }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout gameFrameLayout = new javax.swing.GroupLayout(gameFrame);
        gameFrame.setLayout(gameFrameLayout);
        gameFrameLayout.setHorizontalGroup(
            gameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );
        gameFrameLayout.setVerticalGroup(
            gameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gameFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                            .addComponent(btnSend, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(gameFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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

    /**
     * @param args the command line arguments
     */
    public void main(String args[]) {
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
    @Override
    public void update(String message) {
        if (message.equals("NOT")){
            displayData();
        } else{
            mainLoop(message);
        }
    }
}
