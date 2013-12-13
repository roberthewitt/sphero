package com.orbotix.sample.locator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
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
    private TextView distanceWalked;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        travelled.setColor(Color.BLACK);
        travelled.setAntiAlias(true);
        collision.setColor(Color.rgb(255, 0, 0));
    }

    @Override
    public void showFor(List<CollisionLocatorData> dataList) {
        list = dataList;
        invalidate();
    }

    @Override
    public void setWalkedDistanceView(TextView view) {
        this.distanceWalked = view;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (list == null || list.size() == 0) {
            return;
        }

        final BoundingBoxInfo boxInfo = getBoxInfo(list);
//        Log.e("rob", String.format("boxMagnitude: x=%f, y=%f left=%f, top=%f", boxInfo.size.x, boxInfo.size.y, boxInfo.leftEdge, boxInfo.topEdge));
        float iHaveWalked = 1;

        final int maxXScreenSize = canvas.getWidth();
        final int maxYScreenSize = canvas.getHeight();

        final float xRatio = ((float) maxXScreenSize) / boxInfo.size.x;
        final float yRatio = ((float) maxYScreenSize) / boxInfo.size.y;

        float lastXCoord = Float.MAX_VALUE;
        float lastYCoord = Float.MAX_VALUE;

        for (CollisionLocatorData item : list) {

            float offsetPositionX = item.locatorData.getPositionX() - boxInfo.leftEdge;
            offsetPositionX *= xRatio;

            float offsetPositionY = item.locatorData.getPositionY() - boxInfo.topEdge;
            offsetPositionY *= yRatio;

            if (lastXCoord != Float.MAX_VALUE) {
                canvas.drawLine(lastXCoord, lastYCoord, offsetPositionX, offsetPositionY, travelled);

                final float absX = Math.abs(offsetPositionX - lastXCoord) / xRatio;
                final float absY = Math.abs(offsetPositionY - lastYCoord) / yRatio;
                iHaveWalked += Math.sqrt((absX * absX) + (absY * absY));
            }

            if (item.isCollision) {
                final int xCoord = (int) offsetPositionX;
                final int yCoord = (int) offsetPositionY;
                canvas.drawRect(xCoord - 3, yCoord - 3, xCoord + 3, yCoord + 3, collision);
            }

            lastXCoord = offsetPositionX;
            lastYCoord = offsetPositionY;
        }
        distanceWalked.setText(String.format("Distance Rolled: %.2f meters", iHaveWalked / 100));
    }

    private BoundingBoxInfo getBoxInfo(List<CollisionLocatorData> points) {
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
