/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * 定数を扱うstaticでfinalなクラス
 *
 * @author ginnan
 */
public class Constants {

    //Information about area information
    public static int AREA_NUM = 288;  //最小エリアの数 ※288 ※72
    public static int WiFi_NUM = 100;  //WiFi APの数 ※100 ※28
    public static int LTEU_NUM = 7;   //LTE-U BSの数 ※10 ※2 ※8 ※3 ※7
    public static int LINE_NUM = 2;   //エリアの列の数(最小エリア数で割り切れる数を選択) /* 使われていない*/

    public static int AP_COVER_NUM = 54;     //WiFi APがカバーしているエリアの数 ※24  ※54
    public static int BS_COVER_NUM = 54;     //LTE-U BSがカバーしているエリアの数 ※24 ※54
    public static int AP_BS_POSITION_NUM = 136;    //WiFi ,LTE-Uを置ける位置の数 ※136 ※31 ※136
    public static int AREA_COVERED_AP_NUM = 12; //最小エリアをカバーしているAPの数(APがすべての位置に配置された場合を仮定) ※12 ※12
    public static int AREA_COVERED_BS_NUM = 12; //最小エリアをカバーしているBSの数(BSがすべての位置に配置された場合を仮定) ※12 ※12

    public static int LTEU_AREA_COVER_NUM = 2; //同一エリアをカバーしているLTE-Uの最大数

    //Information about WiFi AP or LTE-U BS
    public static double WiFi_CAPACITY = 40; //WiFi AP の容量[Mbps]
    public static double LTEU_CAPACITY = 75; //LTE-U BS の容量[Mbps]
    public static int CHANNEL_NUM = 4; //利用可能なチャネルの数 ※8
    public static double[][] CAPACITY_WITH_LAA_WIFI;   //WiFiとLTE-Uがチャネルを共有するときの容量[Mbps]
    public static double[] CAPACITY_WITH_WIFIS;        //WiFi同士がチャネルを共有するときの容量[Mbps]
    public static int WiFi_CSMA_RANGE = 54; //CSMA/CAが可能な範囲(通信可能な範囲) ※54
    public static int LTEU_LBT_RANGE = 54;  //LBTが可能な範囲(通信可能な範囲) ※54

    //User information
    public static double DOWNLOAD_FILE_SIZE = 400; //ダウンロードファイルサイズ [Mbit]
    public static double mu = 0.00333333333333333; //0.001666667;  //0.0166666666666666666; //0.00333333333333333; //0.008333333333;////平均通信時間の逆数
    public static double STEADY_NUM = 10000;    //過渡状態の呼数

    //I am not using it because it is possible to input with the parameter → argument of the genetic algorithm
    public static final int GA_NUM = 1000;                    // GA loop count
    public static final int GA_INDIVIDUAL_NUM = 10;//※10				//Initial population of GA
    public static final int PARENT_NUM = 3;//※3 //Number of parent pairs selected in one crossover
    public static final int CROSSOVER_NUM = 1;
    public static final int ELITE_SELECT_NUM = 1; //In the selection, the number of individuals to be selected by elite selection

    public static int SERVICE_SET;//Is it communication for a certain period of time or file download?

    public static String VERSION = Constants.class.getPackage().getName();

    public static String are = "0.0005"; //Used for the file name of the file output

//   public  static int num = 1;
}
