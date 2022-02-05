/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;

/**
 * WiFiとLTE-Uの混在環境におけるチャネル割り当て,接続先選択シミュレータ
 *
 * @author ginnan
 */
public class LAAandWiFiSimulator {

    /**
     * @param args the command line arguments
     */
    /*
     * Argument list
     * 0: loop_num -> Number of loops (number of simulations)
     * 1: interval_time -> Frequency allocation interval (execution interval of the
     * proposed method)
     * 2: wifi_user_lambda -> WiFi only user arrival rate
     * 3: lteu_user_lambda -> Arrival rate of WiFi + LTE-U users
     * 4: end_condition -> Selection of simulation end conditions(0: number of
     * calls, 1: time)
     * 5: end_num -> Number of calls or time that is the end condition
     * 6: service_set -> User's service usage pattern (0: File download, 1:
     * Communication for a certain period of time)
     * 7: select_method -> Selection of method to use, such as proposed method
     * 8:GA Number of loops
     * 9:Probability of mutation
     * 10:GA population
     * 11:Number of pairs of parents at the time of crossing
     * 12:Number to select in elite selection
     * 
     */
    public static UserParameter _param;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        long start = System.currentTimeMillis();// measurement of time
        // 引数の代入n.args=1 300 0.0005 0.0005 0 650 1 1 1000 5 10 3 1
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
        _param = new UserParameter(args1);

        // Creating a class that outputs the simulation results to a file
        Output output = new Output(_param);

        System.out.println("Capacities installing.... ");

        // Set capacity when sharing channels
        Constants.CAPACITY_WITH_LAA_WIFI = Utility.SetCapacitySharedWiFiLTEU();
        Constants.CAPACITY_WITH_WIFIS = Utility.SetCapacitySharedWiFi();

        System.out.println("Topology installing....");

        // Get area, AP, BS coverage, LTE-U placement location
        AreaTopology Topology = new AreaTopology();

        // Creating a scenario
        Scenario scenario;

        Constants.SERVICE_SET = _param.service_set;

        Constants.are = args1[2];

        for (int i = 0; i < _param.loop_num; i++) {
            /* When looping with a for statement */
            System.out.println("Loop " + (i + 1));

            scenario = new Scenario(i + 30, _param, Topology);
            /* */

            /* When producing a result for each time */
            // System.out.println("Loop " + _param.loop_num);
            // scenario = new Scenario((40+_param.loop_num), _param, Topology);
            /* */
            scenario.startSimulation();
            output.update(scenario);

            // The one that recorded the throughput of all users during the simulation →
            // Used to get the cumulative distribution
            // if (_param.loop_num == 1) {
            // output.writeToFile_Throughput(scenario.getData().wifi_users_throghput,
            // scenario.getData().lteu_users_throghput,
            // scenario.getData().wifi_users_min_throghput,
            // scenario.getData().lteu_users_min_throghput);
            //
            // output.writeToFile_Throughput_time_Avg(scenario.getData().wifi_users_min_time_throghput,
            // scenario.getData().lteu_users_min_time_throghput);
            // }
        } // for_loop (turn off if simulating each time)******************
        output.executeSimEnd();

        // Write simulation results to a file and output to screen
        if (Constants.SERVICE_SET == 0) {// For file download
            output.writeToFile();
            output.printToScreen();
        } else {// For fixed-time communication
            output.writeToFile2();
            output.printToScreen2();

        }

        // measurement of time
        long end = System.currentTimeMillis();
        System.out.println("Simulation Time: " + (end - start) + "[ms]");
        output.writeToFile_SimuTime(end - start);

    }

}
