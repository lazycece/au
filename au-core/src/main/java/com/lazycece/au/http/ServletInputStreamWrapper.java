package com.lazycece.au.http;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author lazycece
 */
public class ServletInputStreamWrapper extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    public ServletInputStreamWrapper(byte[] data) {
        this.byteArrayInputStream = new ByteArrayInputStream(data);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }

    @Override
    public int read() throws IOException {
        return this.byteArrayInputStream.read();
    }
}
