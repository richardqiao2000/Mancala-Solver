package org.richardqiao.game.mancala;


public class Game {

  public static void main(String[] args) {
    //test();
    MancalaPlayer p1 = new MancalaPlayer(1);
    MancalaPlayer p2 = new MancalaPlayer(p1, 2);
    Board board = new Board(p1, p2);
    p2.board = p1.board = board;
    
    while(!p1.isEmpty() && !p2.isEmpty()){
      System.out.println(new State(p1, p2, 1));
      int endAt = p1.scoopPocketWithAI();
      System.out.println(new State(p1, p2, 2));
      if(p1.isEmpty() || p2.isEmpty()){
        break;
      }
      if(endAt == Constant.CUP_AMOUNT - 1){
        continue;
      }else if(endAt >= 0 && p1.pockets[endAt] == 1){
        p1.gainEggs(endAt);
        System.out.println(new State(p1, p2, 2));
      }
      if(p1.isEmpty() || p2.isEmpty()){
        break;
      }
      
      String input = System.console().readLine();
      int move = Integer.valueOf(input);
      endAt = p2.scoopPocket(move);
      while(endAt == Constant.CUP_AMOUNT - 1){
        System.out.println(new State(p1, p2, 2));
        input = System.console().readLine();
        input = System.console().readLine();
        move = Integer.valueOf(input);
        endAt = p2.scoopPocket(move);
      }
      if(endAt >= 0 && p2.pockets[endAt] == 1){
        p2.gainEggs(endAt);
        System.out.println(new State(p1, p2, 1));
      }
      
      /*
      endAt = p2.scoopPocketWithAI();
      while(endAt == Constant.CUP_AMOUNT - 1){
        System.out.println(new State(board, 2));
        endAt = p2.scoopPocketWithAI();
      }
      if(endAt >= 0 && board.p1.pockets[endAt] == 1){
        System.out.println(new State(board, 2));
        board.p2.gainEggs(endAt);
      }
      System.out.println(new State(board, 1));
      */
    }
    System.out.println(new State(p1, p2, 1));
    if(p1.getTotal() == p2.getTotal()){
      System.out.println("Draw!");
    }else if(p1.getTotal() > p2.getTotal()){
      System.out.println("You lose!");
    }else{
      System.out.println("You win!");
    }
  }

}
