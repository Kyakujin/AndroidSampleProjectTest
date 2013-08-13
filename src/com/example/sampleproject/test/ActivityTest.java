package com.example.sampleproject.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.widget.Button;
import android.widget.TextView;

import com.example.sampleproject.MainActivity;
import com.example.sampleproject.R;
import com.example.sampleproject.SecondActivity;

public class ActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Activity mFirstActivity;
    private Activity mSecondActivity;
    private Instrumentation mInstrumentation;
    private Button mButton;

    public ActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mFirstActivity = getActivity();
        mInstrumentation = getInstrumentation();
        // Widgetを取得
        mButton = (Button) mFirstActivity.findViewById(R.id.buttonNext);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(mFirstActivity != null) {
            mFirstActivity.finish();
            mFirstActivity = null;
        }
        if(mSecondActivity != null) {
            mSecondActivity.finish();
            mSecondActivity = null;
        }
    }

    public void test00PreConditions() {
        assertNotNull(mFirstActivity);
        assertNotNull(mButton);
        assertNull(mSecondActivity);
    }

    public void test01DispSecondActivity() {
        // 遷移後のActivityを検知する
        ActivityMonitor monitor = new ActivityMonitor(
                SecondActivity.class.getCanonicalName(), null, false);
        mInstrumentation.addMonitor(monitor);
        
      // NEXTボタンをクリック
        mFirstActivity.runOnUiThread(new Runnable() {             
            @Override
            public void run() {
                mButton.performClick();
            }
        });
        
        //UIスレッドが終了するまで待つ
        mInstrumentation.waitForIdleSync();
        
        // SecondActivityの起動を待つ
        mSecondActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 2000);
        assertEquals(monitor.getHits(),1);
        
        // テキストビューの表示確認
        TextView  secondText = (TextView) mSecondActivity.findViewById(R.id.textViewSecond);
        assertNotNull(secondText);
        ViewAsserts.assertOnScreen(secondText.getRootView(), secondText);
        assertEquals("Second Activity", secondText.getText());
    }

}
