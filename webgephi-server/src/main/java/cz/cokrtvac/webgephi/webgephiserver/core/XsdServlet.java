package cz.cokrtvac.webgephi.webgephiserver.core;

import cz.cokrtvac.webgephi.api.util.IOUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8. 6. 2014
 * Time: 11:05
 */
@WebServlet(urlPatterns = "/v1/*")
public class XsdServlet extends HttpServlet {
    @Inject
    private Logger log;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String file = req.getRequestURI();
        int i = file.lastIndexOf("/");
        file = file.substring(i + 1);

        log.info(req.getRequestURI() + " | " + file);

        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(file);
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream(file + ".xsd");
            }
            if (is == null) {
                resp.setContentType("text/plain");
                resp.getWriter().write("No resource found: " + file);
                resp.setStatus(400);
            } else {
                resp.setContentType("text/xml");
                OutputStream os = resp.getOutputStream();
                IOUtil.copy(is, os);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
