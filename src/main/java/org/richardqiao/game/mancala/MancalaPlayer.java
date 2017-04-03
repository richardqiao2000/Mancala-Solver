package org.richardqiao.game.mancala;

public class MancalaPlayer {

  public int[] pockets;
  private MancalaPlayer opp;
  public MancalaPlayer(){
    pockets = new int[Constant.CUP_AMOUNT];
    for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
      pockets[i] = Constant.EGG_AMOUNT;
    }
  }
  
  public MancalaPlayer(MancalaPlayer opp){
    this();
    this.opp = opp;
    this.opp.opp = this;
  }
  
  public MancalaPlayer(State state, int player){
    int[] s = player == 1 ? state.p1.pockets : state.p2.pockets;
    pockets = new int[s.length];
    for(int i = 0; i < s.length; i++){
      pockets[i] = s[i];
    }
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


}
