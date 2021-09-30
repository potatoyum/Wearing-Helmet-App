#define BT_RX 10 //블루투스
#define BT_TX 11 //블루투스
#define led 13 //led
#define TRIG 2
#define ECHO 3
#define Relay A1
#define sol A2 //솔레노이드
#include <SoftwareSerial.h>

int isOpen, h_running;
SoftwareSerial HM10(BT_RX, BT_TX);

void setup() {
  pinMode(Relay, OUTPUT);
  digitalWrite(Relay, HIGH);
  
  Serial.begin(9600);
  HM10.begin(9600);

  pinMode(led, OUTPUT);
  
  pinMode(sol, OUTPUT);
  
  digitalWrite(led, LOW);
  digitalWrite(sol, LOW);

  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  isOpen = 0;
  h_running = 0;
}

int isHelmetIn(){
  long duration, distance;
  
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);

  duration = pulseIn (ECHO, HIGH);
  distance = duration * 17 / 1000; 

  //Serial.println(distance);

  return (distance >= 7)? 0 : 1;
}

void lockOn(){
  digitalWrite(sol, LOW);
  isOpen = 0;
}

void lockOff(){
  digitalWrite(sol, HIGH);
  isOpen = 1;
}

void UVPhase(){
  //소독 UV-LED 켜기
  delay(2000);
  digitalWrite(led, HIGH);
  delay(1000);
  digitalWrite(led, LOW);
  delay(500);
  digitalWrite(Relay, LOW);
}

void loop() {
  if (HM10.available()) {
    char h = HM10.read();

    if (h == '^') {
      //HM10.write("get\n");
      lockOff();
      h_running++;
    }
  }

//  Serial.print("running ");
//  Serial.print(h_running);
//  Serial.print(" isOpen ");
//  Serial.print(isOpen);
//  Serial.print(" helmetIn ");
//  Serial.println(isHelmetIn());
  
  if(h_running == 1 && isOpen == 1 && isHelmetIn() == 0){
    lockOn();
  }
  if(h_running == 2 && isOpen == 1 && isHelmetIn() == 1){
    lockOff();
    h_running = 0;
    HM10.write("end");
    UVPhase();
  }
}
