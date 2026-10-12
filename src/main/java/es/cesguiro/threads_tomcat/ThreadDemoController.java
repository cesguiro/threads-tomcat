package es.cesguiro.threads_tomcat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThreadDemoController {

    @GetMapping("/api/threads")
    public ResponseEntity<ThreadResponse> inspectStaticUser(
            @RequestParam(required = false) String user) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        if (user != null) {
            SharedUser.setUser(user);
        }

        Thread.sleep(1000);
        String currentUser = SharedUser.getUser();
        return ResponseEntity.ok(new ThreadResponse(threadName, currentUser, currentUser != null));
    }

    @GetMapping("/api/threads/thread-local")
    public ResponseEntity<ThreadResponse> inspectThreadLocalUser(
            @RequestParam(required = false) String user) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        if (user != null) {
            ThreadLocalUser.setUser(user);
        }

        Thread.sleep(1000);
        String currentUser = ThreadLocalUser.getUser();
        return ResponseEntity.ok(new ThreadResponse(threadName, currentUser, currentUser != null));
    }

    @GetMapping("/api/threads/thread-local-clean")
    public ResponseEntity<ThreadResponse> inspectThreadLocalUserWithCleanup(
            @RequestParam(required = false) String user) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        if (user != null) {
            ThreadLocalUser.setUser(user);
        }

        try {
            Thread.sleep(1000);
            String currentUser = ThreadLocalUser.getUser();
            return ResponseEntity.ok(new ThreadResponse(threadName, currentUser, currentUser != null));
        } finally {
            ThreadLocalUser.clearUser();
        }
    }

    public record ThreadResponse(String threadName, String user, boolean loggedIn) {
    }
}
