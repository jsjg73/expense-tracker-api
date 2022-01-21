package com.pairlearning.expensetracker;

import com.pairlearning.expensetracker.resources.UserResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseTrackerApiApplicationTests {

	@Autowired
	public MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
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
}
