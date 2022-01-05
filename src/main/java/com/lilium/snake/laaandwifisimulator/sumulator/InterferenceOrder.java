/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * 個体を干渉の多いAP順に並べる (現提案手法では使っていない)
 *
 * @author ginnan
 */
public class InterferenceOrder {

    protected int inteference_num;//干渉しているAP, BSの数
    protected int individual_id;//APのID

    public InterferenceOrder(int num, int id) {
        inteference_num = num;
        individual_id = id;
    }

}
