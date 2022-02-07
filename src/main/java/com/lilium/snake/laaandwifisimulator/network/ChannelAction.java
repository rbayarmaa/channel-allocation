package com.lilium.snake.laaandwifisimulator.network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.lilium.snake.laaandwifisimulator.sumulator.Constants;

import freemarker.core.ReturnInstruction.Return;

public class ChannelAction {

    public static void main(String[] args) {
        var channel = new ChannelAction();
    }

    // ArrayList<Integer> actionList = ;
    List<int[]> actionList = new ArrayList<>();

    // TODO End nemeh
    ChannelAction() {
        // List<ChannelType> list = new ArrayList<>();

        int countOfAp = 3;
        var listApCh = new int[countOfAp]; // permanent temp state
        permanent(listApCh.length - 2, listApCh.length - 1, listApCh);

    }

    private void permanent(int index, int end, int[] listApCh) {
        // permanent temp state
        int currentChannelIndex = listApCh.length - 1; // hamgiin suulees butsaad bagasahaar index lex

        for (int i = listApCh.length - 1; i >= 0; i--) {
            for (int j = i; j < listApCh.length; j++) {
                for (int l = j + 1; l < listApCh.length; l++) {
                    for (int k = 0; k < 4; k++) {
                        listApCh[l] = k;
                        actionList.add(listApCh.clone());
                    }
                }
                currentChannelIndex++;
            }
            currentChannelIndex++;
        }
        currentChannelIndex++;
        // while (index < end) {

        // if (listApCh[currentChannelIndex] > 3) {
        // listApCh[currentChannelIndex] = 0;
        // currentChannelIndex--;
        // permanent(index - 1, end, listApCh);
        // } else {
        // actionList.add(listApCh.clone());
        // listApCh[currentChannelIndex]++;
        // }

        // }
        // permanent(index - 1, listApCh);
    }

}

// /**
// * Gets an action based on provided index.
// *
// * @param index Index based on which action is selected.
// * @return Returns one of Action values.
// */
// public static Action getActionByIndex(final int index) {
// return VALUES.get(index);
// }
// }
