package io.github.TaigaKudo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.TaigaKudo.dto.EmailChangeRequest;
import io.github.TaigaKudo.dto.PasswordChangeRequest;
import io.github.TaigaKudo.dto.UserMeResponse;
import io.github.TaigaKudo.dto.UserUpdateRequest;
import io.github.TaigaKudo.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController の単体テスト")
class UserControllerTest {

	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private UserService userService;

	@BeforeEach
	void setup() {
		UserController controller = new UserController(userService);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	@Test
	@DisplayName("GET /users/me はユーザー情報を返す")
	void me_returnsUserInfo() throws Exception {
		UserMeResponse resp = new UserMeResponse(1L, "Taro", "taro@example.com");
		when(userService.getMe(1L)).thenReturn(resp);

		mockMvc.perform(get("/users/me").principal(new TestingAuthenticationToken(1L, null)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT /users/me は更新後のユーザー情報を返す")
	void updateMe_returnsUpdated() throws Exception {
		UserUpdateRequest req = new UserUpdateRequest("NewName");
		UserMeResponse resp = new UserMeResponse(1L, "NewName", "taro@example.com");
		when(userService.updateMe(1L, req)).thenReturn(resp);

		mockMvc.perform(put("/users/me").principal(new TestingAuthenticationToken(1L, null))
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(req)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT /users/me/password は204を返す")
	void changePassword_returnsNoContent() throws Exception {
		PasswordChangeRequest req = new PasswordChangeRequest("old", "new");
		doNothing().when(userService).changePassword(1L, req);

		mockMvc.perform(put("/users/me/password").principal(new TestingAuthenticationToken(1L, null))
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(req)))
				.andExpect(status().isNoContent());

		verify(userService).changePassword(1L, req);
	}

	@Test
	@DisplayName("PUT /users/me/email は204を返す")
	void changeEmail_returnsNoContent() throws Exception {
		EmailChangeRequest req = new EmailChangeRequest("new@example.com", "password");
		doNothing().when(userService).changeEmail(1L, req);

		mockMvc.perform(put("/users/me/email").principal(new TestingAuthenticationToken(1L, null))
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(req)))
				.andExpect(status().isNoContent());

		verify(userService).changeEmail(1L, req);
	}

	@Test
	@DisplayName("DELETE /users/me は204を返す")
	void deleteMe_returnsNoContent() throws Exception {
		doNothing().when(userService).deleteMe(1L);

		mockMvc.perform(delete("/users/me").principal(new TestingAuthenticationToken(1L, null)))
				.andExpect(status().isNoContent());

		verify(userService).deleteMe(1L);
	}

}
