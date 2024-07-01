#ifdef ESP8266
  #define GPIO_ROTARY_A 12
  #define GPIO_ROTARY_B 14
  #define GPIO_ROTARY_BUTTON 2
  #define GPIO_NEO 13
  #define GPIO_NEOWING 15
  #define GPIO_I2C_SDA 4
  #define GPIO_I2C_SCL 5
  #define LMIC_NSS 2
  #define LMIC_DIO 15
  #define PIN_AIN0 A0
#endif

#ifdef ESP32
  #define GPIO_ROTARY_A 19
  #define GPIO_ROTARY_B 18
  #define GPIO_ROTARY_BUTTON 16
  #define GPIO_NEO 23
  #define GPIO_NEOWING 5
  #define GPIO_I2C_SDA 21
  #define GPIO_I2C_SCL 22
  #define LMIC_NSS 16
  #define LMIC_DIO 5
  #define PIN_AIN0 36
  #define TOUCH_PIN_UP T7 
  #define TOUCH_PIN_DOWN T9 
  #define TOUCH_PIN_BUTTON T5 
  #define TOUCH_UP_THRESHOLD 12 
  #define TOUCH_DOWN_THRESHOLD 12 
  #define TOUCH_BUTTON_THRESHOLD 12 
  #define TOUCH_DEBOUNCE_DELAY 100L
#endif


