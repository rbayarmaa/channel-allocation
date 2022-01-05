/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * 計算用のクラス
 *
 * @author ginnan
 */
public class Utility {

    public static double CalcUserthroughput(double capacity, int connecting_num) {
        double throughput;
        if (connecting_num == 0) {
            throughput = capacity;
        } else {
            throughput = (double) capacity / connecting_num;
        }

        return throughput;
    }

    /*AP,BSに接続しているユーザのスループットの再計算*/
    public static void ReCalcUserthroughput(LinkedList<UserNode> users, double assign_throughput, double current_time) {
        UserNode user = null;
        ListIterator<UserNode> iterator = users.listIterator();
        while (iterator.hasNext()) {
            user = iterator.next();
            user.reCalcThroughput(assign_throughput, current_time);
        }
    }

    /* 一定時間通信する場合のユーザスループットの再計算 */
    public static void ReCalcUserthroughput2(LinkedList<UserNode> users, double assign_throughput, double current_time) {
        UserNode user = null;
        ListIterator<UserNode> iterator = users.listIterator();
        while (iterator.hasNext()) {
            user = iterator.next();
            user.reCalcThroughput2(assign_throughput, current_time);
        }
    }

    //ユーザの接続先APの変更処理
    public static void ChangeConnectingAPs(LinkedList<UserNode> users, AccessPoint ap) {
        UserNode user = null;
        ListIterator<UserNode> iterator = users.listIterator();
        while (iterator.hasNext()) {
            user = iterator.next();
            user.ChangeConnectedAP(ap);
        }
    }

    //ユーザの接続先APの変更処理
    public static void ChangeConnectingAPs(AccessPoint ap) {
        UserNode user = null;
        ListIterator<UserNode> iterator = ap.UserList.listIterator();
        while (iterator.hasNext()) {
            user = iterator.next();
            user.ChangeConnectedAP(ap);
        }
    }

    /**
     * 平均 lambda の到着率に従うポアソン分布の到着間隔を出力する．<br>
     * 到着間隔は指数分布に従う．
     *
     * @param lambda           平均到着率
     * @param mersenne_twister ランダム値を生成
     * @return 到着間隔
     */
    public static double expRand(double lambda, MersenneTwister mersenne_twister) {
        return -Math.log(1 - mersenne_twister.genrand_real2()) / lambda;
    }

    /*WiFi AP, LTE-U BSの容量を取得(チャネル共有モデルの適用) */
    public static double[][] SetCapacitySharedWiFiLTEU() {
        double[][] capacity_with_wifi_lteu = new double[2][40];    //[2]->LTEU(1)とWiFi(0), [40]->1個のLTE-Uと0～40のWiFiとチャネルを共有する場合
        int a, b, c;
        c = 0;
        try {
            File csv;
            csv = new File("capacity_shared_with_wifi_lteu.csv");
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
//                while(st.hasMoreTokens()){
                a = Integer.valueOf(st.nextToken().trim()).intValue();
                b = Integer.valueOf(st.nextToken().trim()).intValue();
                capacity_with_wifi_lteu[0][c] = a;
                capacity_with_wifi_lteu[1][c] = b;
                c++;
//                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }
        Constants.CAPACITY_WITH_LAA_WIFI = capacity_with_wifi_lteu;
        return capacity_with_wifi_lteu;
    }

    /*WiFi APの容量を取得(チャネル共有モデルの適用) */
    public static double[] SetCapacitySharedWiFi() {
        double[] capacity_with_wifi = new double[40]; //[40]WiFiが1～40の場合 [40]
        int a, b;
        b = 0;

        try {
            File csv;
            csv = new File("capacity_shared_with_wifis.csv");
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
//                while(st.hasMoreTokens()){
                a = Integer.valueOf(st.nextToken()).intValue();
                capacity_with_wifi[b] = a;
                b++;
//                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }
        Constants.CAPACITY_WITH_WIFIS = capacity_with_wifi;
        return capacity_with_wifi;
    }
}
