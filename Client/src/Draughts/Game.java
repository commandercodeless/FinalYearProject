/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Draughts;

import Chess.*;
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

//    public void doAction(Action thisTurn){
//        Piece movedPiece = pieces.get(thisTurn.pieceMoved);
//        board.tiles[movedPiece.x][movedPiece.y].piece = null;
//        
//        if (board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece != null){
//            board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece.taken = true;
//        }
//        movedPiece.x = thisTurn.endLocationX;
//        movedPiece.y = thisTurn.endLocationY;
//        
//        board.tiles[thisTurn.endLocationX][thisTurn.endLocationY].piece = movedPiece;
//        pieces.set(thisTurn.pieceMoved, movedPiece);
//        
//        displayBoard();
//    }
    public void displayBoard(){
        
    }
}