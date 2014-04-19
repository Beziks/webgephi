package cz.cokrtvac.webgephi.client;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23. 3. 2014
 * Time: 17:49
 */
public interface WebgephiClient {
    Response get(String targetPath) throws WebgephiClientException;

    Response put(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException;

    Response post(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException;
}
