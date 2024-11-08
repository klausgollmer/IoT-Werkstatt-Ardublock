# IoT-Werkstatt for ESP8266 (Octopus) and ESP32 (Makey:Lab)

## Contents

  - [Geting Startet](#start)
  - [Documentation](#documentation)
  - [External Resources](#External Resources)

### Start
[Getting Started](https://www.umwelt-campus.de/iot-werkstatt/tutorials/schnellstart-octopus-anschliessen-und-einrichten)

### Documentation

You can use the [IoT-Werkstatt Website](https://www.umwelt-campus.de/forschung/projekte/iot-werkstatt) to get all information about this project.

### External Resources

**The IoT<sup>2</sup>Werkstatt (Internet of Things and Thinking Workshop) provides an Ardublock-based tool for education and training **.
In addition to our IoT<sup>2</sup>\ extension, the ZIP file with the complete programming environment - ready to use - also contains a collection of various external libraries and tools, which are listed below. 



**Arduino 1.8.19 portable** is an open source project, supported by many.

The Arduino team is composed of Massimo Banzi, David Cuartielles, Tom Igoe and David A. Mellis.

Arduino uses [GNU avr-gcc toolchain](https://gcc.gnu.org/wiki/avr-gcc), [GCC ARM Embedded toolchain](https://launchpad.net/gcc-arm-embedded), [avr-libc](https://www.nongnu.org/avr-libc/), [avrdude](https://www.nongnu.org/avrdude/), [bossac](http://www.shumatech.com/web/products/bossa), [openOCD](http://openocd.org/) and code from [Processing](https://www.processing.org) and [Wiring](http://wiring.org.co).

**Ardublock**: Copyright (C) 2011 David Li and He Qichen

[ArduBlock](http://blog.ardublock.com/) is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

Originally used (2016) in the version [from LetsgoING](https://letsgoing.org/) (HS Reutlingen)

**Java: Eclipse Temurin™ Latest Releases**

<https://www.eclipse.org/legal/epl-2.0/>

<https://adoptium.net/de/>

**Esp8266 Package** <https://github.com/esp8266/Arduino>

**ESP32 Package** <https://github.com/espressif/arduino-esp32>

The respective licence conditions of the authors apply to all libraries. The following table contains corresponding references.

| Interface-<br><br>component | Detail | Library | Licenze | Name of Authors |
| --- | --- | --- | --- | --- |
| Umweltdaten<br><br>Bosch BME | Bosch BME280<br><br>Bosch BME680<br><br>Adafruit_Unified_Sensor<br><br>BSEC2 und BME68x | Sparkfun BME280 V1.0.0<br><br><https://github.com/sparkfun/SparkFun_BME280_Arduino_Library><br><br><https://github.com/adafruit/Adafruit_BME680><br><br><https://github.com/adafruit/Adafruit_Sensor><br><br><https://github.com/boschsensortec/Bosch-BSEC2-Library><br><br><https://github.com/BoschSensortec/Bosch-BME68x-Library> |     | Marshall Taylor @ SparkFun Electronics<br><br>Limor Fried/Ladyada<br><br>Bosch Sensortec |
| Beschleunigung Bosch BMO055<br><br>Grove MMA7660 | <https://www.adafruit.com/product/2472><br><br><http://wiki.seeed.cc/Grove-3-Axis_Digital_Accelerometer-1.5g/> | <https://github.com/adafruit/Adafruit_BNO055><br><br>[github.com/Seeed-Studio /Accelerometer_MMA7660](https://github.com/Seeed-Studio/Accelerometer_MMA7660) |     | Kevin (KTOWN)<br><br>Frankie Chu, MIT |
| RGB-LED | Neopixel<br><br>WS2801 | <https://github.com/adafruit/Adafruit_NeoPixel><br><br><https://github.com/adafruit/Adafruit-WS2801-Library> |     | Phil Burgess. PJRC, Michael Miller<br><br>Limor Fried/Ladyada |
| NFC/RFID | <http://wiki.seeed.cc/Grove-NFC/> | <https://github.com/Seeed-Studio/PN532><br><br><https://github.com/Seeed-Studio/Grove-NFC-libraries-Part> |     | @Don, @JiapengLi, @awieser<br><br>Kevin Townsend<br><br>Don Coleman |
| Control App | Blynk | <https://github.com/blynkkk/blynk-library/releases/latest> |     | Volodymyr Shymanskyy |
| Gassensor<br><br>Multichannel | <http://wiki.seeed.cc/Grove-Multichannel_Gas_Sensor/> | [https://github.com/Seeed-Studio/ Mutichannel_Gas_Sensor](https://github.com/Seeed-Studio/%20Mutichannel_Gas_Sensor) |     | **Grove system**<br><br>**Jacky Zhang**<br><br>**GNU** |
| Dreh/Drück<br><br>Rotary-Encoder | <https://www.pjrc.com/teensy/td_libs_Encoder.html> | Encoder by Paul Stoffregen V1.4.1<br><br><https://github.com/PaulStoffregen/Encoder> |     | Paul Stoffregen<br><br>(für ESP8266) |
|     | <https://www.arduino.cc/reference/en/libraries/esp32encoder/> | <https://github.com/madhephaestus/ESP32Encoder/> |     | Kevin Harrington<br><br>(für ESP32) |
| LED-Matrix<br><br>Charlieplex | [learn.adafruit.com/adafruit-15x7-7x15-charlieplex-led-matrix-charliewing-featherwing/](http://learn.adafruit.com/adafruit-15x7-7x15-charlieplex-led-matrix-charliewing-featherwing/) | <https://github.com/adafruit/Adafruit_IS31FL3731> |     |     |
| Textausgabe | Adafruit GFX V1.1.5<br><br><https://github.com/adafruit/Adafruit-GFX-Library> |     |     |
| MQTT | <https://pubsubclient.knolleary.net/index.html> | PubSubClient<br><br>[github.com/knolleary/pubsubclient/releases/tag/v2.6](https://github.com/knolleary/pubsubclient/releases/tag/v2.6) |     | Nick O'Leary<br><br>MIT |
| GSM | Mobilfunk | TinyGSM, 0.1.7,<br><br>[tiny.cc/tiny-g n sm-readme](http://tiny.cc/tiny-gsm-readme)<br><br>StreamDebugger |     | Volodymyr Shymanskyy |
| OTA | On Air Update | [github.com/esp8266/Arduino/tree/master/libraries/ArduinoOTA](file:///C:\Users\k.gollmer\AppData\Local\Temp\github.com\esp8266\Arduino\tree\master\libraries\ArduinoOTA) |     |     |
| LoRaWAN | FeatherWing<br><br>(IBM-LMIC) | <https://github.com/mcci-catena/arduino-lmic> |     | Thomas Telkamp<br><br>Matthijs Kooijman<br><br>MCCI Corporation<br><br>(verändert für ESP8266)<br><br>MIT |
| Digital I/O<br><br>Extender | <https://learn.sparkfun.com/tutorials/sx1509-io-expander-breakout-hookup-guide> | <https://github.com/sparkfun/SparkFun_SX1509_Arduino_Library> |     | Jim Lindblom |
| I2C-ADC<br><br>Extender | <http://wiki.seeed.cc/Grove-I2C_ADC/> |     |     |     |
| Motoren | FeatherWing Adafruit Motor-Shield<br><br>[http:\\www.adafruit.com/products/1438](http://www.adafruit.com/products/1438) | <https://github.com/adafruit/Adafruit_Motor_Shield_V2_Library> |     | BSD |
| 7-Segment | [FeatherWing Adaf](https://learn.adafruit.com/adafruit-7-segment-led-featherwings/overview)ruit | <https://github.com/adafruit/Adafruit_LED_Backpack> |     |     |
| LCD Grove | [Seeedstudio LCD](https://wiki.seeedstudio.com/Grove-16x2_LCD_Series/) | <https://github.com/Seeed-Studio/Grove_LCD_RGB_Backlight/archive/master.zip> |     | Loovee |
| RGB, Gesten,<br><br>Lichtfarbe | <https://www.adafruit.com/product/3595> | <https://github.com/adafruit/Adafruit_APDS9960> |     |     |
| CO2 | <https://www.sparkfun.com/categories/tags/scd30> | <https://github.com/sparkfun/SparkFun_SCD30_Arduino_Library><br><br><https://github.com/sparkfun/SparkFun_SCD4x_Arduino_Library> |     | Nathan Seidle SparkFun Electronics<br><br>MIT |
| CO2 | MH-Z19 | <https://github.com/WifWaf/MH-Z19> |     | Jonathan Dempsey <JDWifWaf@gmail.com> |
| Feinstaub | <http://wiki.seeedstudio.com/Grove-Laser_PM2.5_Sensor-HM3301/> | <https://github.com/Seeed-Studio/Seeed_PM2_5_sensor_HM3301> |     |     |
| Feinstaub | Honeywell HPMA115S0 | <https://www.dfrobot.com/wiki/index.php/PM2.5_laser_dust_sensor_SKU:SEN0177> |     | Zuyang @ HUST |
| Blynk | Control App | Blynk<br><br><http://www.blynk.cc> |     | Volodymyr Shymanskyy |
| LIDAR |     | <http://en.benewake.com/product/detail/5c345e26e5b3a844c472329c.html> |     |     |
| Touch-Sensor | <https://learn.adafruit.com/adafruit-mpr121-12-key-capacitive-touch-sensor-breakout-tutorial> | <https://github.com/adafruit/Adafruit_MPR121> |     | Fried/Ladyada |
| InfluxDB |     | <https://github.com/tobiasschuerg/InfluxDB-Client-for-Arduino> |     | [Tobias Schürg (tobiasschuerg)](https://github.com/tobiasschuerg) |
| PaxCounter<br><br>(WiFi-Sniffer) | <https://www.youtube.com/watch?v=fmhjtzmLrg8> | <https://github.com/SensorsIot/Wi-Fi-Sniffer-as-a-Human-detector> |     | Andreas Spiess,<br><br>Ray Burnette |
| Regression | CurveFit<br><br>Polynomfit | <https://github.com/Rotario/arduinoCurveFitting> |     | Rowan Easter-Robinson Rotario<br><br>[rotarioner@gmail.com](mailto:rotarioner@gmail.com) |
| EdgeImpulse |     |     |     | Martin Seidinger, Nicolas Kiebel, Robin Barton, UCB |
| CO2WebServer |     |     |     | Jason Rietzke, UCB |
| Hoymiles Balkonkraft |     | <https://github.com/hm-soft/Hoymiles-DTU-Simulation> |     | Hm-soft GNU V3 |
| Person Sensor | Usefull Sensors<br><br><https://www.sparkfun.com/products/21231> | <https://github.com/usefulsensors/person_sensor_docs/blob/main/README.md><br><br><https://github.com/usefulsensors/person_sensor_arduino> |     | [pete@usefulsensors.com](mailto:pete@usefulsensors.com)<br><br>Apache 2.0 |
| ToF Abstand<br><br>VL53L0X | <https://www.pololu.com/product/2490> | <https://github.com/pololu/vl53l0x-arduino/blob/master/LICENSE.txt> |     | Kevin (pololu) |
| Adafruit OLED<br><br>Display | <https://www.adafruit.com/product/4650> | <https://github.com/adafruit/Adafruit_SH110x><br><br><https://github.com/adafruit/Adafruit-GFX-Library> |     | Ladyada, adafruit<br><br>BSD |
| Adafruit GPS<br><br>Position | <https://www.adafruit.com/product/4415> | <https://github.com/adafruit/Adafruit_GPS> |     | Written by Limor Fried/Ladyada for Adafruit Industries. BSD license |
| Adafruit RGB Sensor TCS34725 | <https://www.adafruit.com/product/1334> | <https://github.com/adafruit/Adafruit_TCS34725> |     | Adafruit, BSD |
|     |     |     |     |     |
| Stromsensor<br><br>INA219 | <https://www.adafruit.com/product/904> | <https://github.com/adafruit/Adafruit_INA219> |     | Written by Ktown for Adafruit Industries. BSD license |
| ChatGPT<br><br>ArduinoJson |     | <https://github.com/0015/ChatGPT_Client_For_Arduino><br><br><https://github.com/bblanchon/ArduinoJson> |     | Author: Eric Nam<br><br>MIT<br><br>Author [Benoît Blanchon](https://github.com/bblanchon) , MIT |
| Software seriell | für ESP 32 | <https://github.com/plerup/espsoftwareserial> |     | Author: Dirk Kaar, Peter Lerup |
| Licht | <https://www.instructables.com/BH1750-Digital-Light-Sensor/><br><br><https://www.adafruit.com/product/439> | <https://github.com/claws/BH1750><br><br><https://github.com/adafruit/Adafruit_TSL2561> |     | Claws, MIT<br><br>Kevin (KTOWN) Townsend |
|     |     |     |     |     |

**USB Driver:**

CP210x: <https://www.silabs.com/developers/usb-to-uart-bridge-vcp-drivers>

CH340: <https://www.wch-ic.com/downloads/CH341SER_ZIP.html>
