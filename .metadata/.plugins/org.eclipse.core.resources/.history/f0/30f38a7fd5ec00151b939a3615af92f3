package com.nightonke.wowoviewpager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

import com.nightonke.wowoviewpager.Color.ColorChangeType;
import com.nightonke.wowoviewpager.Eases.EaseType;

/**
 * Created by Weiping on 2016/3/3.
 */

/**
 * animation to change the color of state-list-drawable of view
 */

public class WoWoStateListColorAnimation extends PageAnimation {

    private EaseType easeType;
    private boolean useSameEaseTypeBack = true;

    private ColorChangeType colorChangeType;

    private int[] targetColor;
    private int[] fromColor;

    private int[] targetA;
    private int[] targetR;
    private int[] targetG;
    private int[] targetB;
    private float[][] targetHSV;
    private int[] fromA;
    private int[] fromR;
    private int[] fromG;
    private int[] fromB;
    private float[][] fromHSV;

    /**
     *
     * @param page animation starting page
     * @param startOffset animation starting offset
     * @param endOffset animation ending offset
     * @param fromColor original color
     * @param targetColor target color
     * @param colorChangeType how to change the color.
     *                        For more information, please check the ColorChangeType.class
     * @param easeType ease type.
     *                 For more information, please check the EaseType.class
     * @param useSameEaseTypeBack whether use the same ease type to back
     */
    public WoWoStateListColorAnimation(
            int page,
            float startOffset,
            float endOffset,
            int[] fromColor,
            int[] targetColor,
            ColorChangeType colorChangeType,
            EaseType easeType,
            boolean useSameEaseTypeBack) {

        setPage(page);
        setStartOffset(startOffset);
        setEndOffset(endOffset);

        this.easeType = easeType;
        this.useSameEaseTypeBack = useSameEaseTypeBack;
        this.fromColor = fromColor;
        this.targetColor = targetColor;
        setARGBandHSV();

        this.colorChangeType = colorChangeType;
    }

    private float lastPositionOffset = -1;

    private boolean lastTimeIsExceed = false;
    private boolean lastTimeIsLess = false;

    @Override
    public void play(View onView, float positionOffset) {

        if (positionOffset <= getStartOffset()) {
            if (lastTimeIsLess) return;
            StateListDrawable stateListDrawable = (StateListDrawable) onView.getBackground();
            DrawableContainer.DrawableContainerState drawableContainerState
                    = (DrawableContainer.DrawableContainerState)
                    stateListDrawable.getConstantState();
            Drawable[] drawables = drawableContainerState.getChildren();
            int length = fromColor.length;
            for (int i = 0; i < length; i++) {
                ((GradientDrawable)drawables[i]).setColor(fromColor[i]);
            }
            lastTimeIsLess = true;
            return;
        }
        lastTimeIsLess = false;

        // if the positionOffset exceeds the endOffset,
        // we should set onView to target color
        // otherwise there may be offsets between target color and actually color
        // if the last time we do this, just return
        if (positionOffset >= getEndOffset()) {
            if (lastTimeIsExceed) return;
            StateListDrawable stateListDrawable = (StateListDrawable) onView.getBackground();
            DrawableContainer.DrawableContainerState drawableContainerState
                    = (DrawableContainer.DrawableContainerState)
                    stateListDrawable.getConstantState();
            Drawable[] drawables = drawableContainerState.getChildren();
            int length = targetColor.length;
            for (int i = 0; i < length; i++) {
                ((GradientDrawable)drawables[i]).setColor(targetColor[i]);
            }
            lastTimeIsExceed = true;
            return;
        }
        lastTimeIsExceed = false;

        // get the true offset
        positionOffset = (positionOffset - getStartOffset()) / (getEndOffset() - getStartOffset());
        float movementOffset;

        if (lastPositionOffset == -1) {
            // first movement
            movementOffset = easeType.getOffset(positionOffset);
        } else {
            if (positionOffset < lastPositionOffset) {
                // back
                if (useSameEaseTypeBack) {
                    movementOffset = 1 - easeType.getOffset(1 - positionOffset);
                } else {
                    movementOffset = easeType.getOffset(positionOffset);
                }
            } else {
                // forward
                movementOffset = easeType.getOffset(positionOffset);
            }
        }
        lastPositionOffset = positionOffset;

        StateListDrawable stateListDrawable = (StateListDrawable) onView.getBackground();
        DrawableContainer.DrawableContainerState drawableContainerState
                = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
        Drawable[] drawables = drawableContainerState.getChildren();
        int length = targetColor.length;
        if (colorChangeType == ColorChangeType.RGB) {
            for (int i = 0; i < length; i++) {
                ((GradientDrawable)drawables[i]).setColor(
                        Color.argb(
                                fromA[i] + (int)((targetA[i] - fromA[i]) * movementOffset),
                                fromR[i] + (int)((targetR[i] - fromR[i]) * movementOffset),
                                fromG[i] + (int)((targetG[i] - fromG[i]) * movementOffset),
                                fromB[i] + (int)((targetB[i] - fromB[i]) * movementOffset))
                );
            }
        } else {
            for (int i = 0; i < length; i++) {
                ((GradientDrawable)drawables[i]).setColor(
                        Color.HSVToColor
                            (new float[]{
                                fromHSV[i][0] + (targetHSV[i][0] - fromHSV[i][0]) * movementOffset,
                                fromHSV[i][1] + (targetHSV[i][1] - fromHSV[i][1]) * movementOffset,
                                fromHSV[i][2] + (targetHSV[i][2] - fromHSV[i][2]) * movementOffset}
                            )
                );
            }
        }
    }

    private void setARGBandHSV() {
        int length = targetColor.length;
        targetA = new int[length];
        targetR = new int[length];
        targetG = new int[length];
        targetB = new int[length];
        targetHSV = new float[length][3];
        fromA = new int[length];
        fromR = new int[length];
        fromG = new int[length];
        fromB = new int[length];
        fromHSV = new float[length][3];
        for (int i = 0; i < length; i++) {
            targetA[i] = Color.alpha(targetColor[i]);
            targetR[i] = Color.red(targetColor[i]);
            targetG[i] = Color.green(targetColor[i]);
            targetB[i] = Color.blue(targetColor[i]);
            Color.colorToHSV(targetColor[i], targetHSV[i]);

            fromA[i] = Color.alpha(fromColor[i]);
            fromR[i] = Color.red(fromColor[i]);
            fromG[i] = Color.green(fromColor[i]);
            fromB[i] = Color.blue(fromColor[i]);
            Color.colorToHSV(fromColor[i], fromHSV[i]);
        }
    }
}
