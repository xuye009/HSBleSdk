package com.handscape.sdk.inf;

import android.view.MotionEvent;
import java.util.HashMap;

import com.handscape.sdk.bean.BaseKeyBean;

//蓝牙触摸指令的接收、保存、管理
//按键映射的管理
public interface ICommondManager {

    //指令接收器
    void onCommondReceive(MotionEvent event);

    //获取未定义的按键映射
    HashMap<Integer, BaseKeyBean> getUnDefineMap();

    //添加未定义的按键
    void addUndefineMap(Integer id, BaseKeyBean point);

    //移除未定义的按键
    void removeUndefineKeyMap(Integer id);

    //清空未定义的按键
    void clearUndefineKeyMap();

    //获取已经定义的按键映射
    HashMap<Integer, BaseKeyBean> getKeyMap();

    //增加按键映射
    void addKeyMap(Integer id, BaseKeyBean point);

    //移除按键
    void removeKeyMap(Integer id);

    //清空按键映射
    void clearKeyMap();

    //是否在按键配置界面
    boolean isInConfigMode();
}
