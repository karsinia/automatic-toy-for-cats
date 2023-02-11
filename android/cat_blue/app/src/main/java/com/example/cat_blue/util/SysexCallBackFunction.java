package com.example.cat_blue.util;

public interface SysexCallBackFunction {
	void call(byte command, byte argc, byte[] argv);
}
