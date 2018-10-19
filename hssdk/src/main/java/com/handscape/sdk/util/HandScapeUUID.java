/**
 *  Copyright (C) HandScape Inc. 2014. All Rights Reserved.
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of HandScape Inc.  The intellectual and technical concepts contained
 *  herein are proprietary to HandScape Inc. and may be covered by U.S. and Foreign Patents,
 *  patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from HandScape Inc.
 *
 *  This software is provided to the customer for evaluation
 *  purposes only and, as such early feedback on performance and operation
 *  is anticipated. The software source code is subject to change and
 *  not intended for production. Use of developmental release software is
 *  at the user's own risk. This software is provided "as is," and HandScape Inc.
 *  cautions users to determine for themselves the suitability of using the
 *  beta release version of this software. HandScape Inc. makes no warranty or
 *  representation whatsoever of merchantability or fitness of the product
 *  for any particular purpose or use. In no event shall HandScape Inc. be liable for
 *  any consequential, incidental or special damages whatsoever arising out
 *  of the use of or inability to use this software, even if the user has
 *  advised HandScape Inc. of the possibility of such damages.
 *
 *  Refer to HandScapeInc-Licence.txt for details
 *
 *  @author Michael Leahy
 */
package com.handscape.sdk.util;

import java.util.UUID;

/**
 * HandScapeUUID
 */
public final class HandScapeUUID
{
   // This is the reverse of s_TOUCH_SERVICE, i.e., A63D6FD4-A8C5-5692-994A-E0361E728EE3,
   // for example, -90=0xA6, 61=0x3D, 111=0x6F, ...
   public static final byte s_TOUCH_SERVICE_BYTEARRAY[] =
    new byte[] { -90, 61, 111, -44, -88, -59, 86, -110, -103, 74, -32, 54, 30, 114, -114, -29 };

   public static final byte s_HOU_SERVICE_BYTEARRAY[] =
    new byte[] { -63, -91, 0, 91, 2, 0, 35, -101, -31, 17, 2, -47, 0, 28, 0, 0 };

   public static final UUID s_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

   // Service
   public static final UUID s_TOUCH_SERVICE = UUID.fromString("E38E721E-36E0-4A99-9256-C5A8D46F3DA6");

   public static final UUID s_HOU_SERVICE = UUID.fromString("00001C00-D102-11E1-9B23-000EFB0000A5");


   public static final UUID s_CJZC_SERVICE = UUID.fromString("53AA21CC-37EF-4D08-9AEA-DCB366FDCDBD");

   // Characteristics

   public static final UUID s_TOUCH_COMMAND = UUID.fromString("53AA21CC-37EF-4D08-9AEA-DCB366FDCDBD");

   // Description: Returns the Tactonic device information in JSON format as:
   // {“rows”:32,“cols”:32,“row-sp”:6500,“col-sp”:6500,“serial”:24117514}
   public static final UUID s_TOUCH_DEVICE_INFO = UUID.fromString("CEA645E4-D85E-4363-8CF6-E5D12FB0AE77");

   // Description: Touch notification will be sent as a notification from the peripheral to the master (smartphone)
   // in the native Tactonic packed format, which is below.

   /**
    * typedef enum
    * {
    *    TACTONIC_TOUCH_STATE_START = 0,
    *    TACTONIC_TOUCH_STATE_UPDATE = 1,
    *    TACTONIC_TOUCH_STATE_END = 2,
    * } TactonicTouchState_t;
    *
    *
    * typedef struct __attribute__((__packed__))
    * {
    *    uint16_t touchID;   // little endian
    *    uint16_t x; // big endian
    *    uint16_t y; // big endian
    *    uint16_t force; // not used
    *    uint8_t state;
    *    uint8_t notused;
    * } AtmelTouch_T;
    *
    *  The id of a touch remains constant as long as the touch remains on the device.
    *  The X and Y location range from 0 to 0xfff (That's three Fs!)
    *  The force of the touch (not currently used).
    *  The touch state (Start = 0, Update = 1, End = 2) are sent.
    */
   public static final UUID s_TOUCH_DATA = UUID.fromString("A71EB84E-2AB5-4C00-8581-8FE418B60856");

   /**
    * typedef struct __attribute__((__packed__))
    * {
    *      uint8_t state_id; // Bits 0-3 = Touch ID, Bits 4-7 = State
    *      uint8_t xposmsb; // X position MSByte
    *      uint8_t yposmsb; // Y position MSByte
    *      uint8_t xyposlsb; // Bits 0-3 = Y position lsbits, Bits 4-7 = X position lsbits
    *      uint8_t area; // Size of touch
    *      uint8_t vector; // Gives an indication of the direction of the touch and a confidence level
    *      uint8_t amplitude; // Touch amplitude (sum of measured deltas)
    * } TouchMultiData_t;
    */
   public static final UUID s_TOUCH_DATA_MULTIPLE = UUID.fromString("617CD8C5-1714-4134-9351-D4C88766C537");

   /**
    * typedef struct __attribute__((__packed__))
    * {
    *      uint8_t xyposmsb; // Bits 0-3 = Y position msbits, Bits 4-7 = X position msbits
    *      uint8_t xposlsb; // X position LSByte
    *      uint8_t yposlsb; // Y position LSByte
    * } TouchMultiData_t;
    */
   public static final UUID s_HOU_TOUCH_DATA_MULTIPLE = UUID.fromString("00001C0F-D102-11E1-9B23-000EFB0000A5");

   public static final UUID s_Battery_Service=UUID.fromString("0000180f-0000-1000-8000-00805F9B34FB");

   public static final UUID s_Battery_Level=UUID.fromString("00002a19-0000-1000-8000-00805F9B34FB");

   private HandScapeUUID() {}
}
