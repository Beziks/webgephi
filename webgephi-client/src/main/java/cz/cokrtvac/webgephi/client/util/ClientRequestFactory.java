package cz.cokrtvac.webgephi.client.util;

import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 25.1.14
 * Time: 18:49
 */
public class ClientRequestFactory {
    private static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }
    }};

    private static SSLContext sslContext = initSSLContext();

    private static SSLContext initSSLContext() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            return sc;
        }catch (Exception e){
            throw new RuntimeException("Client ssl context cannot be initialized", e);
        }
    }

    private static HostnameVerifier dummyVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    /**
     * Creates Client request which does not care about server certificate.
     *
     * @return
     */
    public static Client create() {
        try {
            ClientBuilder b = ClientBuilder.newBuilder();
            b.sslContext(sslContext);
            b.hostnameVerifier(dummyVerifier);
            Client c =  b.build();
            return c;

        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Client object", e);
        }
    }
}
