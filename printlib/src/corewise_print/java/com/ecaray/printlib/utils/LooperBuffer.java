package com.ecaray.printlib.utils;

public interface LooperBuffer {
	void add(byte[] buffer);

	byte[] getFullPacket();
}