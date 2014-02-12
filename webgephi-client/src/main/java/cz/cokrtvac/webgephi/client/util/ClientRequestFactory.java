package cz.cokrtvac.webgephi.client.util;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 25.1.14
 * Time: 18:49
 */
public class ClientRequestFactory {
    /**
     * Creates Client request which does not care about server certificate.
     *
     * @param url
     * @return
     */
    public static ClientRequest create(String url){
        try {
            ApacheHttpClient4Executor e = new ApacheHttpClient4Executor(getSecuredHttpClient());
            return new ClientRequest(url, e);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Client Request object", e);
        }
    }

    private static DefaultHttpClient getSecuredHttpClient() throws Exception {
        return getSecuredHttpClient(new DefaultHttpClient());
    }

    private static DefaultHttpClient getSecuredHttpClient(HttpClient httpClient) throws Exception {
        final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return _AcceptedIssuers;
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }
            };
            ctx.init(null, new TrustManager[]{ tm }, new SecureRandom());
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", 443, ssf));
            return new DefaultHttpClient(ccm, httpClient.getParams());
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String args []) throws Exception {
        ClientRequest r = create("https://localhost:8443/webgephiserver/rest/users?oauth_token=9dc69ecd-2bf8-45db-8940-ac24db0aaceb&oauth_consumer_key=client.webgephi.cz&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1390678274&oauth_nonce=865131424771094&oauth_version=1.0&oauth_signature=ecIZEd3tWRD3F7nPQPDWCgXLe20%3D");
        ClientResponse<String> res = r.get(String.class);
        System.out.println(res.getResponseStatus() + " " + res.getEntity());
    }
}
