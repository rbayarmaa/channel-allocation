package com.lilium.snake.laaandwifisimulator.network;

import java.util.ArrayList;
import java.util.List;

import com.lilium.snake.laaandwifisimulator.sumulator.Constants;

public class ChannelAction {

    // ArrayList<Integer> actionList = ;
    List<List<Integer>> actionList = new ArrayList<>();

    // TODO End nemeh
    ChannelAction() {
        List<ChannelType> list = new ArrayList<>();
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            for (int j = 0; j < 4; j++) {
                list.add(new ChannelType(i, j));
            }
        }
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            for (int j = 0; j < 4; j++) {
                list.add(new ChannelType(i + 10000, j));
            }
        }

    }

    /**
     * Gets an action based on provided index.
     *
     * @param index Index based on which action is selected.
     * @return Returns one of Action values.
     */
    public static Action getActionByIndex(final int index) {
        return VALUES.get(index);
    }
}
