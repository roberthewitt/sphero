package com.orbotix.sample.locator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.orbotix.sample.locator.mapping.CollisionLocatorData;
import com.orbotix.sample.locator.mapping.LocationViewer;
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
    int xt=100;
    int xsp=0;
    int yt=100;
    int ysp=0;
    int deadTime=30;

    private float currentAngle = 0;

    private Random randomGenerator;
    private List<CollisionLocatorData> locationList = new ArrayList<CollisionLocatorData>();
    private LocationViewer locationViewer;

    private LocatorListener mLocatorListener = new LocatorListener() {
        @Override
        public void onLocatorChanged(LocatorData locatorData) {
            if (locatorData != null) {
                Log.d(TAG, locatorData.toString());
                locationList.add(new CollisionLocatorData(locatorData, false));
                locationViewer.showFor(locationList);
            }
        }
    };
    private Handler handler;
    private boolean stoppingMapping = false;
    private KotikanColors kotikanColors;

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
        locationViewer = (LocationViewer) findViewById(R.id.sphero_map_view);

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
                kotikanColors = new KotikanColors();
                kotikanColors.setNextColor(mRobot);
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
            kotikanColors.setNextColor(mRobot);
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
        startStuckHandler();
        stoppingMapping = false;
        randomDrive();
    }

    public void stopMapping() {
        mRobot.stop();
        stoppingMapping = true;
    }

    private void randomDrive() {
        currentAngle += (110 + randomGenerator.nextInt(50));
        if(currentAngle >= 360) currentAngle -= 360;
        mRobot.drive(currentAngle, 0.6f);
    }

    private void startStuckHandler() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (Math.abs(getLastLocatorData().getVelocityX()) <= 1.0f && Math.abs(getLastLocatorData().getVelocityY()) <= 1.0f) {
                    randomDrive();
                }
                if (!stoppingMapping) {
                    startStuckHandler();
                }
            }
        }, 1000);
    }

    private LocatorData getLastLocatorData() {
        return getLastCollisionLocationData().locatorData;
    }

    private CollisionLocatorData getLastCollisionLocationData() {
        return locationList.get(locationList.size() - 1);
    }

}
