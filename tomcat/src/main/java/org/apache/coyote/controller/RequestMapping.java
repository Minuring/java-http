package org.apache.coyote.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.coyote.message.HttpRequest;

public class RequestMapping {

    private final Map<Predicate<HttpRequest>, Controller> mappings = new LinkedHashMap<>();
    private final Controller notFoundController;

    public RequestMapping(final Controller notFoundController) {
        this.notFoundController = notFoundController;
    }

    public void add(final Predicate<HttpRequest> predicate, final Controller controller) {
        mappings.putIfAbsent(predicate, controller);
    }

    public Controller getController(final HttpRequest request) {
        return mappings.keySet().stream()
                .filter(p -> p.test(request))
                .map(mappings::get)
                .findFirst()
                .orElse(notFoundController);
    }
}
