package com.orbotix.sample.locator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.orbotix.sample.locator.mapping.CollisionLocatorData;
import com.orbotix.sample.locator.mapping.LocationViewer;

import java.util.List;

/**
 * Created: 13/12/2013
 *
 * @author ryanjohn
 */
public class MapView extends View implements LocationViewer {

    private final Paint travelled = new Paint();
    private final Paint collision = new Paint();
    private List<CollisionLocatorData> list;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        travelled.setColor(Color.BLACK);
        travelled.setAntiAlias(true);
        collision.setColor(Color.rgb(255,0,0));
    }

    @Override
    public void showFor(List<CollisionLocatorData> dataList) {
        list = dataList;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (list == null || list.size() == 0) {
            return;
        }

        final BoundingBoxInfo boxInfo = getBoundsOfPoints(list);

        final int maxXScreenSize = canvas.getWidth();
        final int maxYScreenSize = canvas.getHeight();

        final float xRatio = ((float) maxXScreenSize) / boxInfo.size.x;
        final float yRatio = ((float) maxYScreenSize) / boxInfo.size.y;

        float lastXCoord = Float.MAX_VALUE;
        float lastYCoord = Float.MAX_VALUE;

        for (CollisionLocatorData item : list) {

            float offsetPositionX = item.locatorData.getPositionX() + boxInfo.leftEdge;
            float offsetPositionY = item.locatorData.getPositionY() + boxInfo.topEdge;
            offsetPositionX *= xRatio;
            offsetPositionY *= yRatio;

            if (lastXCoord != Float.MAX_VALUE) {
                canvas.drawLine(lastXCoord, lastYCoord, offsetPositionX, offsetPositionY, travelled);
            }
            lastXCoord = offsetPositionX;
            lastYCoord = offsetPositionY;

            if (item.isCollision) {
                final int xCoord = (int) lastXCoord;
                final int yCoord = (int) lastYCoord;
                canvas.drawRect(xCoord - 3, yCoord - 3, xCoord + 3, yCoord + 3, collision);
            }
        }
    }

    private BoundingBoxInfo getBoundsOfPoints(List<CollisionLocatorData> points) {
        float maxX = 0f, maxY = 0f, minX = 0f, minY = 0f;
        for (CollisionLocatorData item : points) {
            final float positionX = item.locatorData.getPositionX();
            final float positionY = item.locatorData.getPositionY();
            if (positionX > maxX) {
                maxX = positionX;
            }
            if (positionY > maxY) {
                maxY = positionY;
            }
            if (positionX < minX) {
                minX = positionX;
            }
            if (positionY < minY) {
                minY = positionY;
            }
        }
        final FloatPoint boxSize = new FloatPoint(maxX - minX, maxY - minY);
        return new BoundingBoxInfo(boxSize, minX, minY);
    }

    private class FloatPoint {
        final float x;
        final float y;

        private FloatPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private class BoundingBoxInfo {
        final FloatPoint size;
        final float leftEdge;
        final float topEdge;

        private BoundingBoxInfo(FloatPoint size, float leftEdge, float topEdge) {
            this.size = size;
            this.leftEdge = leftEdge;
            this.topEdge = topEdge;
        }
    }
}
