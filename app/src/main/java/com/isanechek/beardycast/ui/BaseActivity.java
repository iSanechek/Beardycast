//package com.isanechek.beardycast.ui;
//
//import android.content.*;
//import android.os.IBinder;
//import android.support.v7.app.AppCompatActivity;
//import android.telephony.TelephonyManager;
//import com.isanechek.beardycast.data.Model;
//import com.isanechek.beardycast.ui.podcast.service.PlayService;
//import com.isanechek.beardycast.utils.HardwareIntentReceiver;
//
///**
// * Created by isanechek on 14.09.16.
// */
//public class BaseActivity extends AppCompatActivity {
//
//    private PlayService playService;
//    private ServiceConnection serviceConnection;
//    private boolean isPlayServiceBound = false;
//    private HardwareIntentReceiver hardwareIntentReceiver;
//    private Intent playIntent;
//
//
//    public void initService(Model model) {
//        serviceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                PlayService.PlayServiceBinder binder = (PlayService.PlayServiceBinder) service;
//                playService = binder.getService();
//                isPlayServiceBound = true;
//
//                IntentFilter headsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
//                IntentFilter callStateFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//                hardwareIntentReceiver = new HardwareIntentReceiver(playService, model);
//                registerReceiver(hardwareIntentReceiver, headsetFilter);
//                registerReceiver(hardwareIntentReceiver, callStateFilter);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                unregisterReceiver(hardwareIntentReceiver);
//                isPlayServiceBound = false;
//            }
//        };
//
//        if (!isPlayServiceBound) {
//            if (playIntent == null) {
//                playIntent = new Intent(this, PlayService.class);
//                this.isPlayServiceBound = bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//                this.startService(playIntent);
//            }
//        }
//    }
//
//    @Override
//    public void initservice(Model model) {
//
//    }
//}
