package com.orbotix.sample.locator.mapping;

import android.widget.TextView;

import java.util.List;

/**
 * Created: 13/12/2013
 *
 * @author roberthewitt
 */
public interface LocationViewer {

    public void showFor(List<CollisionLocatorData> dataList);

    public void setWalkedDistanceView(TextView view);

}
