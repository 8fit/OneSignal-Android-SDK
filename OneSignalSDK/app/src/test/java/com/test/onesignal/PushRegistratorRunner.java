/**
 * Modified MIT License
 *
 * Copyright 2015 OneSignal
 *
 * Portions Copyright 2013 Google Inc.
 * This file includes portions from the Google GcmClient demo project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 1. The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * 2. All copies of substantial portions of the Software may only be used in connection
 * with services provided by OneSignal.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.test.onesignal;

import android.app.Activity;

import com.onesignal.BuildConfig;
import com.onesignal.PushRegistrator;
import com.onesignal.PushRegistratorGPS;
import com.onesignal.ShadowGoogleCloudMessaging;
import com.onesignal.ShadowGooglePlayServicesUtil;
import com.onesignal.example.BlankActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@Config(packageName = "com.onesignal.example",
      constants = BuildConfig.class,
      shadows = { ShadowGooglePlayServicesUtil.class, ShadowGoogleCloudMessaging.class },
      sdk = 21)
@RunWith(CustomRobolectricTestRunner.class)
public class PushRegistratorRunner {

   private Activity blankActiviy;
   private static boolean callbackFired;

   @BeforeClass // Runs only once, before any tests
   public static void setUpClass() throws Exception {
      ShadowLog.stream = System.out;
   }

   @Before // Before each test
   public void beforeEachTest() throws Exception {
      blankActiviy = Robolectric.buildActivity(BlankActivity.class).create().get();
      callbackFired = false;
      ShadowGoogleCloudMessaging.exists = true;
   }

   @Test
   public void testGooglePlayServicesAPKMissingOnDevice() throws Exception {
      PushRegistratorGPS pushReg = new PushRegistratorGPS();
      final Thread testThread = Thread.currentThread();

      pushReg.registerForPush(blankActiviy, "", new PushRegistrator.RegisteredHandler() {
         @Override
         public void complete(String id) {
            System.out.println("HERE: " + id);
            callbackFired = true;
            testThread.interrupt();
         }
      });
      try {Thread.sleep(5000);} catch (Throwable t) {}

      Assert.assertTrue(callbackFired);
   }

   @Test
   public void testGCMPartOfGooglePlayServicesMissing() throws Exception {
      PushRegistratorGPS pushReg = new PushRegistratorGPS();
      ShadowGoogleCloudMessaging.exists = false;

      final Thread testThread = Thread.currentThread();

      pushReg.registerForPush(blankActiviy, "", new PushRegistrator.RegisteredHandler() {
         @Override
         public void complete(String id) {
            System.out.println("HERE: " + id);
            callbackFired = true;
            testThread.interrupt();
         }
      });
      try {Thread.sleep(5000);} catch (Throwable t) {}

      Assert.assertTrue(callbackFired);
   }
}