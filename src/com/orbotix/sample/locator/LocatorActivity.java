package com.orbotix.sample.locator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import orbotix.robot.base.*;
import orbotix.robot.sensor.LocatorData;
import orbotix.sphero.CollisionListener;
import orbotix.sphero.ConnectionListener;
import orbotix.sphero.LocatorListener;
import orbotix.sphero.Sphero;
import orbotix.view.connection.SpheroConnectionView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocatorActivity extends Activity {

    private static final String TAG = "OBX-LocatorSample";
    /** Robot to from which we are streaming */
    private Sphero mRobot = null;

    /** The Sphero Connection View */
    private SpheroConnectionView mSpheroConnectionView;

    /** Collision Parameters*/
    int xt=200;
    int xsp=0;
    int yt=125;
    int ysp=0;
    int deadTime=100;

    private float currentAngle = 0;

    private Random randomGenerator;
    private List<CollisionLocatorData> locationList = new ArrayList<CollisionLocatorData>();

    private LocatorListener mLocatorListener = new LocatorListener() {
        @Override
        public void onLocatorChanged(LocatorData locatorData) {
            if (locatorData != null) {
                Log.d(TAG, locatorData.toString());
                locationList.add(new CollisionLocatorData(locatorData, false));
            }
        }
    };
    private Handler handler;
    private boolean stoppingMapping = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        randomGenerator = new Random();
        currentAngle = randomGenerator.nextInt(359);

        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMapping();
            }
        });

        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMapping();
            }
        });

        mSpheroConnectionView = (SpheroConnectionView) findViewById(R.id.sphero_connection_view);
        mSpheroConnectionView.setSingleSpheroMode(true);
        // Set the connection event listener 
        mSpheroConnectionView.addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected(Robot sphero) {
                mRobot = (Sphero) sphero;
                // Skip this next step if you want the user to be able to connect multiple Spheros
                mSpheroConnectionView.setVisibility(View.INVISIBLE);
                mRobot.getSensorControl().addLocatorListener(mLocatorListener);
                mRobot.getSensorControl().setRate(5);
                mRobot.getCollisionControl().startDetection(xt, xsp, yt, ysp, deadTime);
                mRobot.getCollisionControl().addCollisionListener(mCollisionListener);
                startStuckHandler();
            }

            @Override
            public void onConnectionFailed(Robot sphero) {
                // let the SpheroConnectionView handle or hide it and do something here...
            }

            @Override
            public void onDisconnected(Robot sphero) {
                mSpheroConnectionView.startDiscovery();
            }
        });
    }

    private final CollisionListener mCollisionListener = new CollisionListener() {
        public void collisionDetected(CollisionDetectedAsyncData collisionData) {
            // Do something with the collision data
            Log.i("Sphero", "X,Y " + getLastLocatorData().getPositionX() + " , " + getLastLocatorData().getPositionY());
            getLastCollisionLocationData().isCollision = true;
            mRobot.stop();
            randomDrive();
        }
    };

    /** Called when the user comes back to this app */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list of Spheros
        mSpheroConnectionView.startDiscovery();
    }

    /** Called when the user presses the back or home button */
    @Override
    protected void onPause() {
        super.onPause();
        //Set the AsyncDataListener that will process each response.
        if (mRobot != null) {
            mRobot.getSensorControl().removeLocatorListener(mLocatorListener);
            mRobot.disconnect();           // Disconnect Robot properly
        }
    }

    public void startMapping() {
        stoppingMapping = false;
        randomDrive();
    }

    public void stopMapping() {
        mRobot.stop();
        stoppingMapping = true;
    }

    private void randomDrive() {
        currentAngle = randomGenerator.nextInt(359);
        mRobot.drive(currentAngle, 0.6f);
    }

    private void startStuckHandler() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (getLastLocatorData().getVelocityX() <= 0.2f && getLastLocatorData().getVelocityY() <= 0.2f)
                    currentAngle = randomGenerator.nextInt(359);
                randomDrive();
                if(!stoppingMapping) {
                    startStuckHandler();
                }
            }
        }, 3000);
    }

    private LocatorData getLastLocatorData() {
        return getLastCollisionLocationData().locatorData;
    }

    private CollisionLocatorData getLastCollisionLocationData() {
        return locationList.get(locationList.size() - 1);
    }

    private class CollisionLocatorData {
        public LocatorData locatorData;
        public boolean isCollision;

        private CollisionLocatorData(LocatorData locatorData, boolean collision) {
            this.locatorData = locatorData;
            this.isCollision = collision;
        }
    }
}
