package com.lilium.snake.laaandwifisimulator.network;

import com.lilium.snake.laaandwifisimulator.sumulator.Constants;

import org.deeplearning4j.rl4j.space.ActionSpace;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.factory.Nd4j;

public class CustomActionSpace implements ActionSpace<int[]> {
    protected final Random rnd;
    protected final int size;

    public CustomActionSpace() {
        this.rnd = Nd4j.getRandom();
        this.size = Constants.WiFi_NUM + Constants.LTEU_NUM;
        rnd.setSeed(4);
    }

    @Override
    public int[] randomAction() {
        int[] action = new int[size];
        for (int i = 0; i < action.length; i++) {
            action[i] = rnd.nextInt(4);
        }
        return action;
    }

    @Override
    public Object encode(int[] a) {
        return a;
    }

    @Override
    public int[] noOp() {
        int[] action = new int[size];
        for (int i = 0; i < action.length; i++) {
            action[i] = -1;
        }
        return action;
    }

    @Override
    public int getSize() {
        return size;
    }

}
