package org.richardqiao.game.mancala;

import java.util.Date;

public class Constant {
  public static final int CUP_AMOUNT = 7; // 0-5:pockets; 6: mancala
  public static final int EGG_AMOUNT = 4;
  public static final int EGG_TOTAL = EGG_AMOUNT * (CUP_AMOUNT - 1);
  public static Date date = new Date();
  public static final long SEARCH_TIME = 0l;
  public static final int SEARCH_DEPTH = 9;
}
