package org.richardqiao.game.mancala;

import java.util.Date;

public class MancalaPlayer {

  public int[] pockets;
  private MancalaPlayer opp;
  public Board board;
  public int turn;
  
  public MancalaPlayer(int turn){
    pockets = new int[Constant.CUP_AMOUNT];
    for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
      pockets[i] = Constant.EGG_AMOUNT;
    }
    this.turn = turn;
  }
  
  public MancalaPlayer(MancalaPlayer opp, int turn){
    this(turn);
    this.opp = opp;
    this.opp.opp = this;
  }
  
  public MancalaPlayer(State state, int turn){
    int[] s = turn == 1 ? state.s1 : state.s2;
    this.turn = turn;
    pockets = new int[s.length];
    for(int i = 0; i < s.length; i++){
      pockets[i] = s[i];
    }
  }
  
  public int scoopPocketWithAI(){
    //Calculate best move;
    //scoopPocket(bestMove)
    return scoopPocket(bestMove(Constant.SEARCH_DEPTH));
  }
  
  public int scoopPocket(int seq){
    if(seq < 0 || seq >= Constant.CUP_AMOUNT - 1){
      return seq;
    }
    if(pockets[seq] == 0) return seq;
    int eggs = pockets[seq];
    pockets[seq++] = 0;
    while(eggs > 0){
      while(eggs > 0 && seq < Constant.CUP_AMOUNT){
        eggs--;
        pockets[seq++]++;
        if(eggs == 0) return seq - 1;
      }
      seq = 0;
      while(eggs> 0 && seq < Constant.CUP_AMOUNT - 1){
        eggs--;
        opp.pockets[seq++]++;
      }
      seq = 0;
    }
    return -1;
  }
  
  public void unScoopPocket(int seq, int eggs){
    pockets[seq++] = eggs;
    while(eggs > 0){
      while(eggs > 0 && seq < Constant.CUP_AMOUNT){
        eggs--;
        pockets[seq++]--;
      }
      seq = 0;
      while(eggs> 0 && seq < Constant.CUP_AMOUNT - 1){
        eggs--;
        opp.pockets[seq++]--;
      }
      seq = 0;
    }
  }

  public int gainEggs(int seq){
    int resOppo = Constant.CUP_AMOUNT - 2 - seq;
    int opEggs = opp.pockets[resOppo];
    if(opEggs == 0) return 0;
    opp.pockets[resOppo] = 0;
    pockets[seq] = 0;
    pockets[Constant.CUP_AMOUNT - 1] += opEggs + 1;
    return opEggs;
  }
  
  public void unGainEggs(int seq, int eggs){
    if(eggs == 0) return;
    int resOppo = Constant.CUP_AMOUNT - 2 - seq;
    opp.pockets[resOppo] = eggs;
    pockets[seq] = 1;
    pockets[Constant.CUP_AMOUNT - 1] -= eggs + 1;
  }
  
  public int winOrLose(){ //0: draw; 1: win; 2: lose; -1: unknown
    int total = getTotal();
    if(isEmpty()){
      if(total > Constant.EGG_TOTAL){
        return 1;
      }else if(total == Constant.EGG_TOTAL){
        return 0;
      }
      return 2;
    }
    if(pockets[Constant.CUP_AMOUNT - 1] > Constant.EGG_TOTAL) return 1;
    return -1;
  }
  
  public boolean isEmpty(){
    for(int i = 0; i < pockets.length - 1; i++){
      if(pockets[i] > 0) return false;
    }
    return true;
  }
  
  public MancalaPlayer getOpp() {
    return opp;
  }

  public void setOpp(MancalaPlayer opp) {
    this.opp = opp;
  }

  public int getTotal() {
    int total = 0;
    for(int n: pockets) total += n;
    return total;
  }
  
  private int bestMove(int depth){
    int bestIndex = -1, maxMancala = 0, minOppMancala = Constant.EGG_TOTAL + 1;
    int indexCanMove = -1;
    for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
      if(pockets[i] > 0){
        indexCanMove = i;
        int[] scr = getScore(i, depth);
        int pValue = scr[0], oppValue = scr[1];
        if(this.turn == 2){
          int tmp = pValue;
          pValue = oppValue;
          oppValue = tmp;
        }
        if(pValue == -1 || oppValue > Constant.EGG_TOTAL) continue;
        if(pValue > Constant.EGG_TOTAL){
          bestIndex = i;
          break;
        }
        if(maxMancala < pValue){
          maxMancala = pValue;
          bestIndex = i;
        }else if(maxMancala == pValue && oppValue < minOppMancala){
          minOppMancala = oppValue;
          bestIndex = i;
        }
      }
    }
    return bestIndex == -1 ? indexCanMove : bestIndex;
  }
  
  private int[] getScore(int seq, int depth){//result[0]: p1.score; result[1]: p2.score
    if(this.pockets[seq] == 0) return new int[]{-1, -1};
    int mcl1 = this.pockets[this.pockets.length - 1];
    int mcl2 = this.opp.pockets[this.pockets.length - 1];
    if(depth == 0){
      return new int[]{mcl1, mcl2};
    }
    if(mcl1 > Constant.EGG_TOTAL || mcl2 > Constant.EGG_TOTAL){
      return new int[]{mcl1, mcl2};
    }
    int cur = 0;
    int draw = -1;
    State state = new State(board.p1, board.p2, this.turn);
    MancalaPlayer pl1 = new MancalaPlayer(state, 1);
    MancalaPlayer pl2 = new MancalaPlayer(state, 2);
    pl1.setOpp(pl2);
    pl2.setOpp(pl1);
    Board board = new Board(pl1, pl2);
    pl2.board = pl1.board = board;
    MancalaPlayer p = this.turn == 1 ? pl1 : pl2;
    MancalaPlayer opp = p.getOpp();

    int endAt = p.scoopPocket(seq);
    State next = null;
    if(endAt == Constant.CUP_AMOUNT - 1){
      next = new State(pl1, pl2, this.turn);
    }else if(endAt >= 0 && p.pockets[endAt] == 1){
      p.gainEggs(endAt);
      next = new State(pl1, pl2, opp.turn);
    }else{
      next = new State(pl1, pl2, opp.turn);
    }
    
    int[][] scores = new int[Constant.CUP_AMOUNT - 1][2];
    for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
      pl1 = new MancalaPlayer(next, 1);
      pl2 = new MancalaPlayer(next, 2);
      pl1.setOpp(pl2);
      pl2.setOpp(pl1);
      board = new Board(pl1, pl2);
      pl2.board = pl1.board = board;
      p = next.turn == 1 ? pl1 : pl2;
      opp = p.getOpp();
      int[] score = p.getScore(i, depth - 1);
      scores[i][0] = score[0];
      scores[i][1] = score[1];
    }
    
    int maxIndex = -1, max = 0;
    for(int i = 0; i < scores.length; i++){
      int pValue = scores[i][0], oppValue = scores[i][1];
      if(this.turn == 2){
        int tmp = pValue;
        pValue = oppValue;
        oppValue = tmp;
      }
      if(pValue == -1 || oppValue > Constant.EGG_TOTAL) continue;
      if(pValue > Constant.EGG_TOTAL){
        maxIndex = i;
        break;
      }
      if(max < pValue){
        max = pValue;
        maxIndex = i;
      }
    }
    
    return maxIndex != -1 ? new int[]{scores[maxIndex][0], scores[maxIndex][1]} : new int[]{-1, -1};
    
  }
  
  private int bestMove(){
    int cur = 0;
    int draw = -1;
    State state = new State(board.p1, board.p2, this.turn);
    int result = 0;
    for(int i = pockets.length - 2; i >= 0; i--){
      if(pockets[i] == 0) continue;
      cur = i;
      MancalaPlayer pl1 = new MancalaPlayer(state, 1);
      MancalaPlayer pl2 = new MancalaPlayer(state, 2);
      pl1.setOpp(pl2);
      pl2.setOpp(pl1);
      MancalaPlayer p = this.turn == 1 ? pl1 : pl2;
      MancalaPlayer opp = p.getOpp();
      int endAt = p.scoopPocket(i);
      State next = null;
      if(endAt == Constant.CUP_AMOUNT - 1){
        next = new State(pl1, pl2, this.turn);
      }else if(endAt >= 0 && p.pockets[endAt] == 1){
        p.gainEggs(endAt);
        next = new State(pl1, pl2, opp.turn);
      }else{
        next = new State(pl1, pl2, opp.turn);
      }
      //Constant.date = new Date();
      result = next.whoWin(board.map, board.set);
      //long diff = new Date().getTime() - Constant.date.getTime();
      //if(diff > Constant.SEARCH_TIME) break;
      
      if(result == 0){
        draw = i;
      }else if(result == this.turn){
        return i;
      }
    }
    return draw != -1 ? draw : cur;
  }
  
}
