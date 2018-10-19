package com.handscape.sdk.touch;


public class HSTouchCommand {    //class tf
   public static int MAX_TOUCH_COMMAND_COUNT = 32;
   public static int CUSTOM_TOUCH_START_INDEX = 5;
   public static boolean[] commandEvents = new boolean[MAX_TOUCH_COMMAND_COUNT];

   private final long eventTime;
   private int mPointerId;
   private int action;
   private int mPx;
   private int mPy;

   public static int setNewTouchDown() {
      synchronized (commandEvents) {
         for(int tchId = CUSTOM_TOUCH_START_INDEX; tchId< MAX_TOUCH_COMMAND_COUNT; tchId++) {
            if (!commandEvents[tchId]) {
               commandEvents[tchId] = true;
               return tchId;
            }
         }
         return 0;
      }
   }

   public static void setTouchUp(int tchId) {
      synchronized (commandEvents) {
         commandEvents[tchId] = false;
      }
   }

   public static void reset() {
      synchronized (commandEvents) {
         for (int index = CUSTOM_TOUCH_START_INDEX; index < MAX_TOUCH_COMMAND_COUNT; index++) {
            commandEvents[index] = false;
         }
      }
   }

   public static boolean isInvalidTouch(int touchId) {
      return touchId > MAX_TOUCH_COMMAND_COUNT || touchId < CUSTOM_TOUCH_START_INDEX;
   }

   public static HSTouchCommand makeCommand(int pointerId, int action, int pX, int pY, long time) {
      return new HSTouchCommand(pointerId, action, pX, pY, time);
   }

   public static HSTouchCommand newCommand(int pointerId, int action, int px, int py) {
      return new HSTouchCommand(pointerId, action, px, py, HSClock.get().now());
   }

   HSTouchCommand(int pointerId, int  action, int px, int py, long time) {
      this.mPointerId = pointerId;
      this.action = action;
      this.eventTime = time;
      this.mPx = px;
      this.mPy = py;
   }

   public int getId() {
      return this.mPointerId;
   }

   public int getX() {
      return this.mPx;
   }

   public int getY() {
      return this.mPy;
   }

   public void releaseTouch() {
      setTouchUp(this.mPointerId);
   }

   public String getStream() {
      return this.mPointerId + " " + this.mPx + " " + this.mPy;
   }

   public int getAction() { return action; }
   public void setPos(int px, int py) {
      this.mPx = px;
      this.mPy = py;
   }

   public boolean isValidPos() {
      return (this.mPx == -1 || this.mPy == -1) ? false : true;
   }

   public boolean isAfterNow() {
      return nowTime() < this.eventTime;
   }

   protected long nowTime() {
      return HSClock.get().now();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
         return false;
      }
      HSTouchCommand command = (HSTouchCommand) obj;
      if (this.mPointerId != command.mPointerId) {
         return false;
      }
      if (this.action != command.action) {
         return false;
      }
      if (this.mPx != command.mPx) {
         return false;
      }
      if (this.mPy != command.mPy) {
         return false;
      }
      if (this.eventTime != command.eventTime) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "TouchCommand(" + this.mPointerId + ", " + this.action + ", " + this.mPx + ", " + this.mPy + ", " + this.eventTime + ")";
   }
}
