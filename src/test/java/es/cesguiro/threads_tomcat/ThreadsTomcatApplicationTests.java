package es.cesguiro.threads_tomcat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ThreadsTomcatApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void clearUserState() {
		SharedUser.setUser(null);
		ThreadLocalUser.clearUser();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void staticUserIsSharedAcrossRequests() throws Exception {
		mockMvc.perform(get("/api/threads").param("user", "Ana"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.threadName").isNotEmpty())
				.andExpect(jsonPath("$.user").value("Ana"))
				.andExpect(jsonPath("$.loggedIn").value(true));

		mockMvc.perform(get("/api/threads"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user").value("Ana"))
				.andExpect(jsonPath("$.loggedIn").value(true));
	}

	@Test
	void threadLocalUserRemainsOnReusedThreadWhenNotCleared() throws Exception {
		mockMvc.perform(get("/api/threads/thread-local").param("user", "Ana"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user").value("Ana"));

		mockMvc.perform(get("/api/threads/thread-local"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user").value("Ana"));
	}

	@Test
	void threadLocalUserIsClearedAfterRequest() throws Exception {
		mockMvc.perform(get("/api/threads/thread-local-clean").param("user", "Ana"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user").value("Ana"));

		org.junit.jupiter.api.Assertions.assertNull(ThreadLocalUser.getUser());
	}

}
