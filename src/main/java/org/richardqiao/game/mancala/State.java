package org.richardqiao.game.mancala;

public class State {
  public MancalaPlayer p1, p2;
  public int turn; // player1 or player2's turn
  public State(MancalaPlayer p1, MancalaPlayer p2, int turn){
    this.turn = turn;
    this.p1 = p1;
    this.p2 = p2;
  }
  
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Player "+ turn +"'s turn:\n");
    for(int i = p1.pockets.length - 1; i >= 0; i--){
      sb.append(p1.pockets[i] + ", ");
    }
    sb.append('\n');
    sb.append("   ");
    for(int n: p2.pockets){
      sb.append(n + ", ");
    }
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object obj){
    State state = (State)obj;
    if(this.turn != state.turn) return false;
    for(int i = 0; i < p1.pockets.length; i++){
      if(p1.pockets[i] != state.p1.pockets[i]) return false;
      if(p2.pockets[i] != state.p2.pockets[i]) return false;
    }
    return true;
  }
  
  @Override
  public int hashCode(){
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < p1.pockets.length; i++){
      sb.append(p1.pockets[i] + '-' + p2.pockets[i] + '-');
    }
    sb.append(turn);
    return sb.toString().hashCode();
  }
}
