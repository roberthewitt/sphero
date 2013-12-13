package com.orbotix.sample.locator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.orbotix.sample.locator.mapping.CollisionLocatorData;
import com.orbotix.sample.locator.mapping.LocationViewer;

import java.util.List;

/**
 * Created: 13/12/2013
 *
 * @author ryanjohn
 *
 */
public class MapView extends View implements LocationViewer{

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showFor(List<CollisionLocatorData> dataList) {

    }
}
