//
// Created by dan34 on 2/25/2023.
// I rename this .ino and compile and upload with Arduino IDE
// I'm using an ESP32 Pico D4 official Espressif board it works great!
//

#include <Arduino.h>
#include <BluetoothSerial.h>
#include <FastLED.h>
#define DATA_PIN 23

#define MAX_LEDLEX 255  //max number of Ledlexerators

BluetoothSerial mySerial;  //(BT_GRAY_TX, BT_WHITE_RXD);  // RX, TX

#define LED_PIN 23
#define NUM_LEDS 255    // trying max leds
#define BRIGHTNESS 255  // max brightness
#define LED_TYPE WS2811
#define COLOR_ORDER RGB  // this works for the string I bought.
CRGB leds[NUM_LEDS];

#define UPDATES_PER_SECOND 30

class Ledxerlate {
public:
  byte pos;     //position of the led
  long millis;  //time to execute next step
  byte mode;    //mode of animation
  byte step;    //step of the animation
  byte alpha;
  byte red;
  byte green;
  byte blue;
  int duration;
};


class Modus {
private:
  int count = 0;  //leds with animations
  Ledxerlate lx[MAX_LEDLEX];
public:
  Modus() {
  }


  void tick(long millis) {
    bool hasChange = false;
    for (byte k = 0; k < count; k++) {
      //something to happen time has expired
      if (lx[k].millis < millis) {
        hasChange = true;
        Serial.print("Cnt ");
        Serial.print(count);
        Serial.print(" Pos ");
        Serial.print(lx[k].pos);
        Serial.print(" M ");
        Serial.print(millis);
        Serial.print(" Trg ");
        Serial.print(lx[k].millis);

        switch (lx[k].mode) {
          case 1:  //blink its where we start
          case 2:  //blink a bit faster
          case 3:  //blink a bit faster
          case 4:  //blink a bit faster
          case 5:  //yet another blink 3 fast then off for a second
            doBlink(k);
            break;
        }
      }
    }
    if (hasChange) {
      FastLED.show();
    }
  }

  void doBlink(byte cpos) {
    switch (lx[cpos].step) {
      case 0:  //turn on led
        leds[lx[cpos].pos] = CRGB(lx[cpos].red, lx[cpos].green, lx[cpos].blue);
        leds[lx[cpos].pos].fadeToBlackBy(lx[cpos].alpha);
        lx[cpos].step = 1;
        break;
      case 1:
        // turn off
        leds[lx[cpos].pos].fadeToBlackBy(255);
        lx[cpos].step = 0;
        break;
    }
    //mode 5 has varrying durations
    if (lx[cpos].mode == 5) {
      switch (lx[cpos].duration) {
        case 2500:
          lx[cpos].duration = 102;
          break;
        case 102:
          lx[cpos].duration = 101;
          break;
        case 101:
          lx[cpos].duration = 100;
          break;
        case 100:
          lx[cpos].duration = 99;
          break;
        case 99:
          lx[cpos].duration = 98;
          break;
        case 98:
          lx[cpos].duration = 2500;
          break;
      }
    }
    lx[cpos].millis += lx[cpos].duration;  //next time
    Serial.print(" next ");
    Serial.print(lx[cpos].millis);
    Serial.print(" step ");
    Serial.print(lx[cpos].step);
    Serial.print(" dur ");
    Serial.print(lx[cpos].duration);
    Serial.print(" mode ");
    Serial.println(lx[cpos].mode);
  }

  //for debugging
  void printit(int x) {
    Serial.print("Step=");
    Serial.print(x);
    Serial.print(" count=");
    Serial.println(count);
    for (int d = 0; d < count; d++) {
      Serial.print(" d=");
      Serial.print(d);
      Serial.print(" lxpos=");
      Serial.println(lx[d].pos);
    }
  }

  void
  startMode(long m, int pos, int mode, int alpha, int red, int green, int blue) {
    printit(1);
    endMode(pos);
    if (mode != 0) {
      if (count < MAX_LEDLEX) {
        lx[count].step = 0;    //always at start 0
        lx[count].millis = m;  //preserve starting point
        lx[count].pos = pos;
        lx[count].mode = mode;
        lx[count].alpha = alpha;
        lx[count].red = red;
        lx[count].green = green;
        lx[count].blue = blue;
        switch (mode) {
          case 1:
            lx[count].duration = 1000;  //assume we burn off 0 millies
            break;
          case 2:
            lx[count].duration = 750;  //assume we burn off 0 millies
            break;
          case 3:
            lx[count].duration = 500;  //assume we burn off 0 millies
            break;
          case 4:
            lx[count].duration = 250;  //assume we burn off 0 millies
            break;
          case 5:
            lx[count].duration = 2500;  //assume we burn off 0 millies
            break;
        }
        count++;
      }
    }
    printit(3);
  }
  // find the position of the _id in the array
  // if found and there is more in the array
  // move the end of the array to the found position
  void endMode(int pos) {
    //find the pos
    for (int k = 0; k < count; k++) {
      if (lx[k].pos == pos) {
        if (k < count - 1) {
          lx[k] = lx[count - 1];
        }
        //one less in the array
        count--;
        k = count;
      }
    }
    printit(2);
  }
};

Modus xm;



void setup() {

  mySerial.begin("Danny117HDWS2812B");
  //Turn this off when not needed...
  Serial.begin(57600);

  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection(TypicalLEDStrip);
  FastLED.setBrightness(BRIGHTNESS);

  //show a rainbow over all 256 lights 256 times how ever long that is
  for (int k = 0; k < 2; k++) {
    for (int j = 0; j < 255; j++) {
      for (int i = 0; i < NUM_LEDS; i++) {
        leds[i] = CHSV(i - (j * 8), BRIGHTNESS, BRIGHTNESS); /* The higher the value 4 the less fade there is and vice versa */
      }
      FastLED.show();
      delay(2); /* Change this to our hearts desire, the lower the value the faster your colors move (and vice versa) */
    }
  }
  //all lights off at start
  for (int k = 0; k < NUM_LEDS; k++) {
    leds[k] = CRGB(0, 0, 0);
  }
  FastLED.show();
}




void loop() {
  long m = millis();
  //may need to use these from previous loop
  static byte commandInPos = 0;
  static byte commandIn[16];

  //these are only used when processing
  byte t;
  int currentLight;
  bool onOff;
  int alpha;
  int red;
  int green;
  int blue;
  byte mode;  //modes coming soon.  Example welder candleflicker fade etc

  while (mySerial.available()) {
    byte t = mySerial.read();
    int i = t;
    commandIn[commandInPos] = t;
    commandInPos++;
    if (commandInPos == 16) {
      //validate command
      if (commandIn[0] == 232 && commandIn[1] == 34 || commandIn[2] == 182 && commandIn[3] == 0 && commandIn[4] == 0 || commandIn[5] == 0 && commandIn[6] <= NUM_LEDS && commandIn[13] == 232 && commandIn[14] == 34 && commandIn[15] == 182) {
        //command is valid extra values
        currentLight = commandIn[6];  // current light actually uses bytes 3, 4, 5, 6 but one byte gets 0 - 255
        onOff = commandIn[7] != 0;
        alpha = commandIn[8];
        red = commandIn[9];
        green = commandIn[10];
        blue = commandIn[11];
        mode = commandIn[12];
        leds[currentLight] = CRGB(red, green, blue);
        if (!onOff) {
          alpha = 255;
          mode = 0;
        }
        leds[currentLight].fadeToBlackBy(alpha);
        FastLED.show();
        //send back the 16 bytes so the controller knows that this has processed
        mySerial.write(commandIn, 16);
        xm.startMode(m, currentLight, mode, alpha, red, green, blue);
        //reset for next commanld
        commandInPos = 0;
      } else {
        //move the array up one byte and try to find something evetually it works
        //cpu consuming but should find something eventually
        commandInPos = 0;
        for (int k = 1; k < 16; k++) {
          commandIn[commandInPos] = commandIn[k];
          commandInPos++;
        }
      }
    }
  }

  //tick the Modus this does blinks and fades and such
  xm.tick(m);
}

