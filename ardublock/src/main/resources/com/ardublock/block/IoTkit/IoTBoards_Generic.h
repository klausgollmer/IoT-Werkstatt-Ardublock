#ifdef ESP8266
  #define IOTW_GPIO_ROTARY_A 12
  #define IOTW_GPIO_ROTARY_B 14
  #define IOTW_GPIO_ROTARY_BUTTON 2
  #define IOTW_GPIO_NEO 13
  #define IOTW_GPIO_NEOWING 15
  #define SDA 4
  #define SCL 5
  #define LMIC_NSS 2
  #define LMIC_DIO 15
  #define IOTW_GPIO_A0 A0
#endif

#ifdef ESP32
  #define IOTW_GPIO_ROTARY_A 19
  #define IOTW_GPIO_ROTARY_B 18
  #define IOTW_GPIO_ROTARY_BUTTON 16
  #define IOTW_GPIO_NEO 23
  #define IOTW_GPIO_NEOWING 5
  #define SDA 21
  #define SCL 22
  #define LMIC_NSS 16
  #define LMIC_DIO 5
  #define IOTW_GPIO_A0 36
  #define IOTW_TOUCH_PIN_UP T7 
  #define IOTW_TOUCH_PIN_DOWN T9 
  #define IOTW_TOUCH_PIN_BUTTON T5 
  #define IOTW_TOUCH_UP_THRESHOLD 12 
  #define IOTW_TOUCH_DOWN_THRESHOLD 12 
  #define IOTW_TOUCH_BUTTON_THRESHOLD 12 
  #define IOTW_TOUCH_DEBOUNCE_DELAY 100L
#endif


