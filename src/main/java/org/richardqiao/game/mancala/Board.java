package org.richardqiao.game.mancala;

import java.util.HashMap;
import java.util.Map;

public class Board {

  public MancalaPlayer p1;
  public MancalaPlayer p2;
  public Map<State, Integer> map = new HashMap<State, Integer>();

  public Board(){
    p1 = new MancalaPlayer(1);
    p2 = new MancalaPlayer(p1, 2);
  }

  public Board(MancalaPlayer p1, MancalaPlayer p2){
    this.p1 = p1;
    this.p2 = p2;
  }
}
