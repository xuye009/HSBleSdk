package com.handscape.sdk.touch;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import com.handscape.sdk.util.HSMotionBuilder;
import com.handscape.sdk.util.TouchMapKeyUtils;
import com.handscape.sdk.util.PacketData;
import com.handscape.sdk.inf.ICommondManager;
import com.handscape.sdk.inf.IHSTouchCmdReceive;

/**
 * 触摸指令调度器
 */
public class HSTouchDispatch implements Runnable {


    private HandlerThread mDispatchThread;

    private final int MAX_COMMAND_COUNT = HSTouchCommand.MAX_TOUCH_COMMAND_COUNT;
    private Handler mDispathcHandler,mUiHandler;
    private ArrayBlockingQueue<HSTouchCommand> mTouchCommandQueue = new ArrayBlockingQueue<>(100);
    private HSTouchCommand[] mCommands = new HSTouchCommand[this.MAX_COMMAND_COUNT];


    private ICommondManager commondManager;

    public void setTouchServer(ICommondManager commondManager) {
        this.commondManager = commondManager;
    }

    private IHSTouchCmdReceive receive;
    private int screenWidth, screenHeight;

    public void setReceive(IHSTouchCmdReceive receive) {
        this.receive = receive;
    }

    public HSTouchDispatch(IHSTouchCmdReceive receive) {
        this.receive=receive;
        mDispatchThread = new HandlerThread(HSTouchDispatch.class.getName());
        mDispatchThread.start();
        mDispathcHandler = new Handler(mDispatchThread.getLooper());
        screenWidth = TouchMapKeyUtils.getScreenSize()[0];
        screenHeight = TouchMapKeyUtils.getScreenSize()[1];
    }


    @Override
    public void run() {
        if(receive!=null){
            try {
                while (mTouchCommandQueue.size()>0){
                    HSTouchCommand command=mTouchCommandQueue.take();
                    String cmd=makeTouchEventString(command);
                    if(receive!=null){
                        receive.onCmdReceive(cmd);
                    }
                    sendTouchCommandData(cmd);
                    mCommands[command.getId()]=null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加触摸指令
     * @param command
     */
    public void addCmd(final HSTouchCommand command) {
        try {
            Log.v("xuyeAction ",command.getAction()+"");
            mDispathcHandler.post(new Runnable() {
                @Override
                public void run() {
                    String cmd=makeTouchEventString(command);
                    if(receive!=null){
                        receive.onCmdReceive(cmd);
                    }
                    try {
                        sendTouchCommandData(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
//            mTouchCommandQueue.put(command);
//            mCommands[command.getId()] = command;
//            mDispathcHandler.post(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTouchCommandData(String command) throws IOException {
        //讲坐标转化为屏幕上的触控信息
        Log.v("xuyeCmd",command);
        int rotation = TouchMapKeyUtils.getScreenRotation();
        final MotionEvent event = new HSMotionBuilder(rotation, screenWidth, screenHeight, commondManager).build(new PacketData("0|sendevent " + command));
        if (event == null) {
            return;
        }
//        if (mServer != null) {
//            mUIHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mServer.dispatchTouchEvent(event);
//                }
//            });
//        }
        if (commondManager != null) {
            commondManager.onCommondReceive(event);
        }
    }

    /*
     * 生成触摸指令]"
     * */
    private String makeTouchEventString(HSTouchCommand command) {
        Log.v("xuyeAction",command.getAction()+"");
        String result = "touch " +command.getAction()
                + " " + getTouchCount() + " " + command.getId()
                + " " + touchedCmd2String(command);
        return result;
    }


    private String touchedCmd2String() {
        StringBuilder builder = new StringBuilder();
        boolean firstCmd = true;
        for (int i = 0; i < this.MAX_COMMAND_COUNT; i++) {
            if (this.mCommands[i] != null) {
                if (firstCmd) {
                    firstCmd = false;
                } else {
                    builder.append(" ");
                }
                builder.append(this.mCommands[i].getStream());
            }
        }
        return builder.toString();
    }


    private String touchedCmd2String(HSTouchCommand command) {
        return new StringBuilder(command.getStream()).toString();
    }


    private int getTouchCount() {
        int count = 0;
        for (int i = 0; i < MAX_COMMAND_COUNT; i++) {
            if (this.mCommands[i] != null) {
                count++;
            }
        }
        return count;
    }



}
