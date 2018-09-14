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
public class Board {
    public Tile[][] tiles = new Tile[8][8];
    
    public Board() {
        super();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                this.tiles[i][j] = new Tile(i, j);
            }
        }
    }

    public Tile getSpot(int x, int y) {
        return tiles[x][y];
    }
    
    public void add(Piece pieceToAdd){
        int x = pieceToAdd.x;
        int y = pieceToAdd.y;
        tiles[x][y].piece = pieceToAdd;
    }
    
    public boolean checkValid(int startx, int starty, int endx, int endy, String piece, int team){
        if (tiles[endx][endy].piece != null){
            if (team == tiles[endx][endy].piece.team){
                return false;
            }
        }
        
        if (piece.equals("Pawn")){
            if (starty == endy){
                if ((team == 0) && (startx - endx == -1))
                    return true;
                if ((team == 1) && (startx - endx == 1))
                    return true;
                if ((team == 0) && starty == 1 &&(startx - endx == -2) && (tiles[2][startx].piece == null))
                    return true;
                if ((team == 1) && starty == 6 &&(startx - endx == 2) && (tiles[5][startx].piece == null))
                    return true;
            } else{
                if (team == 0 && ((startx - endx == 1) || (startx- endx == -1)) && (starty - endy == -1)){
                    if (team != tiles[endx][endy].piece.team)
                        return true;
                }
                if (team == 1 && ((startx - endx == 1) || (startx- endx == -1)) && (starty - endy == 1)){
                    if (team != tiles[endx][endy].piece.team)
                        return true;
                }
            }
            return false;
        }
        
        if (piece.equals("Rook")){
            if (startx != endx && starty != endy){
                return false;
            }
            if (startx == endx){
                if (starty < endy){
                    if (endy - starty == 1){
                        return true;
                    } else{
                        for (int i = starty + 1; i < endy; i++ ){
                            if (tiles[startx][i].piece != null){
                                return false;
                            }
                        }
                    }
                } else{
                    if (starty - endy == 1){
                        return true;
                    } else{
                        for (int i = starty - 1; i > endy; i-- ){
                            if (tiles[startx][i].piece != null){
                                return false;
                            }
                        }
                    }
                }
            }
            if (starty == endy){
                if (startx < endx){
                    if (endx - startx == 1){
                        return true;
                    } else{
                        for (int i = startx + 1; i < endx; i++ ){
                            if (tiles[i][starty].piece != null){
                                return false;
                            }
                        }
                    }
                } else{
                    if (startx - endx == 1){
                        return true;
                    } else{
                        for (int i = startx - 1; i > endx; i-- ){
                            if (tiles[i][starty].piece != null){
                                return false;
                            }
                        }
                    }
                }
            }
            //check path is clear
            return true;
        }
        if (piece.equals("Knight")){
            if (((startx - endx == 2 || startx - endx == -2) && (starty - endy == 1 || starty - endy == -1)) || ((startx - endx == 1 || startx - endx == -1) && (starty - endy == 2 || starty - endy == -2))){
                return true;
            }
        }
        if (piece.equals("Bishop")){
            if ((startx - endx == starty - endy) || (endx - startx == starty - endy) || (startx - endx == endy - starty)){
                // need to check path is clear
                if (startx - endx == starty - endy){
                    if (startx > endx){
                        if (startx - endx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < startx - endx; i++){
                                if (tiles[startx - i][starty - i].piece != null)
                                    return false;
                            }
                        }
                    } else{
                        if (endx - startx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < endx - startx; i++){
                                if (tiles[startx + i][starty + i].piece != null)
                                    return false;
                            }
                        }
                    }
                }
                if ((endx - startx == starty - endy) || (startx - endx == endy - starty)){
                    if (startx > endx){
                        if (startx - endx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < startx - endx; i++){
                                if (tiles[startx - i][starty + i].piece != null){
                                    return false;
                                }
                            }
                        }
                    }else{
                        if (endx - startx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < endx - startx; i++){
                                if (tiles[startx + i][starty - i].piece != null){
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }
        if (piece.equals("Queen")){
            if ((startx - endx == starty - endy) || (endx - startx == starty - endy) || (startx - endx == endy - starty)){
                // need to check path is clear
                if (startx - endx == starty - endy){
                    if (startx > endx){
                        if (startx - endx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < startx - endx; i++){
                                if (tiles[startx - i][starty - i].piece != null)
                                    return false;
                            }
                        }
                    } else{
                        if (endx - startx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < endx - startx; i++){
                                if (tiles[startx + i][starty + i].piece != null)
                                    return false;
                            }
                        }
                    }
                }
                if ((endx - startx == starty - endy) || (startx - endx == endy - starty)){
                    if (startx > endx){
                        if (startx - endx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < startx - endx; i++){
                                if (tiles[startx - i][starty + i].piece != null){
                                    return false;
                                }
                            }
                        }
                    }else{
                        if (endx - startx == 1){
                            return true;
                        } else{
                            for (int i = 1; i < endx - startx; i++){
                                if (tiles[startx + i][starty - i].piece != null){
                                    return false;
                                }
                            }
                        }
                    }
                }
            } else{
                if (startx != endx && starty != endy){
                    return false;
                }
                if (startx == endx){
                    if (starty < endy){
                        if (endy - starty == 1){
                            return true;
                        } else{
                            for (int i = starty + 1; i < endy; i++ ){
                                if (tiles[startx][i].piece != null){
                                    return false;
                                }
                            }
                        }
                    } else{
                        if (starty - endy == 1){
                            return true;
                        } else{
                            for (int i = starty - 1; i > endy; i-- ){
                                if (tiles[startx][i].piece != null){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (starty == endy){
                    if (startx < endx){
                        if (endx - startx == 1){
                            return true;
                        } else{
                            for (int i = startx + 1; i < endx; i++ ){
                                if (tiles[i][starty].piece != null){
                                    return false;
                                }
                            }
                        }
                    } else{
                        if (startx - endx == 1){
                            return true;
                        } else{
                            for (int i = startx - 1; i > endx; i-- ){
                                if (tiles[i][starty].piece != null){
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }
        if (piece.equals("King")){
            if (startx != endx || starty != endy){
                if (startx - endx < 2 && startx - endx > -2){
                    if (starty - endy < 2 && starty - endy > -2){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
