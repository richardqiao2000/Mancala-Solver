package org.richardqiao.game.mancala;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

  public MancalaPlayer p1;
  public MancalaPlayer p2;
  public BufferedWriter bw = null;

  public Board(){
    p1 = new MancalaPlayer();
    p2 = new MancalaPlayer(p1);
  }

  public int win(State state, Map<State, Integer> map) throws IOException{
    try{
      printLine(state.toString(), 0);
      if(map.containsKey(state)){
        printLine("Existing one, return.", 0);
        return map.get(state);
      }
      boolean winOnce = false;
      boolean drawOnce = false;
      for(int i = 0; i < Constant.CUP_AMOUNT - 1; i++){
        MancalaPlayer pl1 = new MancalaPlayer(state, 1);
        MancalaPlayer pl2 = new MancalaPlayer(state, 2);
        pl1.setOpp(pl2);
        pl2.setOpp(pl1);
        MancalaPlayer p = state.turn == 1 ? pl1 : pl2;
        if(p.pockets[i] > 0){
          int endAt = p.scoopPocket(i);
          int result = 0;
          if(p.isEmpty()){
            printLine(new State(pl1, pl2, state.turn).toString(), 0);
            printLine("---------", 0);
            if(p.getTotal() > Constant.EGG_TOTAL){
              result = state.turn;
            }else if(p.getTotal() == Constant.EGG_TOTAL){
              result = 0;
            }else{
              result = state.turn == 1 ? 2 : 1;
            }
          }else if(endAt == Constant.CUP_AMOUNT - 1){ //last egg in the cup. Do it again
            result = win(new State(pl1, pl2, state.turn), map);
          }else if(endAt >= 0 && endAt != Constant.CUP_AMOUNT - 1 && p.pockets[endAt] == 1){  //Catch Opponents' Eggs
            MancalaPlayer opp = p.getOpp();
            p.gainEggs(endAt);
            if(p.isEmpty() || opp.isEmpty()){
              printLine(new State(pl1, pl2, state.turn).toString(), 0);
              printLine("---------", 0);
              if(p.getTotal() > Constant.EGG_TOTAL){
                result = state.turn;
              }else if(p.getTotal() == Constant.EGG_TOTAL){
                result = 0;
              }else{
                result = state.turn == 1 ? 2 : 1;
              }
            }else{
              int oppTurn = state.turn == 1 ? 2 : 1;
              result = win(new State(pl1, pl2, oppTurn), map);
            }
          }else{
            int oppTurn = state.turn == 1 ? 2 : 1;
            result = win(new State(pl1, pl2, oppTurn), map);
          }
          if(result == state.turn){
            winOnce = true;
            break;
          }else if(result == 0){
            drawOnce = true;
          }
        }
      }
      if(winOnce){
        map.put(state, state.turn);
      }else if(drawOnce){
        map.put(state, 0);
      }else{
        map.put(state, state.turn == 1 ? 2 : 1);
      }
    }catch(IOException ioe){
      ioe.printStackTrace();
    }
    
    return map.get(state);
  }
  
  public void printLine(String str, int redirection) throws IOException{
    if(redirection == 0){
      bw.write(str);
      bw.newLine();
    }else{
      System.out.println(str);
    }
  }
  
  public static void main(String[] args) throws IOException {
    Board board = new Board();
    board.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/main/resources/output.txt")));
    int res = board.win(new State(board.p1, board.p2, 1), new HashMap<State, Integer>());
    board.bw.write("Result: ");
    board.bw.write(res == 0 ? "Draw" : res == 1 ? "p1" : "p2");
    board.bw.newLine();
    if(board.bw != null) board.bw.close();
    System.out.print("Result: ");
    System.out.print(res == 0 ? "Draw" : res == 1 ? "p1" : "p2");
    
    //for(State state: steps){
    //  System.out.println(state);
    //}
  }

}
