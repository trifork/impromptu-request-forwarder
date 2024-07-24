package com.trifork.impromptu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Optional;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;

public class InternalDelegator {
//    private static final Logger log = Logger.getLogger(InternalDelegator.class);

    private ServletContext servletContext;

    public InternalDelegator(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void delegate(ServletContext targetServletContext, final HttpServletRequest req, HttpServletResponse res,
            String path) throws ServletException, IOException {

//        if (log.isDebugEnabled()) {
//            log.debug("delegateInternal, path=" + path);
//        }

        String subPath = path.substring(targetServletContext.getContextPath().length());

        RequestDispatcher requestDispatcher = targetServletContext.getRequestDispatcher(subPath);

        try {
            requestDispatcher.forward(req, res);
        } catch (Exception e) {
            StringBuffer url = req.getRequestURL();
            if (req.getQueryString() != null) {
                url.append('?');
                url.append(req.getQueryString());
            }
            throw new RuntimeException("Exception during internal delegation. URL= " + url + ", delegateRoot=" + path
                    + ", delegatePath=" + path, e);
        }
    }

    public InvocationResult doRequest(final String path, InputStream requestBody, String method,
            HeaderValueResolver headerValueResolver) throws ServletException, IOException {

//        if (log.isDebugEnabled()) {
//            log.debug("doRequest, path=" + path);
//        }

        ServletContext targetServletContext = servletContext.getContext(path);
        if (targetServletContext == null) {
            // res.sendError(HttpServletResponse.SC_NOT_FOUND, "Unable to
            // delegate: Target " + path + " not found");
            throw new IllegalArgumentException("Unable to delegate: Target " + path + " not found");
        }

        String targetContextPath = targetServletContext.getContextPath();

        // subPath = servletPath + pathInfo + queryString
        String subPath = path.substring(targetContextPath.length());

        String servletPath = guessServletPath(targetServletContext, subPath);

        String pathInfo = subPath.substring(servletPath.length());

        String queryString = null;
        int q = pathInfo.indexOf("?");

        if (q >= 0) {
            queryString = pathInfo.substring(q + 1);
            pathInfo = pathInfo.substring(0, q);
        }

        HttpServletRequest requestWrapper = ImpromptuRequest.createRequest(targetContextPath, servletPath, pathInfo,
                queryString, requestBody, method, headerValueResolver);

        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        HttpServletResponse response = ImpromptuRequest.createResponse(byteOutputStream);

        delegate(targetServletContext, requestWrapper, response, path);

//        if (log.isDebugEnabled()) {
//            log.debug("doRequest, got " + byteOutputStream.size() + " bytes, response="
//                    + byteOutputStream.toString(StandardCharsets.UTF_8.name()));
//        }

        return new InvocationResult(response, new ByteArrayInputStream(byteOutputStream.toByteArray()));
    }

    private String guessServletPath(ServletContext servletContext, String subPath) {
        Optional<String> servletPath = servletContext.getServletRegistrations().values().stream()
                .flatMap(sr -> sr.getMappings().stream()).map(m -> matchMappingToPath(m, subPath))
                .filter(p -> p != null).max(new Comparator<String>() {

                    @Override
                    public int compare(String s1, String s2) {
                        return s2.length() - s1.length();
                    }
                });

        if (servletPath.isPresent()) {
            return servletPath.get();
        }
        
        // Fallback to stripping the first path component (work around T4 bug TT-576)       
        int idx = subPath.indexOf("/", 1);

        return subPath.substring(0, idx);
    }

    private String matchMappingToPath(String urlPattern, String path) {
        if (urlPattern.endsWith("/*")) {
            String prefix = urlPattern.substring(0, urlPattern.length() - 2);
            if (path.startsWith(prefix)) {
                return prefix;
            }
        } else if (urlPattern.equals(path)) {
            return urlPattern;
        }
        return null;
    }

}
