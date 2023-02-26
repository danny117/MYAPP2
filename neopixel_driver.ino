#include <SoftwareSerial.h>
#include <FastLED.h>

const int BT_GREEN_ENABLE = 8;
const int BT_GRAY_TX = 9;
const int BT_WHITE_RXD = 10;
const int BT_BLACK_BLINK = 11;

SoftwareSerial mySerial(BT_GRAY_TX, BT_WHITE_RXD); // RX, TX

#define LED_PIN     6
#define NUM_LEDS    33
#define BRIGHTNESS  255
#define LED_TYPE    WS2811
#define COLOR_ORDER RGB
CRGB leds[NUM_LEDS];

int rev[256];

#define UPDATES_PER_SECOND 60


void setup() {
  //Serial.begin(9600);
  mySerial.begin(9600);

  //output for fastled
  pinMode(BT_GREEN_ENABLE, OUTPUT);
  digitalWrite(BT_GREEN_ENABLE, HIGH);

  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(  BRIGHTNESS );

  //all lights off dim at start
  for (int k = 0; k < NUM_LEDS; k++) {
    leds[k] = CRGB( 0, 0, 0);
  }
  //one light on dim green at start
  leds[0] = CRGB(0, 5, 0);
  FastLED.show();


  //reverse int todo in app
  int j = 256;
  for (int k = 0; k < 256 ; k++ ) {
    j --;
    rev[k] = j;
  }
}



void loop() {
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
  byte modes;   //modes coming soon.  Example welder candleflicker fade etc

  while (mySerial.available()) {
    byte t = mySerial.read();
    int i = t;
    Serial.println(i);
    commandIn[commandInPos] = t;
    commandInPos++;
    if (commandInPos == 16) {
      //validate command
      if (commandIn[0] == 232 && commandIn[1] == 34 || commandIn[2] == 182
          && commandIn[3] == 0 && commandIn[4] == 0 || commandIn[5] == 0
          && commandIn[6] <=  NUM_LEDS
          && commandIn[13] == 232 && commandIn[14] == 34 && commandIn[15] == 182) {
        //command is valid extra values
        currentLight = commandIn[6];   // current light actually uses bytes 3, 4, 5, 6 but one byte gets 0 - 255
        onOff = commandIn[7] != 0;
        alpha = commandIn[8];
        alpha = rev[alpha];
        red = commandIn[9];
        green = commandIn[10];
        blue = commandIn[11];
        modes = commandIn[12];
        leds[currentLight] = CRGB( red, green, blue);
        if (!onOff) {
          alpha = 255;
        }
        leds[currentLight].fadeToBlackBy(alpha);
        FastLED.show();
        //send back the 16 bytes so the controller knows that this has processed
        mySerial.write(commandIn, 16);
        //reset for next commanld
        commandInPos = 0;
      } else {
        //move the array up one byte and try to find something evetually it works
        //cpu consuming but should find something eventually
        commandInPos = 0;
        for (int k = 1; k < 16; k ++) {
          commandIn[commandInPos] = commandIn[k];
          commandInPos ++;
        }
      }
    }
  }
}


byte funReverse(byte B) {
  byte ret = 0;

  return ret;
}
