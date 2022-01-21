package io.ayoho.endpoints.utils;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.security.auth.Subject;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;

public class EndpointUtils {

    public String getRequestDump(UriInfo uriInfo, HttpHeaders headers, SecurityContext sec) {
        String result = "";
        result += getUriInfoDump(uriInfo);
        result += addOutputLine("");
        result += getRequestHeadersDump(headers);
        result += addOutputLine("");
        result += getSecurityContextDump(sec);
        result += addOutputLine("");
        result += getCookiesDump(headers);
        return result;
    }

    public String getUriInfoDump(UriInfo uriInfo) {
        String result = addOutputLine("UriInfo:");
        result += addOutputLine("getBaseUri(): " + uriInfo.getBaseUri().toString());
        result += addOutputLine("getRequestUri(): " + uriInfo.getRequestUri().toString());
        result += addOutputLine("getAbsolutePath(): " + uriInfo.getAbsolutePath().toString());
        result += addOutputLine("getPath(): " + uriInfo.getPath());
        MultivaluedMap<String, String> pathParams = uriInfo.getPathParameters();
        for (Entry<String, List<String>> pathParam : pathParams.entrySet()) {
            result += addOutputLine("pathParam: key: " + pathParam.getKey() + "=" + pathParam.getValue());
        }
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        for (PathSegment segment : pathSegments) {
            result += addOutputLine("pathSegment: " + segment.toString());
        }
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Entry<String, List<String>> queryParam : queryParams.entrySet()) {
            result += addOutputLine("queryParam: key: " + queryParam.getKey() + "=" + queryParam.getValue());
        }
        return result;
    }

    public String getRequestHeadersDump(HttpHeaders headers) {
        String result = addOutputLine("Headers:");
        MultivaluedMap<String, String> reqHeaders = headers.getRequestHeaders();
        for (Entry<String, List<String>> entry : reqHeaders.entrySet()) {
            String headerName = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                result += addOutputLine("Header: " + headerName + ": " + value);
            }
        }
        return result;
    }

    public String getSecurityContextDump(SecurityContext sec) {
        String result = addOutputLine("SecurityContext:");
        Principal user = sec.getUserPrincipal();
        result += addOutputLine("getUserPrincipal(): " + user);
        result += addOutputLine("getUserPrincipal().getName(): " + user.getName());
        result += addOutputLine("getAuthenticationScheme(): " + sec.getAuthenticationScheme());
        result += addOutputLine(getWSCredentialsDump());
        return result;
    }

    public String getWSCredentialsDump() {
        String result = addOutputLine("WSSubject:");
        try {
            Subject callerSubject = WSSubject.getCallerSubject();
            Set<Principal> principals = callerSubject.getPrincipals();
            for (Principal principal : principals) {
                result += addOutputLine("principal: getName(): " + principal.getName());
            }
            Set<Object> publicCreds = callerSubject.getPublicCredentials();
            for (Object cred : publicCreds) {
                result += addOutputLine("publicCred: " + cred);
            }
            Set<Object> privateCreds = callerSubject.getPrivateCredentials();
            for (Object cred : privateCreds) {
                result += addOutputLine("privateCred: " + cred);
            }
        } catch (WSSecurityException e) {
            result += addOutputLine("Caught exception getting WSSubject data: " + e);
        }
        return result;
    }

    public String getCookiesDump(HttpHeaders headers) {
        String result = addOutputLine("Cookies:");
        Map<String, Cookie> cookies = headers.getCookies();
        for (Entry<String, Cookie> entry : cookies.entrySet()) {
            Cookie cookie = entry.getValue();
            String cookieString = cookie.getName() + "=" + cookie.getValue() + "; path=" + cookie.getPath() + "; domain=" + cookie.getDomain();
            result += addOutputLine("cookie: " + cookieString);
        }
        return result;
    }

    public String addOutputLine(String line) {
        return line + "\n";
    }

}
