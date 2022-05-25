package com.argentum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Util {

	public static short leShort(short n) {
		return (short) (((n & 0xff) << 8) | (((n & 0xff00) >> 8) & 0xff));
	}

	public static int leInt(int n) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putInt(n);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		return buf.getInt(0);
	}
	
	public static int leFloat(float n) {

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putFloat(n);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        return buf.getInt(0);
	}

}