![logo](http://update.orbotix.com/developer/sphero-small.png)

# Locator




        @Override
        public void onLocatorChanged(LocatorData locatorData) {
        	// Do stuff with the locator data	
        }
    }
    
## Attaching the Data Streaming Listener

Assuming you already have a Sphero object that is connected, attaching the listener is done with this line.

	mRobot.getSensorControl().addLocatorListener(mLocatorListener);
	
## Configuring the Data Streaming Listener

Now that the listener is connected, you must set the data streaming rate (at the least). The maximum sensor sampling rate is ~420 Hz, but we recommend using a value in the 20-40Hz range for Android devices. At 20 Hz, virtually every device will not see a slowdown from the packet detection. However, 40 Hz is only viable when targeting only high-end devices. To set the streaming value, use the `setRate(int hz)` member method of the *SensorControl* class of the Sphero.

	mRobot.getSensorControl().setRate(20 /*Hz*/);
	
Now you're set to receive locator data from the Sphero!




2. **newX**: Set the current X coordinates of Sphero on the ground plane in centimeters.
3. **newY**: Set the current Y coordinates of Sphero on the ground plane in centimeters.
4. **newYaw**: Controls how the X,Y-plane is aligned with Sphero’s heading coordinate system. When this parameter is set to zero, it means that having yaw = 0 corresponds to facing down the Y- axis in the positive direction. The value will be interpreted in the range 0-359 inclusive.












For questions, please visit our developer's forum at [http://forum.gosphero.com/](http://forum.gosphero.com/)

	 