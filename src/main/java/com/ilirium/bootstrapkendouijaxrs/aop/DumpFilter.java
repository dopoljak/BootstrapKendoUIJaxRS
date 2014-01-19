package com.ilirium.bootstrapkendouijaxrs.aop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response.Status;

public class DumpFilter implements Filter
{
     private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DumpFilter.class);
     
    private static class ByteArrayServletStream extends ServletOutputStream
    {

        ByteArrayOutputStream baos;

        ByteArrayServletStream(ByteArrayOutputStream baos)
        {
            this.baos = baos;
        }

        public void write(int param) throws IOException
        {
            baos.write(param);
        }

        @Override
        public boolean isReady()
        {
            throw new UnsupportedOperationException("Not supported yet. BLA BLA !!!");
        }

        @Override
        public void setWriteListener(WriteListener wl)
        {
            throw new UnsupportedOperationException("Not supported yet. BLA BLA !!!");
        }
    }

    private static class ByteArrayPrintWriter
    {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private PrintWriter pw = new PrintWriter(baos);
        private ServletOutputStream sos = new ByteArrayServletStream(baos);

        public PrintWriter getWriter()
        {
            return pw;
        }

        public ServletOutputStream getStream()
        {
            return sos;
        }

        byte[] toByteArray()
        {
            return baos.toByteArray();
        }
    }

    private class BufferedServletInputStream extends ServletInputStream
    {

        ByteArrayInputStream bais;

        public BufferedServletInputStream(ByteArrayInputStream bais)
        {
            this.bais = bais;
        }

        @Override
        public int available()
        {
            return bais.available();
        }

        public int read()
        {
            return bais.read();
        }

        @Override
        public int read(byte[] buf, int off, int len)
        {
            return bais.read(buf, off, len);
        }

        @Override
        public boolean isFinished()
        {
            throw new UnsupportedOperationException("Not supported yet. BLA BLA !!!");
        }

        @Override
        public boolean isReady()
        {
            throw new UnsupportedOperationException("Not supported yet. BLA BLA !!!");
        }

        @Override
        public void setReadListener(ReadListener rl)
        {
            throw new UnsupportedOperationException("Not supported yet. BLA BLA !!!");
        }
    }

    private class BufferedRequestWrapper extends HttpServletRequestWrapper
    {

        ByteArrayInputStream bais;
        ByteArrayOutputStream baos;
        BufferedServletInputStream bsis;
        byte[] buffer;

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException
        {
            super(req);
            InputStream is = req.getInputStream();
            baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int letti;
            while ((letti = is.read(buf)) > 0) {
                baos.write(buf, 0, letti);
            }
            buffer = baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream()
        {
            try {
                bais = new ByteArrayInputStream(buffer);
                bsis = new BufferedServletInputStream(bais);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return bsis;
        }

        public byte[] getBuffer()
        {
            return buffer;
        }
    }
    private boolean dumpRequest;
    private boolean dumpResponse;

    public void init(FilterConfig filterConfig) throws ServletException
    {
        dumpRequest = Boolean.valueOf(filterConfig.getInitParameter("dumpRequest"));
        dumpResponse = Boolean.valueOf(filterConfig.getInitParameter("dumpResponse"));
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException
    {
        
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpSession session = httpRequest.getSession(true);
        
        log.info("Method = {}, Path = {}{}, Query = {}", new Object[] { httpRequest.getMethod(), httpRequest.getServletPath(), httpRequest.getPathInfo(), httpRequest.getQueryString() });
        log.info("SessionID = {}, IP = {}", new Object[] { session.getId(), httpRequest.getRemoteAddr() } );
        
        final BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpRequest);

        if (dumpRequest) {
            log.info("Request content -> {}", new String(bufferedRequest.getBuffer()) );
        }

        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
        final HttpServletResponse wrappedResp = new HttpServletResponseWrapper(httpResponse)
        {
            @Override
            public PrintWriter getWriter()
            {
                return pw.getWriter();
            }

            @Override
            public ServletOutputStream getOutputStream()
            {
                return pw.getStream();
            }
        };

        filterChain.doFilter(bufferedRequest, wrappedResp);

        byte[] bytes = pw.toByteArray();
        httpResponse.getOutputStream().write(bytes);
        
        if (dumpResponse) {
            log.info("Response content -> {}", new String(bytes));
        }        
        
        log.info("JAX-RS returned : code = {}, status = {}", httpResponse.getStatus(), Status.fromStatusCode(httpResponse.getStatus()));
    }

    public void destroy()
    {
    }
}