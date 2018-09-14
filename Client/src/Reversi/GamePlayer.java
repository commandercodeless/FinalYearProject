/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Chess.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author decla_000
 */
public class GamePlayer{
    public String team;
    public Client.Player player;

    private List<Piece> pieces = new ArrayList<>();

    public GamePlayer() {
        super();
    }

    public List<Piece> getPieces() {
        return pieces;
    }

}
