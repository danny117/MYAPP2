//
// Created by dan34 on 2/25/2023.
// I rename this .ino and compile and upload with Arduino IDE
// I'm using an ESP32 Pico D4 official Espressif board it works great!
//
// just added mode 6 marquee to blink
// added mode 7 8 marquee with adjustment

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
  int adj1;
  int adj2;
  int durzero;
  int durnext;
};


class Modus {
private:
  int count = 0;  //leds with animations
  Ledxerlate lx[MAX_LEDLEX];
  //int mql6;  // changes based on lights in marquee
  //int mqs6;  // time the light is off
  //int mql7;  // changes based on lights in marquee
  //int mqs7;  // time the light is off
public:
  Modus() {
  }

  void tick(long millis) {
    bool hasChange = false;
    for (byte k = 0; k < count; k++) {
      //something to happen time has expired
      if (lx[k].millis < millis) {
        hasChange = true;
        switch (lx[k].mode) {
          case 1:  //blink its where we start
          case 2:  //blink a bit faster
          case 3:  //blink a bit faster
          case 4:  //blink a bit faster
          case 5:  //yet another blink 3 fast then off for a second
          case 6:  //marquee yet another blink
          case 7:  //marquee yet another blink
          case 8:  //marquee yet another blink
            doBlink(k, millis);
            break;
        }
      }
    }
    if (hasChange) {
      FastLED.show();
    }
  }

  void doBlink(byte cpos, long millis) {
    int dur;
    Serial.print(" Cnt ");
    Serial.print(count);
    Serial.print(" Pos ");
    Serial.print(lx[cpos].pos);
    Serial.print(" M ");
    Serial.print(millis);
    Serial.print(" Trg ");
    Serial.print(lx[cpos].millis);
    switch (lx[cpos].step) {
      case 0:  //turn on led
        leds[lx[cpos].pos] = CRGB(lx[cpos].red, lx[cpos].green, lx[cpos].blue);
        leds[lx[cpos].pos].fadeToBlackBy(lx[cpos].alpha);
        lx[cpos].step = 1;
        dur = lx[cpos].durzero;
        break;
      case 1:
        // turn off
        leds[lx[cpos].pos].fadeToBlackBy(255);
        lx[cpos].step = 0;
        dur = lx[cpos].durnext;
        break;
    }

    //mode 5 has varrying durations
    if (lx[cpos].mode == 5) {
      switch (lx[cpos].durzero) {
        case 2500:
          lx[cpos].durzero = 102;
          break;
        case 102:
          lx[cpos].durzero = 101;
          break;
        case 101:
          lx[cpos].durzero = 100;
          break;
        case 100:
          lx[cpos].durzero = 99;
          break;
        case 99:
          lx[cpos].durzero = 98;
          break;
        case 98:
          lx[cpos].durzero = 2500;
          break;
      }
      dur = lx[cpos].durzero;
    }
    lx[cpos].millis += dur;
    Serial.print(" next ");
    Serial.print(lx[cpos].millis);
    Serial.print(" step ");
    Serial.print(lx[cpos].step);
    Serial.print(" dur ");
    Serial.print(lx[cpos].durzero);
    Serial.print(" next ");
    Serial.print(lx[cpos].durnext);
    Serial.print(" mode ");
    Serial.println(lx[cpos].mode);
  }

  //for debugging
  void
  printit(int x) {
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

  void startMode(long m, int pos, int mode, int alpha, int red, int green, int blue, int adj1, int adj2) {
    printit(1);
    endMode(pos, m);
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
            lx[count].durzero = 1000;  //assume we burn off 0 millies
            lx[count].durnext = 1000;  //assume we burn off 0 millies
            break;
          case 2:
            lx[count].durzero = 750;  //assume we burn off 0 millies
            lx[count].durnext = 750;  //assume we burn off 0 millies
            break;
          case 3:
            lx[count].durzero = 500;  //assume we burn off 0 millies
            lx[count].durnext = 500;  //assume we burn off 0 millies
            break;
          case 4:
            lx[count].durzero = 250;  //assume we burn off 0 millies
            lx[count].durnext = 250;  //assume we burn off 0 millies
            break;
          case 5:
            lx[count].durzero = 2500;  //assume we burn off 0 millies
            lx[count].durnext = 2500;  //assume we burn off 0 millies
            break;
        }
        count++;
        switch (mode) {
          case 6:  //1 of lights on
          case 7:  //1 of lights off
          case 8:  //adjustable lights
            startMarquee(m, pos, mode, alpha, red, green, blue, adj1, adj2);
            break;
        }
      }
    }
    printit(3);
  }

  void startMarquee(long m, int pos, int mode, int alpha, int red, int green, int blue, int adj1, int adj2) {


    //sort the marquee entries
    int c6 = 0;
    for (int k = 0; k < count; k++) {
      if (lx[k].mode == mode) {
        c6 += 1;
        for (int j = k + 1; j < count + 1; j++) {
          if (lx[j].mode == mode) {
            if (mode == 6 && lx[j].pos > lx[k].pos) {
              Ledxerlate lxtemp = lx[k];
              lx[k] = lx[j];
              lx[j] = lxtemp;
            }
            if (mode == 7 && lx[j].pos < lx[k].pos) {
              Ledxerlate lxtemp = lx[k];
              lx[k] = lx[j];
              lx[j] = lxtemp;
            }
            if (mode == 8 && lx[j].pos < lx[k].pos) {
              Ledxerlate lxtemp = lx[k];
              lx[k] = lx[j];
              lx[j] = lxtemp;
            }
          }
        }
      }
    }
    //calculate delays
    //todo on changes all marquees have to be rebuilt.
    int mqs = 750;
    int mql = 750;
    if (c6 > 1) {
      if (mode == 6) {
        mql = 1500 / c6;
        mqs = 1500 - mql;
      }
      if (mode == 7) {
        mql = 1500 - mql;
        mqs = 1500 / c6;
      }
    }
    int mqz = mqs;

    //adustable with alpha.
    if (mode == 8) {
      int mTime = 2000 * adj2 / 255;
      mqs = mTime * (255 - adj1) / 255;
      mql = mTime * adj1 / 255;
      mqz = mqs;
    }

    //bool hasMarquee = false;
    long mx = m;
    for (int k = 0; k < count + 1; k++) {
      if (lx[k].mode == mode) {
        mx += mqz;
        //one color for mode 6
        lx[k].red = red;
        lx[k].blue = blue;
        lx[k].green = green;
        lx[k].alpha = alpha;
        lx[k].durzero = mqs;
        lx[k].durnext = mql;
        lx[k].millis = mx;
        lx[k].step = 0;
      }
    }
  }

  // find the position of the _id in the array
  // if found and there is more in the array
  // move the end of the array to the found position
  void endMode(int pos, long m) {
    //find the pos
    int hasMarquee = 0;
    for (int k = 0; k < count; k++) {
      if (lx[k].pos == pos) {
        if (lx[k].mode >= 6 && lx[k].mode <= 8) {
          hasMarquee = lx[k].mode;
        }
        if (k < count - 1) {
          lx[k] = lx[count - 1];
        }
        //one less in the array
        count--;
        k = count;
      }
    }
    //rebase the marquee
    if (hasMarquee != 0) {
      for (int k = 0; k < count; k++) {
        if (lx[k].mode == hasMarquee) {
          startMarquee(m, lx[k].pos, lx[k].mode, lx[k].alpha, lx[k].red, lx[k].green, lx[k].blue, lx[k].adj1, lx[k].adj2);
          k = count;
        }
      }
    }
    printit(2);
  }
};

Modus xm;

void setup() {

  mySerial.begin("Danny117HDWS2812B");
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
  static byte commandIn[18];

  //these are only used when processing
  byte t;
  int currentLight;
  bool onOff;
  int alpha;
  int red;
  int green;
  int blue;
  int adj1;
  int adj2;
  byte mode;  //modes coming soon.  Example welder candleflicker fade etc

  while (mySerial.available()) {
    byte t = mySerial.read();
    int i = t;
    commandIn[commandInPos] = t;
    commandInPos++;
    if (commandInPos == 18) {
      //validate command
      if (commandIn[0] == 232 && commandIn[1] == 34 || commandIn[2] == 182 && commandIn[3] == 0 && commandIn[4] == 0 || commandIn[5] == 0 && commandIn[6] <= NUM_LEDS && commandIn[15] == 232 && commandIn[16] == 34 && commandIn[17] == 182) {
        //command is valid extra values
        currentLight = commandIn[6];  // current light actually uses bytes 3, 4, 5, 6 but one byte gets 0 - 255
        onOff = commandIn[7] != 0;
        alpha = commandIn[8];
        red = commandIn[9];
        green = commandIn[10];
        blue = commandIn[11];
        mode = commandIn[12];
        adj1 = commandIn[13];
        adj2 = commandIn[14];
        leds[currentLight] = CRGB(red, green, blue);
        if (!onOff) {
          alpha = 255;
          mode = 0;
        }
        leds[currentLight].fadeToBlackBy(alpha);
        FastLED.show();
        //send back the 16 bytes so the controller knows that this has processed
        mySerial.write(commandIn, 18);
        xm.startMode(m, currentLight, mode, alpha, red, green, blue, adj1, adj2);
        //reset for next commanld
        commandInPos = 0;
      } else {
        //move the array up one byte and try to find something evetually it works
        //cpu consuming but should find something eventually
        commandInPos = 0;
        for (int k = 1; k < 18; k++) {
          commandIn[commandInPos] = commandIn[k];
          commandInPos++;
        }
      }
    }
  }

  //tick the Modus this does blinks and fades and such
  xm.tick(m);
}
