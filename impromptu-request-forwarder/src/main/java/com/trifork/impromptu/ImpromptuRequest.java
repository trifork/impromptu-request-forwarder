package com.trifork.impromptu;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

//import org.apache.log4j.Logger;

public class ImpromptuRequest {

    static HttpServletRequest createRequest(final String contextPath, final String servletPath,
            final String pathInfo, final String queryString, InputStream body, String method, HeaderValueResolver headers) {
        HttpServletRequest requestWrapper = new HttpServletRequest() {

            @Override
            public String getMethod() {
                return method;
            }

            @Override
            public StringBuffer getRequestURL() {
                StringBuffer sb = new StringBuffer();
                sb.append("http://localhost");
                sb.append(":");
                sb.append(getLocalPort());
                sb.append(contextPath);
                sb.append(servletPath);
                sb.append(pathInfo);

                return sb;
            }

            @Override
            public String getRequestURI() {
                return contextPath + servletPath + pathInfo;
            }
            @Override
            public String getServletPath() {
                return servletPath;
            }
            @Override
            public String getPathInfo() {
                return pathInfo;
            }

            @Override
            public String getQueryString() {
                return queryString;
            }

            @Override
            public String getHeader(String name) {
                final ClassLoader localContextClassLoader = Thread.currentThread().getContextClassLoader();
                ClassLoader callersContextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(localContextClassLoader);
                    List<String> values = headers.getValue(name);
                    if (values == null) {
                        return null;
                    }
                    return values.get(0);
                } finally {
                    Thread.currentThread().setContextClassLoader(callersContextClassLoader);
                }
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                final ClassLoader localContextClassLoader = Thread.currentThread().getContextClassLoader();
                ClassLoader callersContextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(localContextClassLoader);
                    List<String> values = headers.getValue(name);
                    if (values == null) {
                        return Collections.emptyEnumeration();
                    }
                    return Collections.enumeration(values);
                } finally {
                    Thread.currentThread().setContextClassLoader(callersContextClassLoader);
                }
            }
            @Override
            public Enumeration<String> getHeaderNames() {
                return headers.getKeys();
            }

            Map<String, Object> attributes = new HashMap<>();
            private String charset = "UTF-8";
            private ServletInputStream i;
            private BufferedReader r;

            @Override
            public void setAttribute(String name, Object value) {
                attributes.put(name, value);
            }

            @Override
            public Object getAttribute(String name) {
                Object result = attributes.get(name);

                return result;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return Collections.enumeration(attributes.keySet());
            }

            @Override
            public void removeAttribute(String name) {
                attributes.remove(name);
            }

            @Override
            public ServletInputStream getInputStream() {
                if (r != null) {
                    throw new IllegalStateException("getReader already called");
                }

                if (i == null) {
                    i = new ServletInputStream() {

                        @Override
                        public int read() throws IOException {
                            return body.read();
                        }

                        @Override
                        public int read(byte b[], int off, int len) throws IOException {
                            return body.read(b, off, len);
                        }
                    };
                }
                
                return i;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                if (i != null) {
                    throw new IllegalStateException("getInputStream already called");
                }

                if (r == null) {
                    r = new BufferedReader(new InputStreamReader(body, charset));
                }

                return r;
            }

            @Override
            public String getCharacterEncoding() {
                return charset;
            }

            @Override
            public void setCharacterEncoding(String charset) throws UnsupportedEncodingException {
                this.charset = charset;
            }

            @Override
            public int getContentLength() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getContentType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getParameter(String name) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getProtocol() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getScheme() {
                return "http";
            }

            @Override
            public String getServerName() {
                return "localhost";
            }

            @Override
            public int getServerPort() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getRemoteAddr() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getRemoteHost() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Locale getLocale() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isSecure() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getRealPath(String path) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getRemotePort() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getLocalName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getLocalAddr() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getLocalPort() {
                return 80;
            }

            @Override
            public ServletContext getServletContext() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
                    throws IllegalStateException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getAuthType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public long getDateHeader(String name) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getIntHeader(String name) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getPathTranslated() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getContextPath() {
                return contextPath;
            }

            @Override
            public String getRemoteUser() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isUserInRole(String role) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HttpSession getSession(boolean create) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HttpSession getSession() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void login(String username, String password) throws ServletException {
                // TODO Auto-generated method stub

            }

            @Override
            public void logout() throws ServletException {
                // TODO Auto-generated method stub

            }

            @Override
            public Collection<Part> getParts() throws IOException, ServletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Part getPart(String name) throws IOException, ServletException {
                // TODO Auto-generated method stub
                return null;
            }
        };
        return requestWrapper;
    }

    static HttpServletResponse createResponse(final ByteArrayOutputStream baos) {
        return new HttpServletResponse() {

            Map<String, String> headerMap = new HashMap<>();
            private int statusCode = SC_OK;
            private String statusMessage;
            private ServletOutputStream o;
            private PrintWriter w = null;
            private String charset = "UTF-8";

            @Override
            public void flushBuffer() throws IOException {
            }

            @Override
            public int getBufferSize() {
                return 0;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public String getContentType() {
                return headerMap.get("Content-Type");
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {

                if (w != null) {
                    throw new IllegalStateException("getWriter() already called");
                }

                if (o == null) {
                    o = new ServletOutputStream() {

                        public void write(int b) throws IOException {
                            baos.write(b);
                        }

                        public void write(byte[] b, int off, int len) throws IOException {
                            baos.write(b, off, len);
                        }
                    };
                }

                return o;
            }

            @Override
            public PrintWriter getWriter() throws IOException {

                if (o != null) {
                    throw new IllegalStateException("getOutputStream() already called");
                }

                if (w == null) {
                    w = new PrintWriter(new OutputStreamWriter(baos, charset));
                }

                return w;
            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public void reset() {
                baos.reset();
                headerMap.clear();
                statusCode = SC_OK;
            }

            @Override
            public void resetBuffer() {
                baos.reset();
            }

            @Override
            public void setBufferSize(int size) {
                // 
            }

            @Override
            public void setCharacterEncoding(String charset) {
                this.charset = charset;
            }

            @Override
            public void setContentLength(int contentLength) {
                headerMap.put("Content-Length", String.valueOf(contentLength));
            }

            @Override
            public void setContentType(String contentType) {
                headerMap.put("Content-Type", contentType);
            }

            @Override
            public void setLocale(Locale arg0) {
                // 
            }

            @Override
            public void addCookie(Cookie arg0) {
                //
            }

            @Override
            public void addDateHeader(String name, long epochMilli) {
                String formattedDate = Instant.ofEpochMilli(epochMilli).toString();

                addHeader(name, formattedDate);
            }

            @Override
            public void addHeader(String name, String value) {
                headerMap.put(name, value);
            }

            @Override
            public void addIntHeader(String name, int value) {
                addHeader(name, String.valueOf(value));
            }

            @Override
            public boolean containsHeader(String name) {
                return headerMap.containsKey(name);
            }

            @Override
            public String encodeRedirectURL(String name) {
                return null;
            }

            @Override
            public String encodeRedirectUrl(String arg0) {
                return null;
            }

            @Override
            public String encodeURL(String arg0) {
                return null;
            }

            @Override
            public String encodeUrl(String arg0) {
                return null;
            }

            @Override
            public void sendError(int sc) throws IOException {
                setStatus(sc);
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {
                statusMessage = msg;

                setStatus(statusCode);

                if (msg == null) {
                    this.statusMessage = getStatusMessage(statusCode);
                }

                switch (statusCode) {

                // The following status codes must not be accompanied by a
                // content body.  

                case HttpServletResponse.SC_CONTINUE :
                case HttpServletResponse.SC_SWITCHING_PROTOCOLS :
                case HttpServletResponse.SC_NOT_MODIFIED :
                case HttpServletResponse.SC_NO_CONTENT :
                    break;

                default :

                    setContentType("text/html");
                    PrintWriter pw = getWriter();
                    StringBuffer sb = new StringBuffer();
                    sb.append("<HTML><BODY>\n");
                    sb.append("<H1>");
                    sb.append(statusCode);
                    sb.append(": ");
                    sb.append(this.statusMessage);
                    sb.append("</H1>\n");
                    if (msg != null) {
                        sb.append("<B>");
                        sb.append(msg);
                        sb.append("</B>\n");
                    }
                    sb.append("</BODY></HTML>");

                    String content = sb.toString();
                    setContentLength(content.length());

                    pw.print(content);
                }
            }

            @Override
            public void sendRedirect(String location) throws IOException {
                throw new UnsupportedOperationException("Redirect not yet supported");
            }

            @Override
            public void setDateHeader(String name, long epochMilli) {
                String formattedDate = Instant.ofEpochMilli(epochMilli).toString();

                setHeader(name, formattedDate);
            }

            @Override
            public void setHeader(String name, String value) {
                headerMap.put(name, value);
            }

            @Override
            public void setIntHeader(String name, int value) {
                setHeader(name, String.valueOf(value));
            }

            @Override
            public void setStatus(int statusCode) {
                this.statusCode = statusCode;
            }

            @Override
            public void setStatus(int statusCode, String statusMessage) {
                this.statusCode = statusCode;
                this.statusMessage = statusMessage;
            }

            @Override
            public String getHeader(String name) {
                return headerMap.get(name);
            }

            @Override
            public Collection<String> getHeaderNames() {
                return headerMap.keySet();
            }

            @Override
            public Collection<String> getHeaders(String name) {
                String value = headerMap.get(name);
                if (value == null) {
                    return Collections.emptyList();
                }
                return Collections.singleton(value);
            }

            @Override
            public int getStatus() {
                return statusCode;
            }

        };
    }

    private static String getStatusMessage(int status) {

        switch (status) {
        case HttpServletResponse.SC_OK :
            return ("OK"); // 200
        case HttpServletResponse.SC_ACCEPTED :
            return ("Accepted"); // 202
        case HttpServletResponse.SC_BAD_GATEWAY :
            return ("Bad Gateway"); // 502
        case HttpServletResponse.SC_BAD_REQUEST :
            return ("Bad Request"); // 400
        case HttpServletResponse.SC_CONFLICT :
            return ("Conflict"); // 409
        case HttpServletResponse.SC_CONTINUE :
            return ("Continue"); // 100
        case HttpServletResponse.SC_CREATED :
            return ("Created"); // 201
        case HttpServletResponse.SC_EXPECTATION_FAILED :
            return ("Expectation Failed"); // 417
        case HttpServletResponse.SC_FORBIDDEN :
            return ("Forbidden"); // 403
        case HttpServletResponse.SC_GATEWAY_TIMEOUT :
            return ("Gateway Timeout"); // 504
        case HttpServletResponse.SC_GONE :
            return ("Gone"); // 410
        case HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED :
            return ("HTTP Version Not Supported"); // 505
        case HttpServletResponse.SC_INTERNAL_SERVER_ERROR :
            return ("Internal Server Error"); // 500
        case HttpServletResponse.SC_LENGTH_REQUIRED :
            return ("Length Required"); //411
        case HttpServletResponse.SC_METHOD_NOT_ALLOWED :
            return ("Method Not Allowed"); // 405
        case HttpServletResponse.SC_MOVED_PERMANENTLY :
            return ("Moved Permanently"); // 301
        case HttpServletResponse.SC_MOVED_TEMPORARILY :
            return ("Moved Temporarily"); // 302
        case HttpServletResponse.SC_MULTIPLE_CHOICES :
            return ("Multiple Choices"); // 300
        case HttpServletResponse.SC_NO_CONTENT :
            return ("No Content"); // 204
        case HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION :
            return ("Non-Authoritative Information"); // 203
        case HttpServletResponse.SC_NOT_ACCEPTABLE :
            return ("Not Acceptable"); // 406
        case HttpServletResponse.SC_NOT_FOUND :
            return ("Not Found"); // 404
        case HttpServletResponse.SC_NOT_IMPLEMENTED :
            return ("Not Implemented"); // 501
        case HttpServletResponse.SC_NOT_MODIFIED :
            return ("Not Modified"); // 304
        case HttpServletResponse.SC_PARTIAL_CONTENT :
            return ("Partial Content"); // 206
        case HttpServletResponse.SC_PAYMENT_REQUIRED :
            return ("Payment Required"); // 402
        case HttpServletResponse.SC_PRECONDITION_FAILED :
            return ("Precondition Failed"); // 412
        case HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED :
            return ("Proxy Authentication Required"); // 407
        case HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE :
            return ("Request Entity Too Large"); // 413
        case HttpServletResponse.SC_REQUEST_TIMEOUT :
            return ("Request Timeout"); // 408
        case HttpServletResponse.SC_REQUEST_URI_TOO_LONG :
            return ("Request URI Too Long"); //414
        case HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE :
            return ("Requested Range Not Satisfiable"); // 416
        case HttpServletResponse.SC_RESET_CONTENT :
            return ("Reset Content"); // 205
        case HttpServletResponse.SC_SEE_OTHER :
            return ("See Other"); // 303
        case HttpServletResponse.SC_SERVICE_UNAVAILABLE :
            return ("Service Unavailable"); // 503
        case HttpServletResponse.SC_SWITCHING_PROTOCOLS :
            return ("Switching Protocols"); // 101
        case HttpServletResponse.SC_UNAUTHORIZED :
            return ("Unauthorized"); // 401
        case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE :
            return ("Unsupported Media Type"); // 415
        case HttpServletResponse.SC_USE_PROXY :
            return ("Use Proxy"); // 305
        default :
            return ("HTTP Response Status " + status);
        }
    }
}

