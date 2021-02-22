/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2018 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.stepper;

import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;

@Kroll.module(name = "TiStepper", id = "ti.stepper")
public class TiStepperModule
	extends KrollModule implements SensorEventListener, ActivityCompat.OnRequestPermissionsResultCallback
{

	// Standard Debugging variables
	private static final String LCAT = "TiStepperModule";
	private static final boolean DBG = TiConfig.LOGD;

	private SensorManager sensorManager;
	private Sensor senAccelerometer;
	private Sensor senStepCounter;
	private Sensor senStepDetector;
	private int stepsTaken = 0;
	private int reportedSteps = 0;
	private int stepDetector = 0;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public TiStepperModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is created
	}

	// Methods
	@Kroll.method
	public void create()
	{
		sensorManager = (SensorManager) TiApplication.getAppRootOrCurrentActivity().getSystemService(SENSOR_SERVICE);

		// Reference/Assign the sensors

		senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		senStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		senStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

		// Register the sensors for event callback

		registerSensors();
	}

	@Kroll.method
	public void registerSensors()
	{
		if (!isAvailable()) {
			Log.e(LCAT, "No sensors available");
			return;
		}

		Activity activity = TiApplication.getAppRootOrCurrentActivity();
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACTIVITY_RECOGNITION)
				== PackageManager.PERMISSION_DENIED) {
			Log.i(LCAT,"permission request");
			ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACTIVITY_RECOGNITION }, 1);
		} else {
			sensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, senStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Kroll.method
	public void unregisterSensors()
	{
		if (!isAvailable()) {
			return;
		}

		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));
		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));
		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
	}

	public static boolean isAvailable()
	{
		PackageManager pm = TiApplication.getAppRootOrCurrentActivity().getPackageManager();
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
			&& pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
			&& pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		Sensor sensor = event.sensor;
		switch (event.sensor.getType()) {
			case Sensor.TYPE_STEP_COUNTER:

				if (reportedSteps < 1) {
					reportedSteps = (int) event.values[0];
				}

				stepsTaken = (int) event.values[0] - reportedSteps;

				KrollDict k = new KrollDict();
				k.put("type", "stepCounter");
				k.put("count", stepsTaken);
				fireEvent("steps", k);

				break;

			case Sensor.TYPE_STEP_DETECTOR:

				stepDetector++;

				KrollDict ks = new KrollDict();
				ks.put("type", "stepDetector");
				ks.put("count", stepDetector);
				fireEvent("steps", ks);

				break;
				/*
			case  Sensor.TYPE_ACCELEROMETER:


				String x = String.format("%.02f", event.values[0]);
				String y = String.format("%.02f", event.values[1]);
				String z = String.format("%.02f", event.values[2]);

				// Output the string to the GUI

				KrollDict ka = new KrollDict();
				ka.put("x", x);
				ka.put("y", y);
				ka.put("z", z);
				fireEvent("acceleration", ka);

				break;*/
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if (requestCode == 1) {
			registerSensors();
		}
	}
}
