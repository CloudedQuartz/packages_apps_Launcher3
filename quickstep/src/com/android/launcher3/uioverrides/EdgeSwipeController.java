/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.uioverrides;

import static com.android.launcher3.LauncherState.NORMAL;
import static com.android.launcher3.LauncherState.OVERVIEW;
import static com.android.launcher3.touch.SwipeDetector.DIRECTION_NEGATIVE;
import static com.android.launcher3.touch.SwipeDetector.DIRECTION_POSITIVE;
import static com.android.launcher3.touch.SwipeDetector.HORIZONTAL;
import static com.android.launcher3.touch.SwipeDetector.VERTICAL;
import static com.android.quickstep.TouchInteractionService.EDGE_NAV_BAR;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.android.launcher3.Launcher;
import com.android.launcher3.anim.SpringAnimationHandler;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.util.VerticalSwipeController;
import com.android.quickstep.RecentsView;

/**
 * Extension of {@link VerticalSwipeController} to go from NORMAL to OVERVIEW.
 */
public class EdgeSwipeController extends VerticalSwipeController {

    private static final Rect sTempRect = new Rect();

    public EdgeSwipeController(Launcher l) {
        super(l, NORMAL, OVERVIEW, l.getDeviceProfile().isVerticalBarLayout()
                ? HORIZONTAL : VERTICAL);
    }

    @Override
    protected boolean shouldInterceptTouch(MotionEvent ev) {
        return mLauncher.isInState(NORMAL) && (ev.getEdgeFlags() & EDGE_NAV_BAR) != 0;
    }

    @Override
    protected int getSwipeDirection(MotionEvent ev) {
        return isTransitionFlipped() ? DIRECTION_NEGATIVE : DIRECTION_POSITIVE;
    }

    @Override
    protected boolean isTransitionFlipped() {
        if (mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            Rect insets = mLauncher.getDragLayer().getInsets();
            return insets.left > insets.right;
        }
        return false;
    }

    @Override
    protected void onTransitionComplete(boolean wasFling, boolean stateChanged) {
        // TODO: Log something
    }

    @Override
    protected void initSprings() {
        mSpringHandlers = new SpringAnimationHandler[0];
    }

    @Override
    protected float getShiftRange() {
        return getShiftRange(mLauncher);
    }

    public static float getShiftRange(Launcher launcher) {
        RecentsView.getPageRect(launcher.getDeviceProfile(), launcher, sTempRect);
        DragLayer dl = launcher.getDragLayer();
        Rect insets = dl.getInsets();

        if (launcher.getDeviceProfile().isVerticalBarLayout()) {
            if (insets.left > insets.right) {
                return insets.left + sTempRect.left;
            } else {
                return dl.getWidth() - sTempRect.right + insets.right;
            }
        } else {
            return dl.getHeight() - sTempRect.bottom + insets.bottom;
        }
    }
}
