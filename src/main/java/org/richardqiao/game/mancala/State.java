package org.richardqiao.game.mancala;

import java.util.HashMap;
import java.util.Map;

public class State {
  public int[] s1, s2;
  public int turn; // player1 or player2's turn
  public State(MancalaPlayer p1, MancalaPlayer p2, int turn){
    this.turn = turn;
    s1 = new int[p1.pockets.length];
    s2 = new int[p2.pockets.length];
    for(int i = 0; i < s1.length; i++){
      s1[i] = p1.pockets[i];
      s2[i] = p2.pockets[i];
    }
  }
  
  public State(Board board, int turn){
    this.turn = turn;
    s1 = new int[board.p1.pockets.length];
    s2 = new int[board.p2.pockets.length];
    for(int i = 0; i < s1.length; i++){
      s1[i] = board.p1.pockets[i];
      s2[i] = board.p2.pockets[i];
    }
  }
  
  public int whoWin(Map<State, Integer> map){
    if(map.containsKey(this)){
      return map.get(this);
    }
    boolean winOnce = false;
    boolean drawOnce = false;
    for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
      MancalaPlayer pl1 = new MancalaPlayer(this, 1);
      MancalaPlayer pl2 = new MancalaPlayer(this, 2);
      pl1.setOpp(pl2);
      pl2.setOpp(pl1);
      MancalaPlayer p = this.turn == 1 ? pl1 : pl2;
      MancalaPlayer opp = p.getOpp();
      
      int res = 0;
      if(p.isEmpty() || opp.isEmpty()){
        if(p.getTotal() > Constant.EGG_TOTAL){
          res = this.turn;
        }else if(p.getTotal() == Constant.EGG_TOTAL){
          res = 0;
        }else{
          res = opp.turn;
        }
        map.put(this, res);
        return res;
      }
      if(p.pockets[i] == 0) continue;
      int endAt = p.scoopPocket(i);
      if(p.isEmpty() || opp.isEmpty()){
        if(p.getTotal() > Constant.EGG_TOTAL){
          res = this.turn;
        }else if(p.getTotal() == Constant.EGG_TOTAL){
          res = 0;
        }else{
          res = opp.turn;
        }
      }else if(endAt == Constant.CUP_AMOUNT - 1){
        res = new State(pl1, pl2, this.turn).whoWin(map);
      }else if(endAt >= 0 && p.pockets[endAt] == 1){
        p.gainEggs(endAt);
        if(p.isEmpty() || opp.isEmpty()){
          if(p.getTotal() > Constant.EGG_TOTAL){
            res = this.turn;
          }else if(p.getTotal() == Constant.EGG_TOTAL){
            res = 0;
          }else{
            res = opp.turn;
          }
        }else{
          res = new State(pl1, pl2, this.turn == 1 ? 2 : 1).whoWin(map);
        }
      }else{
        res = new State(pl1, pl2, this.turn == 1 ? 2 : 1).whoWin(map);
      }
      if(res == this.turn){
        winOnce = true;
        break;
      }else if(res == 0){
        drawOnce = true;
      }
    }
    if(winOnce){
      map.put(this, this.turn);
    }else if(drawOnce){
      map.put(this, 0);
    }else{
      map.put(this, this.turn == 1 ? 2 : 1);
    }
    return map.get(this);
  }
  
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Player "+ turn +"'s turn:\n");
    for(int i = s1.length - 1; i >= 0; i--){
      sb.append(s1[i] + ", ");
    }
    sb.append('\n');
    sb.append("   ");
    for(int n: s2){
      sb.append(n + ", ");
    }
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object obj){
    State state = (State)obj;
    if(this.turn != state.turn) return false;
    for(int i = 0; i < s1.length; i++){
      if(s1[i] != state.s1[i]) return false;
      if(s2[i] != state.s2[i]) return false;
    }
    return true;
  }
  
  @Override
  public int hashCode(){
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < s1.length; i++){
      sb.append(s1[i] + '-' + s2[i] + '-');
    }
    sb.append(turn);
    return sb.toString().hashCode();
  }
  
  public static void main(String[] args){
    MancalaPlayer p1 = new MancalaPlayer(1);
    MancalaPlayer p2 = new MancalaPlayer(p1, 2);
    Board board = new Board(p1, p2);
    p2.board = p1.board = board;
    //board.p1.pockets = new int[]{0,0,2,10};
    //board.p2.pockets = new int[]{3,0,1,8};
    State state = new State(p1, p2, 1);
    int res = state.whoWin(board.map);
    System.out.print("Result: ");
    System.out.print(res == 0 ? "Draw" : res == 1 ? "p1" : "p2");
  }
}
