/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * csvファイルから、エリアのトポロジ(APの配置)情報を取得する
 *
 * @author ginnan
 */
public final class AreaTopology {

    /* WiFi APの配置に関する情報 */
    protected int ap_bs_position_num; //AP,BSを配置可能なエリアの数
    protected int ap_cover_num;   //APがカバーしているエリアの数
    protected int[][] ap_position_cover_area;    // WiFi APがカバーしているエリアの情報
    protected int area_covered_ap_num;    //最小エリアをカバーしているAPの数(APがすべての位置に配置された場合を仮定) 
    protected int[][] area_ap_cover;  //最小エリアをカバーしているAPの情報

    /* LTE-U BSの配置に関する情報 */
    protected int bs_cover_num;   //最小エリアをカバーしているBSの数
    protected int[][] bs_position_cover_area;    // LTE-U BSがカバーしているエリアの情報
    protected int area_covered_bs_num;    //最小エリアをカバーしているBSの数(BSがすべての位置に配置された場合を仮定)
    protected int[][] area_bs_cover;  //最小エリアをカバーしているBSの情報   
    protected int[] lteu_place; //LTE-Uの配置場所の情報
    protected int[] lteu_channel_assign;//LTE-Uのチャネル割り当て(おそらく変更はない)

    public AreaTopology() {
        ap_bs_position_num = Constants.AP_BS_POSITION_NUM;
        ap_cover_num = Constants.AP_COVER_NUM;
        ap_position_cover_area = new int[ap_bs_position_num][ap_cover_num];
        area_covered_ap_num = Constants.AREA_COVERED_AP_NUM;
        area_ap_cover = new int[Constants.AREA_NUM][area_covered_ap_num];

        bs_cover_num = Constants.BS_COVER_NUM;
        bs_position_cover_area = new int[ap_bs_position_num][bs_cover_num];
        area_covered_bs_num = Constants.AREA_COVERED_BS_NUM;
        area_bs_cover = new int[Constants.AREA_NUM][area_covered_bs_num];
        lteu_place = new int[Constants.LTEU_NUM];
        lteu_channel_assign = new int[Constants.LTEU_NUM];

        setAPCoverArea();
//        SetCoverAP();
        setBSCoverArea();
//        SetCoverBS();
        SetLTEUdeployedArea();

    }

    //AP,BSの配置可能位置において、APがカバーしているエリアのセット
    public void setAPCoverArea() {
        int a, b;

        try {
            File csv; //AP,BSのカバー情報のcsvファイル
            csv = new File("ap_cover.csv"); //csvの構成: APの位置ID カバーエリア0,カバーエリア1....
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    a = Integer.valueOf(st.nextToken().trim()).intValue();
                    for (int cover = 0; cover < ap_cover_num; cover++) {
                        b = Integer.valueOf(st.nextToken().trim()).intValue();
                        ap_position_cover_area[a][cover] = b;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

    }

    //最小エリアをどのAPがカバーしているか。
    public void SetCoverAP() {//使っていない
        int a, b;

        try {
            File csv;
            csv = new File("ap_covered.csv"); //csvの構成: エリアID, AP_id, AP_id,...
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    a = Integer.valueOf(st.nextToken()).intValue();
                    for (int cover = 0; cover < area_covered_ap_num; cover++) {
                        b = Integer.valueOf(st.nextToken()).intValue();
                        area_ap_cover[a][cover] = b;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

    }

    //AP,BSの位置において、BSがカバーしているエリアのセット
    public void setBSCoverArea() {
        int a, b;

        try {
            File csv;
            csv = new File("bs_cover.csv"); //csvの構成: BSの位置ID カバーエリア0,カバーエリア1....
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    a = Integer.valueOf(st.nextToken().trim()).intValue();
                    for (int cover = 0; cover < bs_cover_num; cover++) {
                        b = Integer.valueOf(st.nextToken().trim()).intValue();
                        bs_position_cover_area[a][cover] = b;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

    }

    //最小エリアをどのBSがカバーしているか
    public void SetCoverBS() {//使っていない
        int a, b;

        try {
            File csv;
            csv = new File("bs_covered.csv"); //csvの構成: エリアID, BS_id, BS_id,...
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    a = Integer.valueOf(st.nextToken()).intValue();
                    for (int cover = 0; cover < area_covered_bs_num; cover++) {
                        b = Integer.valueOf(st.nextToken()).intValue();
                        area_bs_cover[a][cover] = b;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

    }

    //LTE-U BSの配置場所、LTE-U BSの割り当てチャネルをセット
    public void SetLTEUdeployedArea() {
        int lte_id = 0;
        try {
            File csv; //AP,BSのカバー情報のcsvファイル
            csv = new File("lteu_position.csv"); //csvの構成: 一列のみ、各行にLTE-U BSの配置
            BufferedReader br = new BufferedReader(new FileReader(csv));

            //最終行まで読む
            String line = "";
            while ((line = br.readLine()) != null) {
                //一行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    lteu_place[lte_id] = Integer.valueOf(st.nextToken().trim()).intValue();
                    lteu_channel_assign[lte_id] = Integer.valueOf(st.nextToken().trim()).intValue();
                }
                lte_id++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

    }

}
