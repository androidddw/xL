package com.nightonke.wowoviewpager.Color;

/**
 * Created by Weiping on 2016/3/5.
 */

public enum ColorChangeType {

    RGB(0),
    HSV(1);

    private int type;

    /**
     * how to perform the process of changing color
     * @param type
     */
    ColorChangeType(int type) {
        this.type = type;
    }

}
