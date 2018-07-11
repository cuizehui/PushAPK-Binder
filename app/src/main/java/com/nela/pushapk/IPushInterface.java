package com.nela.pushapk;

import android.os.IInterface;

public interface IPushInterface extends IInterface {

    public void sendTokenAndPackageName();
}
