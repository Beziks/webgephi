package cz.cokrtvac.webgephi.webgephiserver.core.auth.not_used;

import cz.cokrtvac.webgephi.api.util.Log;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import java.io.IOException;


public class WebgephiBasicAuthenticator extends ValveBase {

    private Logger log = Log.get(WebgephiBasicAuthenticator.class);


    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        log.info("WebgephiBasicAuthenticator START");
    }
}
