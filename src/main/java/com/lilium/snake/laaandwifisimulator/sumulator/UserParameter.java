/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * ユーザのパラメータ,ユーザ入力引数
 *
 * @author ginnan
 */
public class UserParameter {

    /*引数*/
    public final int loop_num;  //0:合計のループ(シミュレーション)回数
    public final double interval_time;  //1:周波数割り当て間隔(GAの実行間隔
    public final double wifi_user_lambda;   //2:WiFiのみユーザの到着率
    public final double lteu_user_lambda;   //3:WiFi + LTE-Uユーザの到着率
    public final int end_condition; //4:シミュレーション終了条件の選択(0:呼数, 1:時間)
    public final int end_num;   //5:終了条件となる呼数 or 時間
    public final int service_set; //6:ユーザの利用形態 (0:ファイルダウンロード, 1:一定時間の通信)
    public final int select_method;   //7:提案手法などの選択
    public final int ga_loop_num;   //8:GAのループ回数
    public final int mutation_prob; //9:突然変異の確率 
    public final int ga_individual_num; //10:GAの個体数
    public final int crossover_parent_num; //11:交叉時の親の組数
    public final int elite_num; //12:エリート選択で選択する数

    public UserParameter(String[] s) {
        /*引数の取得*/
        loop_num = Integer.parseInt(s[0]);
        interval_time = Double.parseDouble(s[1]);
        wifi_user_lambda = Double.parseDouble(s[2]);
        lteu_user_lambda = Double.parseDouble(s[3]);
        end_condition = Integer.parseInt(s[4]);
        end_num = Integer.parseInt(s[5]);
        service_set = Integer.parseInt(s[6]);
        select_method = Integer.parseInt(s[7]);
        ga_loop_num = Integer.parseInt(s[8]);
        mutation_prob = Integer.parseInt(s[9]);
        ga_individual_num = Integer.parseInt(s[10]);
        crossover_parent_num = Integer.parseInt(s[11]);
        elite_num = Integer.parseInt(s[12]);

    }
}
