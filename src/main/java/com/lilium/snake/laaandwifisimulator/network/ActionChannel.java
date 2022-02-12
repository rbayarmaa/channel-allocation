package com.lilium.snake.laaandwifisimulator.network;

import com.lilium.snake.laaandwifisimulator.sumulator.Constants;

public class ActionChannel {
    public int id;
    public int channel;
    public boolean isWifi;

    public ActionChannel() {

    }

    void setAction(int action) {
        this.id = (action / 4);

        isWifi = this.id < Constants.WiFi_NUM;
        if (!isWifi)
            id = id + 10000;
        this.channel = (action % 4) + 1;
    }

    private static ActionChannel currentAction = new ActionChannel();

    public static ActionChannel getCurrentAction(int action) {
        currentAction.setAction(action);
        return currentAction;
    }
}
