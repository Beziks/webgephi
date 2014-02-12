package cz.cokrtvac.webgephi.client;

/**
 * Keeps info about access and request token
 * <p/>
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 22:56
 */
public class Token {
    private String consumerKey;
    private String consumerSecret;

    private String token;
    private String secret;
    // Request token only
    private String verifier;

    public Token(String consumerKey, String consumerSecret, String token, String secret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getVerifier() {
        return verifier;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    @Override
    public String toString() {
        return "Token{" +
                "consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='" + consumerSecret + '\'' +
                ", token='" + token + '\'' +
                ", secret='" + secret + '\'' +
                ", verifier='" + verifier + '\'' +
                '}';
    }
}
