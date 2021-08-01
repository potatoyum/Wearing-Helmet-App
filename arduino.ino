#include <SoftwareSerial.h>
#define BT_RX 3 //블루투스
#define BT_TX 2 //블루투스
#define SWW 8 //버튼
#define led 9 //led
#include <Servo.h> 
 
int servoPin = 6; //서보모터

Servo servo; 

int angle = 0;
int temp=0;
SoftwareSerial HM10(BT_RX, BT_TX);

void setup() {
  Serial.begin(9600);
  HM10.begin(9600);
  servo.attach(servoPin);
  pinMode(led, OUTPUT); 
  pinMode(SWW, INPUT);
  temp=0;
  Serial.write(HM10.available());

}

void loop() {
  int btn = digitalRead(SWW);

  if(HM10.available()){
    char h = HM10.read();
    Serial.write(h);
    if(h=='^'){
      Serial.write("received");
      HM10.write("received");
      servo.write(0); //신호 받으면 열기
      temp=0;
    }

  }

  if (btn==1 && temp==0){ //잠기는 것
    Serial.write(btn);
    servo.write(90);
    HM10.write("turn");

    //소독 UV-LED 켜기
    delay(2000);
    digitalWrite(led, HIGH);
    delay(1000);
    digitalWrite(led, LOW);
    
  }
  

  

/*
  if(Serial.available()){
    HM10.write(Serial.read());
  }
*/
}
