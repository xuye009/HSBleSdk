package com.handscape.sdk.util;

/* compiled from: PacketData */
public class PacketData {    //class ta
    private String mPacketType;  // [0] of Content -  packet type string. value only "sendevent"
    private String mEventType;   // [1] of Content - event type string. value only "touch"
    private String[] mPacketContents;

    public PacketData() {
        init();
    }

    public PacketData(String data) {
        parsePacket(data);
    }

    private void init() {
        this.mPacketType = "UNKOWN".toLowerCase();
        this.mEventType = "UNKOWN".toLowerCase();
        this.mPacketContents = null;
    }

    public String getPacketContent(int index) {
        if (index >= this.mPacketContents.length) {
            return "";
        }
        return this.mPacketContents[index];
    }

    public int getIntPacketContent(int index) {
        return Integer.parseInt(getPacketContent(index));
    }

    public void parsePacket(String data) {
        init();
        String[] split = data.split("\\|");
        String strContent = data;
        if (split.length > 1) {
            strContent = split[1];
        }
        this.mPacketContents = strContent.split(" ");
        if (this.mPacketContents.length > 1) {
            this.mEventType = this.mPacketContents[1].toLowerCase();
        }
        if (this.mPacketContents.length > 0 && this.mPacketContents[0] != "") {
            this.mPacketType = this.mPacketContents[0].toLowerCase();
        }
    }
}
