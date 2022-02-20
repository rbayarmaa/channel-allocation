package com.lilium.snake.laaandwifisimulator;

import com.lilium.snake.laaandwifisimulator.network.ActionChannel;
import com.lilium.snake.laaandwifisimulator.network.WifiEnvironment;
import com.lilium.snake.laaandwifisimulator.network.WiFiNetworkUtil;
import com.lilium.snake.laaandwifisimulator.sumulator.*;
import com.lilium.snake.network.util.GameStateUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
// import java.util.List;

public class MainWiFi {
    private static final Logger LOG = LoggerFactory.getLogger(MainWiFi.class);

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();// 時間計
        MainWiFi wifi = new MainWiFi();
        long end = System.currentTimeMillis();
        System.out.println("this is END Simulation Time: " + (end - start) + "[ms]");
    }

    final Simulator game;

    public MainWiFi() {
        String[] args1 = {
                "1", // loop_num -> 0: Count of for
                "300", // interval_time -> 1:Frequency allocation interval (GA execution interval)
                "0.0005", // wifi_user_lambda -> 2:WiFi only user arrival rate
                "0.0005", // lteu_user_lambda -> 3: Arrival rate of WiFi + LTE-U users
                "0", // end_condition -> 4: Selection of simulation end condition (0: number of
                     // calls, 1: time)
                "650", // end_num -> 5: Number of calls or time that is the end condition
                "1", // service_set -> 6: User usage (0: file download, 1: fixed time communication)
                "1", // select_method -> 7: Selection of proposed method, etc.
                "1000", // ga_loop_num -> 8: GA loop count
                "5", // mutation_prob -> 9: Mutation probability
                "10", // ga_individual_num -> 10: GA population
                "3", // crossover_parent_num -> 11: Number of pairs of parents at the time of
                     // crossover
                "1" // elite_num -> 12: Number to select in elite selection
        };
        game = new Simulator(new UserParameter(args1));

        final String randomNetworkName = "network-" + System.currentTimeMillis() + ".zip";

        // Create training environment
        final WifiEnvironment mdp = new WifiEnvironment(game);
        final QLearningDiscreteDense<WifiState> dql = new QLearningDiscreteDense<>(
                mdp,
                WiFiNetworkUtil.buildDQNFactory(),
                WiFiNetworkUtil.buildConfig());

        // Start the training
        dql.train();
        mdp.close();

        // Save network
        try {
            dql.getNeuralNet().save(randomNetworkName);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        // Reset the game
        game.initialize();

        // Evaluate just trained network
        evaluateNetwork(game, randomNetworkName);

    }

    private void evaluateNetwork(Simulator game, String randomNetworkName) {
        final MultiLayerNetwork multiLayerNetwork = WiFiNetworkUtil.loadNetwork(randomNetworkName);
        double high_score = 0;
        for (int i = 0; i < 1000; i++) { //RB:number of iteration (ex, for last iteration: score of iteration '999' was -1.0)
            double score = 0;
            while (game.isOngoing()) {
                try {
                    final WifiState state = game.getObservation();
                    final INDArray output = multiLayerNetwork.output(state.getMatrix(), false);
                    double[] data = output.data().asDouble();
                    int maxValueIndex = GameStateUtil.getMaxValueIndex(data);
                    var action = ActionChannel.getCurrentAction(maxValueIndex);

                    score = game.changeChannelOfStation(action);

                    // Needed so that we can see easier what is the game doing
                    WiFiNetworkUtil.waitMs(0);
                } catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                    // game.endGame();
                }
            }

            LOG.info("Score of iteration '{}' was '{}'", i, score);
            if (score > high_score) {
                high_score = score;
            }

            // Reset the game
            game.initialize();
        }
        LOG.info("Finished evaluation of the network, high_score was '{}'", high_score);
    }

}
