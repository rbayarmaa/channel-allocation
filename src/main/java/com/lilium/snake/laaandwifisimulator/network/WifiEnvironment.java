package com.lilium.snake.laaandwifisimulator.network;

import java.io.IOException;

import com.lilium.snake.laaandwifisimulator.Simulator;
import com.lilium.snake.laaandwifisimulator.WifiState;
import com.lilium.snake.laaandwifisimulator.sumulator.Constants;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.factory.Nd4j;

public class WifiEnvironment implements MDP<WifiState, int[], CustomActionSpace> {
    // Size is 4 as we have 4 actions

    private final CustomActionSpace actionSpace;
    private final Simulator game;

    public WifiEnvironment(final Simulator game) {
        this.game = game;
        var rnd = Nd4j.getRandom();
        var size = ((Constants.WiFi_NUM + Constants.LTEU_NUM) * 4);

        rnd.setSeed(size);
        actionSpace = new CustomActionSpace();
    }

    @Override
    public ObservationSpace<WifiState> getObservationSpace() {
        // TODO ENd yag yu hiih yostoigoo daraa shiideh heregtei
        return new WIfiObservationSpace();
    }

    @Override
    public CustomActionSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public WifiState reset() {
        try {
            return game.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<WifiState> step(final int[] actionIndex) {
        // Find action based on action index

        // Change direction based on action and move the snake in that direction
        double reward = game.changeChannelOfStation(actionIndex);

        // Get current state
        final WifiState observation = game.getObservation();

        return new StepReply<>(
                observation,
                reward,
                isDone(),
                "SnakeDl4j");
    }

    @Override
    public boolean isDone() {
        return !game.isOngoing();
    }

    @Override
    public MDP<WifiState, int[], CustomActionSpace> newInstance() {
        try {
            game.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new WifiEnvironment(game);
    }
}
