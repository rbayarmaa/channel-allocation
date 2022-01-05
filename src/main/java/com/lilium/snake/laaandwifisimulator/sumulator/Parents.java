/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * 親の組を保存するクラス(子個体の保存にも使っているので注意)
 *
 * @author ginnan
 */
public class Parents implements Cloneable {
    protected int parent_number;
    protected int[][] Parent1;//親個体1
    protected int[][] Parent2;//親個体2

    public Parents(int id) {
        this.parent_number = id;
        this.Parent1 = new int[Constants.LTEU_NUM + Constants.WiFi_NUM][2];
        this.Parent2 = new int[Constants.LTEU_NUM + Constants.WiFi_NUM][2];
    }

    @Override
    public Parents clone() throws CloneNotSupportedException {
        Parents CloneParents = (Parents) super.clone();
////        CloneParents.Set(this.Parent1,this.Parent2,this.parent_number);
        return CloneParents;
    }

    public Parents(Parents parents) {
        this.Set(parents.Parent1, parents.Parent2, parents.parent_number);
    }

    private void Set(int[][] p1, int[][] p2, int num) {
        parent_number = num;
        Parent1 = p1;
        Parent2 = p2;
    }
}
