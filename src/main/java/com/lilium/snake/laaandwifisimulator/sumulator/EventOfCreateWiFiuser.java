/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;

/**
 * WiFiユーザの発生イベント
 *
 * @author ginnan
 */
public class EventOfCreateWiFiuser extends Event {

    public EventOfCreateWiFiuser(double time, UserNode user, Scenario scenario) {
        super(scenario);
        this.event_time = time;
        this.event_user = user;

    }

    @Override
    public void runEvent() throws IOException {
        start();
    }

    private void start() throws IOException {
        /*ユーザの到着時の接続先選択*/
        APBSselection apbs_selection = new APBSselection(_scenario);
        int connect_ap = apbs_selection.SelectAP(event_user);


        /*ユーザの接続処理*/
        ConnectSystem(connect_ap);

        /* 最小スループットの時間平均の処理*/
        _scenario.gettimeMinData(event_time);

        /*次のユーザ発生イベントを作成*/
        int current_area = event_user.getArea();
        double time = _queue.getCurrentTime() + Utility.expRand(_param.wifi_user_lambda, _scenario.getRnd());
        UserNode user = new UserNode(current_area, _queue.getNextNodeID(), time, 0, _area, _queue.getNextWiFiNodeID());
        Event next_event = new EventOfCreateWiFiuser(time, user, _scenario);
        _queue.add(next_event);

    }

    //到着時のユーザの接続処理
    private void ConnectSystem(int connect_ap) {

        AccessPoint ap;
        ap = _area.getWiFiAP(connect_ap);
        //WiFi APへの接続の確立        
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
