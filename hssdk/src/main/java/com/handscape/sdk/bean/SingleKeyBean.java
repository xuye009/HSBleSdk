package com.handscape.sdk.bean;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import com.handscape.sdk.util.TouchMapKeyUtils;

//单键配置
public class SingleKeyBean extends BaseKeyBean implements Parcelable {

    //映射到的中心坐标点
    private PointF point = null;

    public SingleKeyBean() {
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public PointF getPoint() {
        return point;
    }

    /**
     *
     * @param index：第几个模拟按钮，在模拟多键时游泳
     * @param keyCode：按键类别值
     * @param isInConfigMode：是否在按键配置页面
     * @param undefineMap：按键未定义状态
     * @return
     */
    @Override
    public PointF map(int index, int keyCode, boolean isInConfigMode, boolean undefineMap) {
        PointF pointF = new PointF();
        if (getPoint() != null) {
            //在按键未定义状态
            if (undefineMap) {
                PointF point = getPoint();
                pointF.x= point.x;
                pointF.y = point.y;
            } else {
                //单键或者当前在配置模式
                if (keyType == BaseKeyBean.Type_SingleKey || isInConfigMode) {
                    PointF point = getPoint();
                    if (point != null) {
                        pointF.x = point.x;
                        pointF.y = point.y;
                    }
                } else if (keyType == BaseKeyBean.Type_Joystick || keyType == BaseKeyBean.Type_DragKey) {
                    //摇杆或者拖放类型，以设置的摇杆中心为原点，对所设置的摇杆区域进行转化
                    PointF point = getPoint();
                    if (point != null) {
                        float centX = point.x;
                        float centY = point.y;
                        PointF centrePoint = TouchMapKeyUtils.getCentrePoint(keyCode);
                        float spaceX = centrePoint.x - centX;
                        float spaceY = centrePoint.y - centY;
                        pointF.x = pointF.x - spaceX;
                        pointF.y = pointF.y - spaceY;
                    }
                }
            }
        }
        return pointF;
    }

    protected SingleKeyBean(Parcel in) {
        keyType = in.readInt();
        keysize = in.readInt();
        keyalpha = in.readInt();
        keyvisibi = in.readByte() != 0;
        point = in.readParcelable(PointF.class.getClassLoader());
    }

    public static final Creator<SingleKeyBean> CREATOR = new Creator<SingleKeyBean>() {
        @Override
        public SingleKeyBean createFromParcel(Parcel in) {
            return new SingleKeyBean(in);
        }

        @Override
        public SingleKeyBean[] newArray(int size) {
            return new SingleKeyBean[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(keyType);
        dest.writeInt(keysize);
        dest.writeInt(keyalpha);
        dest.writeByte((byte) (keyvisibi ? 1 : 0));
        dest.writeParcelable(point, flags);
    }
}
