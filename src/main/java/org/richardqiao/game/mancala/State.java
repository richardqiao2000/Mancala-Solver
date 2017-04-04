package org.richardqiao.game.mancala;

import java.util.Date;
import java.util.Map;
import java.util.Set;

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
  
  public State(int[] s1, int[] s2, int turn){
    this.s1 = new int[s1.length];
    this.s2 = new int[s2.length];
    for(int i = 0; i < s1.length; i++){
      this.s1[i] = s1[i];
      this.s2[i] = s2[i];
    }    
    this.turn = turn;
  }
  
  public State getReversed(){
    return new State(s2, s1, turn == 1 ? 2 : 1);
  }
  
  public int whoWin(Map<State, Integer> map, Set<State> set){
    //System.out.println(this);
    /*
    if(Constant.SEARCH_TIME != 0l){
      long diff = new Date().getTime() - Constant.date.getTime();
      if(diff > Constant.SEARCH_TIME) return 0;
    }
    */
    if(map.containsKey(this)){
      return map.get(this);
    }
    if(set.contains(this)){
      map.put(this, 0);
      return 0;
    }
    set.add(this);
    
    int oppTurn = this.turn == 1 ? 2 : 1;
    MancalaPlayer pl1 = new MancalaPlayer(this, 1);
    MancalaPlayer pl2 = new MancalaPlayer(this, 2);
    pl1.setOpp(pl2);
    pl2.setOpp(pl1);
    MancalaPlayer p = this.turn == 1 ? pl1 : pl2;
    MancalaPlayer opp = p.getOpp();
    
    int res = 0;
    res = winJudge(p, opp, this.turn);
    if(res != -1){
      map.put(this, res);
      map.put(this.getReversed(), oppTurn);
      return res;
    }

    boolean winOnce = false;
    boolean drawOnce = false;
    for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
      if(p.pockets[i] == 0) continue;
      pl1 = new MancalaPlayer(this, 1);
      pl2 = new MancalaPlayer(this, 2);
      pl1.setOpp(pl2);
      pl2.setOpp(pl1);
      p = this.turn == 1 ? pl1 : pl2;
      opp = p.getOpp();
      res = 0;
      
      int endAt = p.scoopPocket(i);
      res = winJudge(p, opp, this.turn);
      if(res == -1){
        if(endAt == Constant.CUP_AMOUNT - 1){
          res = new State(pl1, pl2, this.turn).whoWin(map, set);
        }else if(endAt >= 0 && p.pockets[endAt] == 1){
          p.gainEggs(endAt);
          res = winJudge(p, opp, this.turn);
          if(res == -1){
            res = new State(pl1, pl2, oppTurn).whoWin(map, set);
          }
        }else{
          res = new State(pl1, pl2, oppTurn).whoWin(map, set);
        }
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
      map.put(this.getReversed(), oppTurn);
    }else if(drawOnce){
      map.put(this, 0);
    }else{
      map.put(this, oppTurn);
      map.put(this.getReversed(), this.turn);
    }
    return map.get(this);
  }
  
  private int winJudge(MancalaPlayer p, MancalaPlayer opp, int turn){ // 0: draw, 1: p1 win, 2: p2 win, -1: unknow 
    int pWin = p.winOrLose();//0: draw; 1: win; 2: lose; -1: unknown
    int oppWin = opp.winOrLose();
    if(pWin == 1 || oppWin == 2) return turn;
    if(oppWin == 1 || pWin == 2) return turn == 1 ? 2 : 1;
    if(pWin == 0 || oppWin == 0) return 0;
    return -1;
  }
  
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Player "+ turn +"'s turn:\n");
    for(int i = s1.length - 1; i >= 0; i--){
      sb.append(" " + s1[i] + " " + "\t");
    }
    sb.append("\n\t");
    for(int n: s2){
      sb.append(" " + n + " " + "\t");
    }
    sb.append("\n\t");
    for(int i = 0; i < s2.length - 1; i++){
      sb.append("[" + (i + 1) + "]" + "\t");
    }
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object obj){
    State state = (State)obj;
    int tmp = this.turn;
    int[] tmpS1 = s1;
    int[] tmpS2 = s2;
//    if(tmp != state.turn){
//      tmp = state.turn;
//      int[] tmpS = tmpS1;
//      tmpS1 = tmpS2;
//      tmpS2 = tmpS;
//    }
    if(tmp != state.turn) return false;
    for(int i = 0; i < tmpS1.length; i++){
      if(tmpS1[i] != state.s1[i]) return false;
      if(tmpS2[i] != state.s2[i]) return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode(){
    StringBuilder sb = new StringBuilder();
    int tmp = turn;
    int[] tmpS1 = s1;
    int[] tmpS2 = s2;
//    if(tmp == 2){
//      tmp = 1;
//      int[] tmpS = tmpS1;
//      tmpS1 = tmpS2;
//      tmpS2 = tmpS;
//    }
    for(int i = 0; i < tmpS1.length; i++){
      sb.append(tmpS1[i] + '-' + tmpS2[i] + '-');
    }
    sb.append(tmp);
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
    Constant.date = new Date();
    int res = state.whoWin(board.map, board.set);
    System.out.print("Result: ");
    System.out.print(res == 0 ? "Draw" : res == 1 ? "p1" : "p2");
  }
}
