package com.nela.pushapk;

import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BinderManager {
    public static final String TAG = "BinderManager";
    private static BinderManager sInstance;
    public IBinder mRemoteBinder;
    private String mToken = null;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==1){
                tryBind();
            }
            super.handleMessage(msg);
        }
    };

    private BinderManager() {
    }

    public static BinderManager getInstance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            sInstance = new BinderManager();
            return sInstance;
        }
    }

    public void start() {
        tryBind();
    }

    public void setMiPushToken(String token) {
        mToken = token;
        registeBinderAndMiPushToken();
    }

    private void tryBind() {
        if (!initRemoteBinder()) {
            startTimer();
        } else {
            setupDeathWatch();
            registeBinderAndMiPushToken();
        }
    }

    private void startTimer() {
        mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    private void registeBinderAndMiPushToken() {
        if (mRemoteBinder == null || TextUtils.isEmpty(mToken)) {
            Log.d(TAG, "mRemoteBinder or mToken is null");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", mToken);
            jsonObject.put("packName", JCApplication.sContext.getPackageName());
            jsonObject.put("through", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String pushInfo = jsonObject.toString();
        Log.d(TAG, "token " + jsonObject.toString());
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        LocalBinder localBinder = new LocalBinder();
        _data.writeStrongBinder(localBinder.asBinder());
        _data.writeString(pushInfo);
        _data.writeInterfaceToken("Juphoon.AndroidPush");
        boolean result = false;
        try {
            result = mRemoteBinder.transact(1, _data, _reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        _reply.readException();
        _reply.readInt();
        Log.d(TAG, "sendTokenAndLocalBinder: " + result);
    }

    public void sendMiPushMessage() {
        if (mRemoteBinder == null) {
            Log.d(TAG, "mRemoteBinder or mToken is null");
            return;
        }
        Log.d(TAG, "sendMiPushMessage");
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        _data.writeInterfaceToken("Juphoon.AndroidPush");
        boolean result = false;
        try {
            result = mRemoteBinder.transact(2, _data, _reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        _reply.readException();
        _reply.readInt();
    }

    private void setupDeathWatch() {
        try {
            mRemoteBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    mRemoteBinder.unlinkToDeath(this, 0);
                    mRemoteBinder = null;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tryBind();
                        }
                    });
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean initRemoteBinder() {
        try {
            Class serviceManager = Class.forName("android.os.ServiceManager");
            Method method = serviceManager.getMethod("getService", String.class);
            mRemoteBinder = (IBinder) method.invoke(serviceManager.newInstance(), "Juphoon.Quantum");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "initRemoteBinder");
        if (mRemoteBinder != null) {
            return true;
        } else {
            return false;
        }
    }

    private class LocalBinder extends Binder implements IPushInterface {

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface("Juphoon.AndroidPush");
                    sendTokenAndPackageName();
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void sendTokenAndPackageName() {
            registeBinderAndMiPushToken();
        }

        @Override
        public IBinder asBinder() {
            return this;
        }
    }
}
