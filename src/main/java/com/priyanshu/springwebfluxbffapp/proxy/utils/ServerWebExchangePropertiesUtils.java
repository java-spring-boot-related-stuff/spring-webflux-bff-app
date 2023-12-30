package com.priyanshu.springwebfluxbffapp.proxy.utils;

import com.priyanshu.springwebfluxbffapp.proxy.model.Host;
import com.priyanshu.springwebfluxbffapp.proxy.model.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;


public class ServerWebExchangePropertiesUtils {

    public static final Logger log= LoggerFactory.getLogger(ServerWebExchangePropertiesUtils.class);

    private ServerWebExchangePropertiesUtils() {}

    /**
     * BFF path container attribute name.
     */
    public static final String PATH_CONTAINER_ATTR = "bff-pathContainerAttr";

    /**
     * BFF matched path attribute name.
     */
    public static final String MATCHED_PATH_ATTR = "bff-MatchedPathAttr";

    /**
     * BFF Matched Route ID attribute name.
     */
    public static final String MATCHED_ROUTE_ATTR = "bff-MatchedRouteAttr";

    /**
     * BFF matched Host ID attribute name.
     */
    public static final String MATCHED_HOST_ATTR = "bff-MatchedHostAttr";

    /**
     * URI template variables attribute name.
     */
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = "bff-uriTemplateVariables";

    @SuppressWarnings("unchecked")
    public static void putUriTemplateVariables(ServerWebExchange exchange, Map<String, String> uriVariables) {
        if (exchange.getAttributes().containsKey(URI_TEMPLATE_VARIABLES_ATTRIBUTE)) {
            Map<String, String> existingVariables = (Map<String, String>) exchange.getAttributes()
                    .get(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            HashMap<String, Object> newVariables = new HashMap<>();
            newVariables.putAll(existingVariables);
            newVariables.putAll(uriVariables);
            exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, newVariables);
        }
        else {
            exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getUriTemplateVariables(ServerWebExchange exchange) {
        if (exchange.getAttributes().containsKey(URI_TEMPLATE_VARIABLES_ATTRIBUTE)) {
            return (Map<String, String>) exchange.getAttributes().get(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        }
        else {
            return new HashMap<>();
        }
    }

    public static void putRouteDetails(ServerWebExchange exchange, Route route) {
        log.debug("Adding Route ID: {} to exchange", route.getId());
        exchange.getAttributes().put(MATCHED_ROUTE_ATTR, route);
    }

    public static void putHostDetails(ServerWebExchange exchange, Host host) {
        log.debug("Adding Host ID: {} to exchange", host.getId());
        exchange.getAttributes().put(MATCHED_HOST_ATTR, host);
    }





}
