package com.pairlearning.expensetracker;

import com.pairlearning.expensetracker.exceptions.EtAuthException;
import com.pairlearning.expensetracker.resources.UserResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.xml.transform.Result;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseTrackerApiApplicationTests {

	@Autowired
	public MockMvc mockMvc;

	@Test
	@Order(3)
	@DisplayName("유저 등록 실패 테스트(잘못된 이메일 형식)")
	public void registerUseFailWrongEmailFormat() throws Exception {
		ResultActions result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/users/register")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content("{\"firstName\": \"David\"," +
								" \"lastName\" : \"Smith\"," +
								"\"email\" : \"davidtestmail.com\"," +
								"\"password\" : \"test123\"}")
		);

		result.andDo(print())
				.andExpect(status().is4xxClientError())
				.andExpect(handler().handlerType(UserResource.class))
				.andExpect(handler().methodName("registerUser"))
				.andExpect(re -> assertTrue(re.getResolvedException() instanceof EtAuthException))
				.andExpect(re ->
						assertTrue(
								"Invalid email format".equals(
										re.getResolvedException().getMessage()
								)
						));
	}
	@Test
	@Order(1)
	@DisplayName("유저 등록 성공 테스트")
	public void registerUser() throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders
				.post("/api/users/register")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content("{\"firstName\": \"David\"," +
							" \"lastName\" : \"Smith\"," +
							"\"email\" : \"david@testmail.com\"," +
							"\"password\" : \"test123\"}")
		);

		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserResource.class))
			.andExpect(handler().methodName("registerUser"))
			.andExpect(jsonPath("$.message", is("registered successfully")));
	}

	@Test
	@Order(2)
	@DisplayName("유저 등록 실패 테스트(중복 이메일)")
	public void registerUseFailDuplicatedEmail() throws Exception {
		ResultActions result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/users/register")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content("{\"firstName\": \"David\"," +
								" \"lastName\" : \"Smith\"," +
								"\"email\" : \"david@testmail.com\"," +
								"\"password\" : \"test123\"}")
		);

		result.andDo(print())
				.andExpect(status().is4xxClientError())
				.andExpect(handler().handlerType(UserResource.class))
				.andExpect(handler().methodName("registerUser"))
				.andExpect(re -> assertTrue(re.getResolvedException() instanceof EtAuthException))
				.andExpect(re ->
						assertTrue(
								"Email already in use".equals(
										re.getResolvedException().getMessage()
								)
						));
	}

	@Test
	@Order(4)
	@DisplayName("로그인 실패(패스워드 불일치)")
	public void loginUserFailWrongPassword() throws Exception {
		RequestBuilder request =
				MockMvcRequestBuilders
					.post("/api/users/login")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content("{\"email\": \"david@testmail.com\",\"password\":\"wrongpassword\"}");

		ResultActions result = mockMvc.perform(request);

		result.andDo(print())
				.andExpect(status().is4xxClientError())
				.andExpect(handler().handlerType(UserResource.class))
				.andExpect(handler().methodName("loginUser"))
				.andExpect(re->assertTrue(re.getResolvedException() instanceof EtAuthException))
				.andExpect(re->assertEquals(re.getResolvedException().getMessage(), "Wrong password"));
	}

	@Test
	@Order(5)
	@DisplayName("로그인 성공")
	public void longinUser() throws Exception {
		ResultActions result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/users/login")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"david@testmail.com\",\"password\":\"test123\"}")
		);

		result.andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(UserResource.class))
				.andExpect(handler().methodName("loginUser"))
				.andExpect(jsonPath("$.message", is("loggedIn successfully")));
	}
}
