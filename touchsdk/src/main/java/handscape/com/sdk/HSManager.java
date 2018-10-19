package handscape.com.sdk;

import android.content.Context;

/**
 * 触摸SDK总入口
 */
public class HSManager {

    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }

    private static HSBleManager hsBleManager;


    public static void init(Context context){
        mContext=context;
        hsBleManager=new HSBleManager(context);
    }






}
