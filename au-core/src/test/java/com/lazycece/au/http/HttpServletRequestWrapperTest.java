package com.lazycece.au.http;

import com.lazycece.au.context.RequestContext;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author lazycece
 * @date 2019/11/27
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpServletRequestWrapperTest {

    @Mock
    private HttpServletRequest request;

    @Before
    public void setup() {
        RequestContext.getCurrentContext().setRequest(request);
    }

    @After
    public void completion() {
        RequestContext.getCurrentContext().unset();
    }

    @Test
    public void testGet() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn("username=lazycece&password=000000");

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);

        assertThat(requestWrapper.getParameter("username")).isEqualTo("lazycece");
        assertThat(requestWrapper.getParameter("password")).isEqualTo("000000");
        assertThat(requestWrapper.getParameters().containsKey("username")).isTrue();
        assertThat(requestWrapper.getParameters().containsKey("password")).isTrue();
    }

    @Test
    public void testPostForm() throws IOException {
        String body = "username=lazycece&password=000000";
        when(request.getMethod()).thenReturn("POST");
        when(request.getInputStream()).thenReturn(new ServletInputStreamWrapper(body.getBytes(StandardCharsets.UTF_8)));
        when(request.getContentLength()).thenReturn(body.length());
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded;charset=utf-8");
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);

        assertThat(requestWrapper.getParameter("username")).isEqualTo("lazycece");
        assertThat(requestWrapper.getParameter("password")).isEqualTo("000000");
        assertThat(requestWrapper.getParameters().containsKey("username")).isTrue();
        assertThat(requestWrapper.getParameters().containsKey("password")).isTrue();
    }

    @Test
    public void testPostBody() throws IOException {
        String body = "username=lazycece&password=000000";
        when(request.getMethod()).thenReturn("POST");
        when(request.getInputStream()).thenReturn(new ServletInputStreamWrapper(body.getBytes(StandardCharsets.UTF_8)));
        when(request.getContentLength()).thenReturn(body.length());
        when(request.getContentType()).thenReturn("application/json;charset=utf-8");

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);

        assertThat(new String(requestWrapper.getContent(), StandardCharsets.UTF_8)).isEqualTo(body);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(requestWrapper.getInputStream(), bos);
        assertThat(new String(bos.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(body);

        bos.reset();
        IOUtils.copy(requestWrapper.getReader(), bos, StandardCharsets.UTF_8);
        assertThat(new String(bos.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(body);
    }

}
