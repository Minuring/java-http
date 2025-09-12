package com.techcourse.controller;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.io.StaticResource;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse doGet(final HttpRequest request) {
        final var session = findOrCreateSession(request);
        if (session.getAttribute("user") != null) {
            return HttpResponse.found()
                    .location("/index.html")
                    .build();
        }

        final var body = new StaticResource("login.html").readAsString();
        return HttpResponse.ok()
                .contentType("text/html;charset=utf-8")
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final var formData = Arrays.stream(request.body().split("&"))
                .map(s -> s.split("="))
                .collect(toMap(kv -> kv[0], kv -> kv[1]));
        final var loggedInUser = login(formData.get("account"), formData.get("password"));

        return Optional.ofNullable(loggedInUser)
                .map(user -> HttpResponse.found()
                            .location("/index.html")
                            .addCookie("jsessionid", saveSessionAndGetId(request, loggedInUser))
                            .build()
                )
                .orElse(HttpResponse.found()
                        .location("/401.html")
                        .build());
    }

    private String saveSessionAndGetId(final HttpRequest request, final User loggedInUser) {
        final var session = findOrCreateSession(request);
        session.setAttribute("user", loggedInUser);
        SessionManager.INSTANCE.add(session);
        return session.getId();
    }

    private HttpSession findOrCreateSession(final HttpRequest request) {
        final var sessionId = request.getSessionId();
        if (sessionId == null) {
            return new Session();
        }

        final var session = SessionManager.INSTANCE.findSession(sessionId);
        if (session != null) {
            return session;
        }
        return new Session();
    }

    private User login(final String account, final String password) {
        final var optionalUser = InMemoryUserRepository.findByAccount(account);
        final var isLoggedIn = optionalUser
                .map(u -> u.checkPassword(password))
                .orElse(false);

        if (isLoggedIn) {
            final var user = optionalUser.get();
            log.info("user : {}", user);
            return user;
        }
        return null;
    }
}
