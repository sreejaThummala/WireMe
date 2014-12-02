package com.wireme.samba;


import java.io.IOException;
import java.io.InputStream;

public class InputStreamManaged extends InputStream{

	private InputStream __ism = null;

	public InputStreamManaged(InputStream is) {
		this.__ism = is;
	}

	private long __length = -1;
	private int __bread = 0;
	private ChangePercentageListener __pclistener = null;

	public void setLength(long l) {
		this.__length = l;
	}

	public long getLength() {
		return this.__length;
	}

	public int getBytesRead() {
		return this.__bread;
	}

	public void setOnPercentageChangedListener(
			ChangePercentageListener __pclistener) {
		this.__pclistener = __pclistener;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		int ret = this.__ism.read(buffer);
		if (ret == -1 || this.__pclistener == null || this.__length == 0)
			return ret;
		// Calculate previous percentage
		long pperc = (this.__bread * 100) / this.__length;
		this.__bread += ret;
		long nperc = (this.__bread * 100L) / this.__length;
		if (pperc != nperc)
			this.__pclistener.onChanged((int) nperc);
		return ret;
	}

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		this.__ism.read(b);
		return b[0];
	}

}
