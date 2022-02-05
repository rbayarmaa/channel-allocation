package com.lilium.snake.laaandwifisimulator.network;

import com.lilium.snake.laaandwifisimulator.Simulator;
import com.lilium.snake.laaandwifisimulator.WifiState;
import com.lilium.snake.network.util.NetworkUtil;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class Environment implements MDP<WifiState, Integer, DiscreteSpace> {
    // Size is 4 as we have 4 actions
    private final DiscreteSpace actionSpace = new DiscreteSpace(107 * 4);
    private final Simulator game;

    public Environment(final Simulator game) {
        this.game = game;
    }

    @Override
    public ObservationSpace<WifiState> getObservationSpace() {
        // TODO ENd yag yu hiih yostoigoo daraa shiideh heregtei
        return new WIfiObservationSpace();
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public WifiState reset() {
        return game.initialize();
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<WifiState> step(final Integer actionIndex) {
        // Find action based on action index
        final ActionChannel actionToTake = new ActionChannel(actionIndex);

        // Change direction based on action and move the snake in that direction
        game.changeChannelOfStation(actionToTake);
        game.move();

        // If you want to see what is the snake doing while training increase this

        NetworkUtil.waitMs(0);

        // Get reward
        double reward = game.calculateRewardForActionToTake(actionToTake);

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
    public MDP<WifiState, Integer, DiscreteSpace> newInstance() {
        game.initializeGame();
        return new Environment(game);
    }
}
