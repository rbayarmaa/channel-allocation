/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.ArrayList;

/**
 * メルセンヌツイスター
 *
 * @author ginnan
 */
public class MersenneTwister {
    private static final int N = 624;
    private static final int M = 397;
    private static final int UPPER_MASK = 0x80000000;  // 上位1ビット
    private static final int LOWER_MASK = 0x7FFFFFFF;  // 下位31ビット
    private static final int MATRIX_A = 0x9908B0DF;
    private int x[] = new int[N];  // 状態を記憶する配列（リングバッファ）
    private int p, q, r;  // リングバッファのインデックス

    /**
     * 種を指定しない，種として現在時間が使われる．
     */
    public MersenneTwister() {
        setSeed(System.currentTimeMillis());
    }

    /**
     * 種を Long 型の整数で指定する．
     *
     * @param seed
     */
    public MersenneTwister(long seed) {
        setSeed(seed);
    }

    /**
     * 種を Int 型の整数配列で指定する．
     *
     * @param seeds
     */
    public MersenneTwister(int[] seeds) {
        setSeed(seeds);
    }

    synchronized public void setSeed(long seed) {
        if (x == null) return;  //  super() からの呼び出しでは何もしない
        x[0] = (int) seed;
        for (int i = 1; i < N; i++) {
            x[i] = 1812433253 * (x[i - 1] ^ (x[i - 1] >>> 30)) + i;
        }
        p = 0;
        q = 1;
        r = M;
    }

    synchronized public void setSeed(int[] seeds) {
        setSeed(19650218);
        int i = 1, j = 0;
        for (int k = 0; k < Math.max(N, seeds.length); k++) {
            x[i] ^= (x[i - 1] ^ (x[i - 1] >>> 30)) * 1664525;
            x[i] += seeds[j] + j;
            if (++i >= N) {
                x[0] = x[N - 1];
                i = 1;
            }
            if (++j >= seeds.length) j = 0;
        }
        for (int k = 0; k < N - 1; k++) {
            x[i] ^= (x[i - 1] ^ (x[i - 1] >>> 30)) * 1566083941;
            x[i] -= i;
            if (++i >= N) {
                x[0] = x[N - 1];
                i = 1;
            }
        }
        x[0] = 0x80000000;
    }

    synchronized protected int next(int bits) {
        int y = (x[p] & UPPER_MASK) | (x[q] & LOWER_MASK);
        y = x[p] = x[r] ^ (y >>> 1) ^ ((y & 1) * MATRIX_A);
        if (++p == N) p = 0;  // リングバッファの添字を +1
        if (++q == N) q = 0;
        if (++r == N) r = 0;

        y ^= (y >>> 11);  // 多次元分布を良くする調整
        y ^= (y << 7) & 0x9D2C5680;
        y ^= (y << 15) & 0xEFC60000;
        y ^= (y >>> 18);
        return (y >>> (32 - bits));
    }

    /**
     * 0から1までの一様乱数を返す．
     * http://www.math.keio.ac.jp/~matumoto/emt.html
     * のコードと同じ結果を出力する
     *
     * @return 0から1までの一様乱数
     */
    // http://www.math.keio.ac.jp/~matumoto/emt.html
    // のコードと同じ結果を出力する
    public double genrand_real2() {
        long x = next(32);
        if (x < 0) x += 1L << 32;
        return x * (1.0 / 4294967296.0);
    }

    public ArrayList<Integer> shuffle(ArrayList<Integer> list) {
        double rnd[] = new double[list.size()];
        int turn[] = new int[rnd.length];
        for (int i = 0; i < rnd.length; i++) {
            turn[i] = rnd.length;
        }

        // 乱数リストを作る。
        for (int i = 0; i < rnd.length; i++) {
            rnd[i] = genrand_real2();
        }
        // 順位リストを作る。
        int num = 0;
        double val = 1.0;
        for (int i = 0; i < rnd.length; i++) {
            for (int j = 0; j < rnd.length; j++) {
                if (val > rnd[j] && turn[j] == rnd.length) {
                    val = rnd[j];
                    num = j;
                }
            }
            val = 1.0;
            turn[num] = i;
        }
        // listを並び替える。
        int minmax[] = new int[rnd.length];
        for (int i = 0; i < rnd.length; i++) {
            minmax[turn[i]] = i;
        }
        ArrayList<Integer> new_list = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            new_list.add(list.get(minmax[i]));
        }
        return new_list;
    }

//    ＜使用例＞
//    public static void main(String[] args) {
//        int[] seeds = {0x123, 0x234, 0x345, 0x456};
//        MersenneTwister random = new MersenneTwister(seeds);
//        System.out.println("1000 outputs of genrand_int32()");
//        for (int i = 0; i < 1000; i++) {
//            long x = random.nextInt();
//            if (x < 0) x += 1L << 32;
//            String s = "         " + x + " ";
//            System.out.print(s.substring(s.length() - 11));
//            if (i % 5 == 4) System.out.println();
//        }
//        System.out.println();
//        System.out.println("1000 outputs of genrand_real2()");
//        DecimalFormat df = new DecimalFormat("0.00000000 ");
//        for (int i = 0; i < 1000; i++) {
//            System.out.print(df.format(random.genrand_real2()));
//            if (i % 5 == 4) System.out.println();
//        }
//    }

}
