package com.michael.testcenter;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.michael.testcenter.service.TestJobService;

public class MainActivity extends AppCompatActivity {

    TextView mWakeLockMsg;
    ComponentName mServiceComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        mWakeLockMsg = (TextView) findViewById(R.id.wakelock_txt);
        mServiceComponent = new ComponentName(this, TestJobService.class);
        Intent startServiceIntent = new Intent(this, TestJobService.class);
        startService(startServiceIntent);

        Button theButtonThatWakelocks = (Button) findViewById(R.id.wakelock_poll);
        theButtonThatWakelocks.setText("theButtonThatWakelocks");

        theButtonThatWakelocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollServer();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    /**
     * This method polls the server via the JobScheduler API. By scheduling the job with this API,
     * your app can be confident it will execute, but without the need for a wake lock. Rather, the
     * API will take your network jobs and execute them in batch to best take advantage of the
     * initial network connection cost.
     * <p>
     * The JobScheduler API works through a background service. In this sample, we have
     * a simple service in MyJobService to get you started. The job is scheduled here in
     * the activity, but the job itself is executed in MyJobService in the startJob() method. For
     * example, to poll your server, you would create the network connection, send your GET
     * request, and then process the response all in MyJobService. This allows the JobScheduler API
     * to invoke your logic without needed to restart your activity.
     * <p>
     * For brevity in the sample, we are scheduling the same job several times in quick succession,
     * but again, try to consider similar tasks occurring over time in your application that can
     * afford to wait and may benefit from batching.
     */
    public void pollServer() {
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (int i = 0; i < 10; i++) {
            JobInfo jobInfo = new JobInfo.Builder(i, mServiceComponent)
                    .setMinimumLatency(5000) // 5 seconds
                    .setOverrideDeadline(60000) // 60 seconds (for brevity in the sample)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // WiFi or data connections
                    .build();

            mWakeLockMsg.append("Scheduling job " + i + "!\n");
            scheduler.schedule(jobInfo);
        }
    }


}
