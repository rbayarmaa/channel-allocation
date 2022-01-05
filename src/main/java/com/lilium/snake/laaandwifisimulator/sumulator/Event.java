/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;

/**
 * イベントの抽象クラス
 *
 * @author ginnan
 */
public abstract class Event {

    /**
     * イベントが処理される時間 (sec)．
     */
    protected double event_time;

    /**
     * イベント ID．
     */
    protected int event_id;

    /**
     * イベント処理の対象となる ユーザ ．
     */
    protected UserNode event_user;

    protected Scenario _scenario;
    protected final EventQueue _queue;
    protected final UserParameter _param;
    protected Area _area;


    protected Event(Scenario scenario) {
        _scenario = scenario;
        _queue = scenario.getQueue();
        _param = scenario.getUserParameter();
        _area = scenario.getArea();
        event_id = _queue.getNextEventID();
    }

    /**
     * 各イベントごとに実装される抽象メソッド
     */
    public abstract void runEvent() throws IOException;

    /**
     * this.mn = null;
     */
    public void finish() {
        event_user = null;
    }

    public double getTime() {
        return event_time;
    }

    public UserNode getMn() {
        return event_user;
    }

    /**
     * イベントの処理時間を設定する
     */
    public void setTime(double time) {
        this.event_time = time;
    }


}
