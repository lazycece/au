package com.lazycece.au;

import com.lazycece.au.filter.AuFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author lazycece
 * @date 2019/11/27
 */
@RunWith(MockitoJUnitRunner.class)
public class AuRunnerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuFilter permitFilter;
    @Mock
    private AuFilter interceptFilter;
    @Mock
    private AuFilter lastFilter;

    @Before
    public void setup() throws Exception {
        when(permitFilter.name()).thenReturn("permit-filter");
        when(permitFilter.preHandle()).thenReturn(true);

        when(interceptFilter.name()).thenReturn("intercept-filter");
        when(interceptFilter.preHandle()).thenReturn(true);

        when(lastFilter.name()).thenReturn("last-filter");
        when(lastFilter.preHandle()).thenReturn(false);

        AuManager auManager = AuManager.getInstance();
        auManager.addAuFilter(permitFilter).includePatterns("/**");
        auManager.addAuFilter(interceptFilter).includePatterns("/**").order(1);
        auManager.addAuFilter(lastFilter).includePatterns("/**").order(2);

        when(request.getRequestURI()).thenReturn("/au/runner");
//        when(response.getWriter()).thenReturn(new PrintWriter(
//                new OutputStreamWriter(new ByteArrayOutputStream(), StandardCharsets.UTF_8),
//                true));
    }

    @Test
    public void run() throws Exception {
        AuRunner auRunner = new AuRunner();
        try {
            auRunner.init(request, response);
            boolean permit = auRunner.preHandle();
            assertThat(permit).isFalse();
            auRunner.postHandle();
        } finally {
            auRunner.unset();
        }
    }

    @After
    public void check() throws Exception {
        verify(permitFilter, times(1)).preHandle();
        verify(permitFilter, times(1)).postHandle();

        verify(interceptFilter, times(1)).preHandle();
        verify(interceptFilter, times(1)).postHandle();

        verify(lastFilter, times(1)).preHandle();
        verify(lastFilter, times(0)).postHandle();

        InOrder inOrder = inOrder(permitFilter, interceptFilter, lastFilter);
        inOrder.verify(permitFilter).preHandle();
        inOrder.verify(interceptFilter).preHandle();
        inOrder.verify(lastFilter).preHandle();
        inOrder.verify(interceptFilter).postHandle();
        inOrder.verify(permitFilter).postHandle();
    }
}
