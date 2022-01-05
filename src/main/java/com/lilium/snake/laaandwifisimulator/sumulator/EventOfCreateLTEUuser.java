/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;

/**
 * WiFi + LTE-Uユーザの発生イベント
 *
 * @author ginnan
 */
public class EventOfCreateLTEUuser extends Event {

    public EventOfCreateLTEUuser(double time, UserNode user, Scenario scenario) {
        super(scenario);
        this.event_time = time;
        this.event_user = user;

    }

    @Override
    public void runEvent() throws IOException {
        start();
    }

    private void start() throws IOException {

        /*ユーザ発生時の接続先選択*/
        APBSselection apbs_selection = new APBSselection(_scenario);
        int connect_ap = apbs_selection.SelectAPorBS(event_user);


        /*ユーザの接続処理*/
        ConnectSystem(connect_ap);

        /* 最小スループットの時間平均の処理*/
        _scenario.gettimeMinData(event_time);

        /*次のユーザ発生イベントを作成*/
        int current_area = event_user.getArea();
        double time = _queue.getCurrentTime() + Utility.expRand(_param.lteu_user_lambda, _scenario.getRnd());
        UserNode user = new UserNode(current_area, _queue.getNextNodeID(), time, 1, _area, _queue.getNextLTEUNodeID());
        Event next_event = new EventOfCreateLTEUuser(time, user, _scenario);
        _queue.add(next_event);

    }

    //到着時の接続処理
    private void ConnectSystem(int connect_ap) {

        AccessPoint ap;
        if (connect_ap < 10000) {
            ap = _area.getWiFiAP(connect_ap);
        } else {//apのidをLTE BSは10000以上にしている
            ap = _area.getLTEUBS(connect_ap - 10000);
        }

        //WiFi AP or LTE-U BSへの接続の確立
        ap.connect(event_user, _queue.getCurrentTime(), _param.service_set);

        //ユーザ側の設定        
        if (_scenario.getUserParameter().service_set == 0) {
            double file_size = Constants.DOWNLOAD_FILE_SIZE;
            event_user.connect(file_size, ap);
        } else {
            event_user.connect2(ap, _scenario);
        }
        Event end_event = new EventOfUserEnd(event_user, _scenario);
        event_user.setEndEvent(end_event);

        //ユーザの通信終了のイベントをイベントキューに追加
        _queue.add(end_event);

    }

}
