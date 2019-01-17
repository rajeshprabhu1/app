package com.ouam.app.commonInterfaces;


public interface InterfaceApiResponseCallBack {

        void onRequestSuccess(Object obj);
        void onRequestFailure(Throwable r);

}
