/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

/**
 *
 * @author decla_000
 */
public class Tile {
    int x;
    int y;
    Piece piece;
    
    public Tile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        piece = null;
    }

    public void occupySpot(Piece piece){
        //if piece already here, delete it, i. e. set it dead
        if(this.piece != null)
            this.piece.setTaken(false);
        //place piece here
        this.piece = piece;
    }

    public boolean isOccupied() {
        if(piece != null)
            return true;
        return false;
    }

    public Piece releaseTile() {
        Piece releasedPiece = this.piece;
        this.piece = null;
        return releasedPiece;
    }
}
