#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>

#define FIREBASE_HOST "aquaalert-1820-default-rtdb.firebaseio.com"  
#define FIREBASE_AUTH "DDAKsneJ87qUFABdDg0fLiPzd26yfwPHniSzCYWq"  

// defines pins numbers
const int trigPin = 2;  //D4
const int echoPin = 0;  //D3
const int LED = 4; //D2

// defines variables
long duration;
int distance = 0;
int wifi = 0;
int water = 0;
int water1 = 0;
int distanceSame = 1;
int lastDistance = -1;
int count1 = 0;
int count2 = 0;
int count3 = 0;
int led1 = 0;


//Firebase object..
FirebaseData firebaseData;
WiFiManager wifiManager;


void setup(){
  
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT); // Sets the echoPin as an Input
  pinMode(LED, OUTPUT);
  
  wifiManager.autoConnect("Aqua","123456789");
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
}
void loop(){
  
Firebase.getInt(firebaseData,"/LED");
if(firebaseData.intData() == 1)
  digitalWrite(LED, HIGH);
else
  digitalWrite(LED,LOW);


if(WiFi.status() != WL_CONNECTED){
  wifi = 0;
  Firebase.setInt(firebaseData, "/Connectivity", wifi);
  wifiManager.autoConnect("Aqua","123456789");
}
else{
  wifi = 1;
  Firebase.setInt(firebaseData, "/Connectivity", wifi);
}
    
  
// Clears the trigPin
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);

// Sets the trigPin on HIGH state for 10 micro seconds
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);

// Reads the echoPin, returns the sound wave travel time in microseconds
    duration = pulseIn(echoPin, HIGH);

// Calculating the distance
Firebase.setInt(firebaseData, "/Distance", distance);
distance= duration*0.034/2;

if(distance == lastDistance) {
    // Increment counter
    distanceSame++;
} else {
    distanceSame = 1;
}
// prepare lastDistance for next cycle
lastDistance = distance;


water1 = water;
switch(distance){
case 1:
case 2:
case 3:water = 500;
       break;
case 4: 
case 5:
case 6: water = 400;
        break;
case 7:
case 8:water = 300;
       break;
case 9:
case 10:
case 11:water = 200;
        break;
case 12:
case 13:water = 100;
        break;
case 14:
case 15:water = 0;
        break;
default : water = water1;
          break;
}
Firebase.getInt(firebaseData, "/Total");
  int count4 = firebaseData.intData();
if(distanceSame >= 4){
   Firebase.setInt(firebaseData, "/WaterLevel", water);
    if(water>=count1){
      count1=water;
      }
    else{
      count2=water;
      count3=count1-count2;
      count4=count4+count3;
      Firebase.setInt(firebaseData, "/WaterDrank", count3);
      Firebase.setInt(firebaseData,"/Total",count4);
      count1=count2;
      //LED OFF
      Firebase.setInt(firebaseData,"/LED",0);
     }
}
}
