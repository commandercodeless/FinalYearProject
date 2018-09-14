/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.util.List;

/**
 *
 * @author decla_000
 */
public class Game {
    private Board board = new Board();
    private List<Piece> pieces;
    private GamePlayer white;
    private GamePlayer black;
    public Game() {
        super();
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
    
    public void setColorWhite(GamePlayer player) {
        this.white = player;
    }

    public void setColorBlack(GamePlayer player) {
        this.black = player;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public GamePlayer getWhite() {
        return white;
    }

    public void setWhite(GamePlayer white) {
        this.white = white;
    }

    public GamePlayer getBlack() {
        return black;
    }

    public void setBlack(GamePlayer black) {
        this.black = black;
    }

    public boolean initializeBoardGivenPlayers() {
        if(this.black == null || this.white == null)
            return false;
        this.board = new Board();
        for(int i=0; i<black.getPieces().size(); i++){
            board.getSpot(black.getPieces().get(i).getX(), black.getPieces().get(i).getY()).occupySpot(black.getPieces().get(i));
        }
        return true;
    }

    public void displayBoard(){
        
    }
}