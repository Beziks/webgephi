package cz.cokrtvac.webgephi.webgephiserver.core.auth.not_used;

import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import cz.cokrtvac.webgephi.webgephiserver.core.auth.LoginManager;
import org.jboss.security.ErrorCodes;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.picketlink.idm.credential.Credentials;
import org.slf4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12.1.14
 * Time: 1:15
 */
public class WebgephiLoginModule extends AbstractServerLoginModule {
    private static final Logger log = Log.get(WebgephiLoginModule.class);
    /**
     * The login identity
     */
    private Principal identity;
    /**
     * The proof of login identity
     */
    private char[] credential;

    private LoginManager loginManager;

    /**
     * Override the superclass method to look for the following options after
     * first invoking the super version.
     *
     * @param options :
     *                option: hashAlgorithm - the message digest algorithm used to hash passwords.
     *                If null then plain passwords will be used.
     *                option: hashCharset - the name of the charset/encoding to use when converting
     *                the password String to a byte array. Default is the platform's default
     *                encoding.
     *                option: hashEncoding - the string encoding format to use. Defaults to base64.
     *                option: ignorePasswordCase: A flag indicating if the password comparison
     *                should ignore case.
     *                option: digestCallback - The class name of the DigestCallback {@link org.jboss.crypto.digest.DigestCallback}
     *                implementation that includes pre/post digest content like salts for hashing
     *                the input password. Only used if hashAlgorithm has been specified.
     *                option: hashStorePassword - A flag indicating if the store password returned
     *                from #getUsersPassword() should be hashed .
     *                option: hashUserPassword - A flag indicating if the user entered password should be hashed.
     *                option: storeDigestCallback - The class name of the DigestCallback {@link org.jboss.crypto.digest.DigestCallback}
     *                implementation that includes pre/post digest content like salts for hashing
     *                the store/expected password. Only used if hashStorePassword or hashUserPassword is true and
     *                hashAlgorithm has been specified.
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);

        try {
            InitialContext initialContext = new InitialContext();
            loginManager = (LoginManager) initialContext.lookup("java:module/LoginManager");
        } catch (NamingException e) {
            log.error("Lookup forLoginManager failed", e);
            throw new InitializationException("Lookup for LoginManager failed", e);
        }
        //identityManager = StaticBeanFactory.getIdentityManager();
    }

    /**
     * Perform the authentication of the username and password.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean login() throws LoginException {
        try {
            HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
            log.info(request.toString());
            log.info(request.getHeader("Authorization"));
        } catch (PolicyContextException e) {
            e.printStackTrace();
        }

        // See if shared credentials exist
        if (super.login() == true) {
            log.info("Shared credentials exists.");
            // Setup our view of the user
            Object username = sharedState.get("javax.security.auth.login.name");
            if (username instanceof Principal)
                identity = (Principal) username;
            else {
                String name = username.toString();
                try {
                    identity = createIdentity(name);
                    log.info("IDENTITY=" + identity.getName());
                } catch (Exception e) {
                    log.debug("Failed to createRegularUser principal", e);
                    throw new LoginException(ErrorCodes.PROCESSING_FAILED + "Failed to createRegularUser principal: " + e.getMessage());
                }
            }
            Object password = sharedState.get("javax.security.auth.login.password");
            if (password instanceof char[])
                credential = (char[]) password;
            else if (password != null) {
                String passwordString = password.toString();
                credential = passwordString.toCharArray();
                log.info("CREDENTIAL=" + passwordString);
            }
            return true;
        }

        super.loginOk = false;
        String[] info = getUsernameAndPassword();
        String username = info[0];
        String password = info[1];
        log.info("USERNAME=" + username);
        log.info(("PASSWORD=" + password));

        if (username == null && password == null) {
            identity = unauthenticatedIdentity;
            super.log.trace("Authenticating as unauthenticatedIdentity=" + identity);
        }

        if (identity == null) {
            try {
                identity = createIdentity(username);
            } catch (Exception e) {
                log.debug("Failed to createRegularUser principal", e);
                throw new LoginException(ErrorCodes.PROCESSING_FAILED + "Failed to createRegularUser principal: " + e.getMessage());
            }

            // Check password
            Credentials.Status status = loginManager.validate(username, password);

            if (!Credentials.Status.VALID.equals(status)) {
                FailedLoginException fle = new FailedLoginException("Password Incorrect/Password Required");
                log.debug("Bad password for username=" + username + ". Status=" + status);
                throw fle;
            }
        }

        if (getUseFirstPass() == true) {    // Add the principal and password to the shared state map
            sharedState.put("javax.security.auth.login.name", identity);
            sharedState.put("javax.security.auth.login.password", credential);
        }
        super.loginOk = true;
        super.log.trace("User '" + identity + "' authenticated, loginOk=" + loginOk);
        return true;
    }

    @Override
    protected Principal getIdentity() {
        return identity;
    }

    @Override
    protected Group[] getRoleSets() throws LoginException {
        return new Group[]{loginManager.getRoleSets(identity.getName())};
    }

    @Override
    protected Principal getUnauthenticatedIdentity() {
        return unauthenticatedIdentity;
    }

    /**
     * Called by login() to acquire the username and password strings for
     * authentication. This method does no validation of either.
     *
     * @return String[], [0] = username, [1] = password
     * @throws LoginException thrown if CallbackHandler is not set or fails.
     */
    protected String[] getUsernameAndPassword() throws LoginException {
        String[] info = {null, null};
        // prompt for a username and password
        if (callbackHandler == null) {
            throw new LoginException(ErrorCodes.NULL_VALUE + "Error: no CallbackHandler available " +
                    "to collect authentication information");
        }

        NameCallback nc = new NameCallback("User name: ", "guest");
        PasswordCallback pc = new PasswordCallback("Password: ", false);
        Callback[] callbacks = {nc, pc};
        String username = null;
        String password = null;
        try {
            callbackHandler.handle(callbacks);
            username = nc.getName();
            char[] tmpPassword = pc.getPassword();
            if (tmpPassword != null) {
                credential = new char[tmpPassword.length];
                System.arraycopy(tmpPassword, 0, credential, 0, tmpPassword.length);
                pc.clearPassword();
                password = new String(credential);
            }
        } catch (IOException e) {
            LoginException le = new LoginException(ErrorCodes.PROCESSING_FAILED + "Failed to get username/password");
            le.initCause(e);
            throw le;
        } catch (UnsupportedCallbackException e) {
            LoginException le = new LoginException(ErrorCodes.UNRECOGNIZED_CALLBACK + "CallbackHandler does not support: " + e.getCallback());
            le.initCause(e);
            throw le;
        }
        info[0] = username;
        info[1] = password;
        return info;
    }
}
