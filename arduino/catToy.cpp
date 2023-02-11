#if 1

#include <Arduino.h>
#include <Firmata.h>
#include <avr/wdt.h>
#include <Servo.h>
#include "util/SuperLoop.h"

#define SERVOPIN	8

Servo servo;

class ServoMove: public SuperLoop {
public:
	int angle = 0;
	int moveType = 0;
	void job() override {
		switch (moveType) {
		case 0:
			if (angle == 0) {
				servo.write(0);
				angle = 180;
			} else {
				servo.write(180);
				angle = 0;
			}
			break;
		case 1:
			servo.write(random(0, 180));
			break;
		default:
			break;
		}
	}
};

ServoMove servoMove;

void setup() {
	Firmata.begin(115200);
	servo.attach(SERVOPIN);
	servoMove.begin(500);

	Firmata.attach(DIGITAL_MESSAGE, [](uint8_t pin, int value) {
		switch (pin) {
		case 0:
			servoMove.moveType = 0;
			servoMove.angle = 0;
			if (servoMove.isStop())
				servoMove.start();
			break;
		case 1:
			servoMove.moveType = 1;
			if (servoMove.isStop())
				servoMove.start();
			break;
		case 2:
			servoMove.stop();
			servoMove.angle = servo.read();
			if (servoMove.angle > 0) {
				servoMove.angle -= 10;
				servo.write(servoMove.angle);
			}
			break;
		case 3:
			servoMove.stop();
			servoMove.angle = servo.read();
			if (servoMove.angle < 180) {
				servoMove.angle += 10;
				servo.write(servoMove.angle);
			}
			break;
		default:
			break;
		}
	});

	Firmata.attach(SET_PIN_MODE, [](uint8_t pin, int tf) {
		if (tf)
			servoMove.start();
		else
			servoMove.stop();
	});

	Firmata.attach(SYSTEM_RESET, []() {
		wdt_enable(WDTO_120MS);
	});
}

void loop() {
	while (Firmata.available())
		Firmata.processInput();
	servoMove.loop();
}

#endif
