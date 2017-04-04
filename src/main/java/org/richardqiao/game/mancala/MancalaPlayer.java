package org.richardqiao.game.mancala;

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
    return scoopPocket(bestMove());
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

  private int bestMove(){
    int cur = 0;
    int draw = -1;
    State state = new State(board.p1, board.p2, this.turn);
    int result = 0;
    for(int i = 0; i < pockets.length - 1; i++){
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
      result = next.whoWin(board.map);
      if(result == 0){
        draw = i;
      }else if(result == this.turn){
        return i;
      }
    }
    return draw != -1 ? draw : cur;
  }
  
}
