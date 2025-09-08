package com.techcourse.handler;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.io.StaticResource;
import org.apache.coyote.message.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    public String handle(final HttpRequest request) throws IOException {
        final var session = request.getSession();
        if (session.getAttribute("user") != null) {
            return "HTTP/1.1 302 Found\r\nLocation: /index.html";
        }

        // 계정, 비밀번호를 입력한 경우 로그인 시도
        final var httpMethod = request.startLine().httpMethod();
        if (httpMethod.isPost()) {
            final var formData = Arrays.stream(request.body().split("&"))
                    .map(s -> s.split("="))
                    .collect(toMap(kv -> kv[0], kv -> kv[1]));
            final var loggedInUser = login(formData.get("account"), formData.get("password"));

            // 로그인 성공한 경우 302 -> index.html 리다이렉트
            if (loggedInUser != null) {
                session.setAttribute("user", loggedInUser);
                SessionManager.INSTANCE.add(session);
                return "HTTP/1.1 302 Found\r\nLocation: /index.html \r\n"
                        + "Set-Cookie: JSESSIONID=" + session.getId();
            }

            // 로그인 실패한 경우 302 -> 401.html 리다이렉트
            return "HTTP/1.1 302 Found\r\nLocation: /401.html";
        }

        // 계정, 비밀번호를 입력하지 않은 경우 로그인 화면
        final var body = new StaticResource("login.html").readAsString();
        return String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body
        );
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
