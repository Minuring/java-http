package org.apache.coyote.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class StaticResource {

    private static final String STATIC_PATH = "static";
    private static final char DOT = '.';

    private static final Set<String> EXTENSIONS = Set.of(
            ".html", ".css", ".js", ".png", ".jpg", ".svg"
    );

    private final URL resource;

    public StaticResource(final String resourceName) throws FileNotFoundException {
        final var isStatic = EXTENSIONS.stream().anyMatch(resourceName::endsWith);
        if (!isStatic) {
            throw new IllegalArgumentException("Resource name '" + resourceName + "' is not a static resource");
        }

        resource = getResource(resourceName);
    }

    private URL getResource(final String resourceName) throws FileNotFoundException {
        final var classLoader = getClass().getClassLoader();
        final var adjustedName = adjustName(resourceName);
        final var resource = classLoader.getResource(STATIC_PATH + adjustedName);
        if (resource == null) {
            throw new FileNotFoundException("Resource not found : " + resourceName);
        }

        return resource;
    }

    public String readAsString() {
        try (InputStream in = resource.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExtension() {
        final String path = resource.getPath();
        if (path.indexOf(DOT) == -1) {
            return "";
        }

        return path.substring(path.lastIndexOf(DOT) + 1);
    }

    private String adjustName(final String resourceName) {
        if (resourceName.startsWith("/")) {
            return resourceName;
        }
        return "/" + resourceName;
    }
}
