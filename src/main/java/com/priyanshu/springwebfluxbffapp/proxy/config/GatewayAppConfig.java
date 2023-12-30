package com.priyanshu.springwebfluxbffapp.proxy.config;

import com.priyanshu.springwebfluxbffapp.proxy.condition.GatewayAppEnabledCondition;
import com.priyanshu.springwebfluxbffapp.proxy.model.Host;
import com.priyanshu.springwebfluxbffapp.proxy.model.PredicateDef;
import com.priyanshu.springwebfluxbffapp.proxy.model.RawGatewayProperties;
import com.priyanshu.springwebfluxbffapp.proxy.model.route.RawRouteProperties;
import com.priyanshu.springwebfluxbffapp.proxy.model.route.Route;
import com.priyanshu.springwebfluxbffapp.proxy.predicate.PredicateBuilder;
import com.priyanshu.springwebfluxbffapp.proxy.utils.ServerWebExchangePropertiesUtils;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
@Conditional(GatewayAppEnabledCondition.class)
public class GatewayAppConfig {

    private static final Logger log= LoggerFactory.getLogger(GatewayAppConfig.class);

    private final Map<String, Host> hostConfig= new HashMap<>();

    private final List<Route> routesConfig= new ArrayList<>();

    private final ApplicationContext applicationContext;


    /**
     * Creates an instance of Gateway App Configuration
     * @param applicationContext Spring Application Context, which will be used to fetch beans
     * @param rawGatewayProperties Raw Gateway Properties from app-config which will be used to serve the requests.
     */
    public GatewayAppConfig(
            ApplicationContext applicationContext,
            RawGatewayProperties rawGatewayProperties
            )
    {
    // Setting up hosts should be first step, since the Host Map will be used later to validate host id's.
    this.configureHosts(rawGatewayProperties.getHosts());
    this.applicationContext= applicationContext;
    this.parseRouteProperties(rawGatewayProperties.getRoutes());
    }

    /**
     * Picks a route based upon ServerWebExchange and configured Route Predicates
     * @param exchange ServerWebExchange, for which route needs to be fetched.
     * @return Selected route based upon provided ServerWebExchange and Configured RouteProperties
     */
    public Route selectRoute(ServerWebExchange exchange) {

        for(Route route : this.routesConfig) {
            if(matchRoutePredicates(route, exchange)) {
                log.debug("Selected Route: {} For URL: {}", route.getId(), exchange.getRequest().getURI().getRawPath());
                ServerWebExchangePropertiesUtils.putRouteDetails(exchange, route);
                return route;
            }
        }
        log.debug("No Gateway Routes were selected For URL: {}", exchange.getRequest().getURI().getRawPath());
        return null;
    }

    private boolean matchRoutePredicates(Route route, ServerWebExchange exchange) {
        for(Predicate<ServerWebExchange> predicate: route.getPredicates()) {
            if(!predicate.test(exchange)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prepares the Host configuration from the app-properties.
     * @param hosts List of hosts, which are available in app-properties
     */
    private void configureHosts(List<Host> hosts) {
        for(Host host: hosts) {
            hostConfig.put(host.getId(), host);
        }
    }

    /**
     * Parses route properties from app-properties to the Route
     * @param rawRouteProperties Route Properties From app-properties.
     */
    private void parseRouteProperties(List<RawRouteProperties> rawRouteProperties) {
        for(RawRouteProperties routeProperty: rawRouteProperties) {
            Route route= new Route();
            route.setId(routeProperty.getId());
            route.setHost(this.getHostById(routeProperty.getHostId()));
            route.setUpstream(routeProperty.getUpstream());
            route.setPredicates(parsePredicates(routeProperty.getPredicates()));
            routesConfig.add(route);
        }
    }

    /**
     * Method used to fetch Host By Host ID During Routes Initialization.
     * @param hostId Id of host, for which Host Config needs to be fetched.
     * @return Host Configuration
     * @throws ValidationException If Host with ID not found, throw ValidationException.
     */
    private Host getHostById(String hostId) throws ValidationException {
        if(hostConfig.containsKey(hostId)) {
            return hostConfig.get(hostId);
        }
        log.error("Requested Host with id: {} during routes initialization, but not exists. Have you defined any host with id: {}?",hostId, hostId);
        throw new ValidationException("No Host With ID: " + hostId + " Found During Routes Initialization");
    }

    /**
     * Parses the raw Predicate Definitions in app-properties in Java 8 Predicate during application start time itself.
     * @param rawPredicates List of raw predicates, which needs to be parsed in Java 8 Predicate Function
     * @return List of Predicates in parsed format
     */
    private List<Predicate<ServerWebExchange>> parsePredicates(List<String> rawPredicates) {
        List<Predicate<ServerWebExchange>> parsedPredicates= new ArrayList<>();

        for(String rawPredicate: rawPredicates) {
            PredicateDef predicateDef= new PredicateDef(rawPredicate);
            PredicateBuilder builder= this.loadPredicate(predicateDef);
            parsedPredicates.add(builder.apply(predicateDef));
        }

        return parsedPredicates;
    }

    /**
     * Loads the PredicateBuilder bean by its name. Or else throws BeansException if bean is not found.
     * @param predicateDef PredicateDefinition, for which the builder bean needs to be loaded.
     * @return An instance of PredicateBuilder with the provided bean name, which is available in ApplicationContext.
     */
    private PredicateBuilder loadPredicate(PredicateDef predicateDef) throws BeansException {
        log.debug("Loading The Predicate Definition For Predicate: {}", predicateDef);

        String beanName= predicateDef.getName()+ "Predicate";

        try {
            return applicationContext.getBean(beanName, PredicateBuilder.class);
        }
        catch(BeansException ex) {
            log.error("No bean definition found for bean {}. Is there any bean available in applicationContext with this name?", beanName);
            throw ex;
        }
    }

}
