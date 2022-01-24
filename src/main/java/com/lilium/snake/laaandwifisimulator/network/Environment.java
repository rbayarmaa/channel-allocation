package com.lilium.snake.laaandwifisimulator.network;

import com.lilium.snake.laaandwifisimulator.MainWiFi;
import com.lilium.snake.laaandwifisimulator.WifiState;
import com.lilium.snake.network.util.NetworkUtil;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class Environment implements MDP<WifiState, Integer, DiscreteSpace> {
    // Size is 4 as we have 4 actions
    private final DiscreteSpace actionSpace = new DiscreteSpace(4);
    private final MainWiFi game;

    public Environment(final MainWiFi game) {
        this.game = game;
    }

    @Override
    public ObservationSpace<WifiState> getObservationSpace() {
        return new GameObservationSpace();
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public WifiState reset() {
        return game.initializeGame();
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<WifiState> step(final Integer actionIndex) {
        // Find action based on action index
        final Action actionToTake = Action.getActionByIndex(actionIndex);

        // Change direction based on action and move the snake in that direction
        game.changeDirection(actionToTake);
        game.move();

        // If you want to see what is the snake doing while training increase this value
        NetworkUtil.waitMs(0);

        // Get reward
        double reward = game.calculateRewardForActionToTake(actionToTake);

        // Get current state
        final WifiState observation = game.buildStateObservation();

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
