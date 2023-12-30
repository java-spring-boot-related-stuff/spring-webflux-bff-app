package com.priyanshu.springwebfluxbffapp.proxy.predicate;

import com.priyanshu.springwebfluxbffapp.constant.GatewayConstants;
import com.priyanshu.springwebfluxbffapp.proxy.model.PredicateDef;
import com.priyanshu.springwebfluxbffapp.proxy.utils.ServerWebExchangePropertiesUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.function.Predicate;

public class PathPredicateBuilder implements PredicateBuilder {

    private final static PathPatternParser pathPatternParser= new PathPatternParser();

    @Override
    public Predicate<ServerWebExchange> apply(PredicateDef predicateDef) {

        // Declare a list to hold the parsed path patterns.
        final ArrayList<PathPattern> pathPatterns = new ArrayList<>();

        //  Parse Each Path Defined in config and add it to parsed path's list.
        synchronized (pathPatternParser) {
            for(String pattern: predicateDef.getArgs()) {
                PathPattern pathPattern = pathPatternParser.parse(pattern);
                pathPatterns.add(pathPattern);
            }
        }

        return new Predicate<ServerWebExchange>() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {

                // Fetch Current Path Either From ServerWebExchange or Compute it.
                PathContainer path = (PathContainer) serverWebExchange.getAttributes().computeIfAbsent(
                        GatewayConstants.PATH_CONTAINER_ATTR,
                        s -> PathContainer.parsePath(serverWebExchange.getRequest().getURI().getRawPath()));

                PathPattern match = null;

                // Try to match path against each compiled pattern.
                for (PathPattern pathPattern : pathPatterns) {
                    if (pathPattern.matches(path)) {
                        match = pathPattern;
                        break;
                    }
                }

                if (match != null) {
                    PathPattern.PathMatchInfo pathMatchInfo = match.matchAndExtract(path);
                    ServerWebExchangePropertiesUtils.putUriTemplateVariables(serverWebExchange, pathMatchInfo.getUriVariables());
                    serverWebExchange.getAttributes().put(ServerWebExchangePropertiesUtils.MATCHED_PATH_ATTR, match.getPatternString());
                    return true;
                }
                else {
                    return false;
                }

            }
        };
    }
}
