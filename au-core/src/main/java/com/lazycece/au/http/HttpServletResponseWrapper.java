package com.lazycece.au.http;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * wrapper http servlet response for multiple reading
 *
 * @author lazycece
 */
public class HttpServletResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {

    private HttpServletResponse response;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private ServletOutputStream servletOutputStream;
    private PrintWriter printWriter;

    public HttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (this.servletOutputStream == null) {
            this.servletOutputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }

                @Override
                public void write(int b) {
                    byteArrayOutputStream.write(b);
                }
            };
        }
        return this.servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (this.printWriter == null) {
            this.printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8), true);
        }
        return this.printWriter;
    }

    public byte[] getContent() {
        return this.byteArrayOutputStream.toByteArray();
    }

    public void setContent(byte[] content) throws IOException {
        this.byteArrayOutputStream.reset();
        this.byteArrayOutputStream.write(content);
    }

}