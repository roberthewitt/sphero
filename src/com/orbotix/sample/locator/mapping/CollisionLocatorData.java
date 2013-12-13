package com.orbotix.sample.locator.mapping;

import orbotix.robot.sensor.LocatorData;

/**
* Created: 13/12/2013
*
* @author roberthewitt
*/
public class CollisionLocatorData {
    public LocatorData locatorData;
    public boolean isCollision;

    public CollisionLocatorData(LocatorData locatorData, boolean collision) {
        this.locatorData = locatorData;
        this.isCollision = collision;
    }
}
