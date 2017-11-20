/**
 * Copyright (C) 2012 Iordan Iordanov
 * Copyright (C) 2009 Michael A. MacDonald
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */

package com.iiordanov.aSPICE;

import com.iiordanov.aSPICE.RemoteCanvas;
import com.iiordanov.aSPICE.R;

import android.graphics.Matrix;
import android.widget.ImageView.ScaleType;

/**
 * @author Michael A. MacDonald
 */
class FitToScreenScaling extends AbstractScaling {
    
    static final String TAG = "FitToScreenScaling";
    
    private Matrix matrix;
    int canvasXOffset;
    int canvasYOffset;
    float scaling;
    float minimumScale;
    
    /**
     * @param id
     * @param scaleType
     */
    public FitToScreenScaling() {
        super(R.id.itemFitToScreen, ScaleType.FIT_CENTER);
        matrix = new Matrix();
        scaling = 0;
    }

    /* (non-Javadoc)
     * @see com.iiordanov.aSPICE.AbstractScaling#getDefaultHandlerId()
     */
    @Override
    int getDefaultHandlerId() {
        return R.id.itemInputTouchPanZoomMouse;
    }

    /* (non-Javadoc)
     * @see com.iiordanov.aSPICE.AbstractScaling#isAbleToPan()
     */
    @Override
    boolean isAbleToPan() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.iiordanov.aSPICE.AbstractScaling#isValidInputMode(int)
     */
    @Override
    boolean isValidInputMode(int mode) {
        return true;
    }
    
    /**
     * Call after scaling and matrix have been changed to resolve scrolling
     * @param activity
     */
    private void resolveZoom(RemoteCanvasActivity activity)
    {
        activity.getCanvas().scrollToAbsolute();
        //activity.vncCanvas.pan(0,0);
    }
    
    /* (non-Javadoc)
     * @see com.iiordanov.aSPICE.AbstractScaling#getScale()
     */
    @Override
    float getScale() {
        return scaling;
    }

    private void resetMatrix()
    {
        matrix.reset();
        matrix.preTranslate(canvasXOffset, canvasYOffset);
    }

    /* (non-Javadoc)
     * @see com.iiordanov.aSPICE.AbstractScaling#setScaleTypeForActivity(com.iiordanov.aSPICE.VncCanvasActivity)
     */
    @Override
    void setScaleTypeForActivity(RemoteCanvasActivity activity) {
        super.setScaleTypeForActivity(activity);
        RemoteCanvas canvas = activity.getCanvas();
        canvas.absoluteXPosition = 0;
        canvas.absoluteYPosition = 0;
        canvasXOffset = -canvas.getCenteredXOffset();
        canvasYOffset = -canvas.getCenteredYOffset();
        canvas.computeShiftFromFullToView ();
        minimumScale = canvas.bitmapData.getMinimumScale();
        scaling = minimumScale;
        resetMatrix();
        matrix.postScale(scaling, scaling);
        canvas.setImageMatrix(matrix);
        resolveZoom(activity);
        canvas.pan(0, 0);
        activity.zoomer.setIsZoomOutEnabled(false);
        activity.zoomer.setIsZoomInEnabled(false);
    }
}
