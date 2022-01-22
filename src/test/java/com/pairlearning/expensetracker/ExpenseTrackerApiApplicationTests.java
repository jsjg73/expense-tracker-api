package com.pairlearning.expensetracker;

import com.jayway.jsonpath.JsonPath;
import com.pairlearning.expensetracker.exceptions.EtAuthException;
import com.pairlearning.expensetracker.exceptions.EtBadRequestException;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;
import com.pairlearning.expensetracker.resources.CategoryResource;
import com.pairlearning.expensetracker.resources.TransactionsResource;
import com.pairlearning.expensetracker.resources.UserResource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExpenseTrackerApiApplicationTests {
	static String token;

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
		ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders
				.post("/api/users/register")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content("{\"firstName\": \"David\"," +
							" \"lastName\" : \"Smith\"," +
							"\"email\" : \"david@testmail.com\"," +
							"\"password\" : \"test123\"}")
		);
		MvcResult result = resultActions.andDo(print())
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserResource.class))
			.andExpect(handler().methodName("registerUser"))
			.andReturn();

		token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
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
				.andExpect(handler().methodName("loginUser"));
	}

	@Test
	@Order(6)
	@DisplayName("카테고리 조회 실패(토큰 누락)")
	public void getAllCategoriesFail1() throws Exception {
		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories")
						.accept(MediaType.APPLICATION_JSON)

		);
		MvcResult mvcResult = resultActions.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertEquals(mvcResult.getResponse().getErrorMessage(), "Authorization token must be provided");
	}
	@Test
	@Order(7)
	@DisplayName("카테고리 조회 실패(Bearer 누락)")
	public void getAllCategoriesFail2() throws Exception {
		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", token)

		);
		MvcResult mvcResult = resultActions.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertEquals(mvcResult.getResponse().getErrorMessage(), "Authorization token must be Bearer [token]");
	}

	@Test
	@Order(8)
	@DisplayName("카테고리 생성 성공")
	public void addCategory() throws Exception {
		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+token)
						.content("{\"title\":\"Shopping\", \"description\": \"this is for recording all my shopping transactions\"}")

		);
		resultActions.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(handler().handlerType(CategoryResource.class))
				.andExpect(handler().methodName("addCategory"))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.title", is("Shopping")))
				.andExpect(jsonPath("$.description", is("this is for recording all my shopping transactions")))
				.andExpect(jsonPath("$.totalExpense", is(0.0)));

	}

	@Test
	@Order(9)
	@DisplayName("카테고리 전체 조회 성공")
	public void getAllCategories() throws Exception {
		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+token)
						.content("{\"title\":\"Other shopping\", \"description\": \"second shopping\"}")

		);
		resultActions.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(handler().handlerType(CategoryResource.class))
				.andExpect(handler().methodName("addCategory"))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.categoryId", is(2)))
				.andExpect(jsonPath("$.title", is("Other shopping")))
				.andExpect(jsonPath("$.description", is("second shopping")))
				.andExpect(jsonPath("$.totalExpense", is(0.0)));

		resultActions = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+token)

		);
		resultActions.andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(CategoryResource.class))
				.andExpect(handler().methodName("getAllCategories"))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].categoryId", is(1)))
				.andExpect(jsonPath("$[0].userId", is(1)))
				.andExpect(jsonPath("$[0].title", is("Shopping")))
				.andExpect(jsonPath("$[0].description", is("this is for recording all my shopping transactions")))
				.andExpect(jsonPath("$[1].categoryId", is(2)))
				.andExpect(jsonPath("$[1].userId", is(1)))
				.andExpect(jsonPath("$[1].title", is("Other shopping")))
				.andExpect(jsonPath("$[1].description", is("second shopping")));
	}

	@Test
	@Order(10)
	@DisplayName("카테고리 단일 조회")
	public void getCategoryByIDSuccess() throws Exception {

		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories/1")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+token)
		);
		resultActions.andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(CategoryResource.class))
				.andExpect(handler().methodName("getCategoryById"))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.title", is("Shopping")))
				.andExpect(jsonPath("$.description", is("this is for recording all my shopping transactions")))
				.andExpect(jsonPath("$.totalExpense", is(0.0)));
	}

	@Test
	@Order(11)
	@DisplayName("카테고리 수정 실패(잘못된 카테고리 ID)")
	public void updateCategoryFail1() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.put("/api/categories/3")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"update title\", \"description\": \"update description\"}")
		).andExpect(status().is4xxClientError())
				.andExpect(re->assertTrue(re.getResolvedException() instanceof EtBadRequestException))
				.andExpect(re->assertTrue(re.getResolvedException().getMessage().equals("invalid request")));
	}

	@Test
	@Order(12)
	@DisplayName("카테고리 수정 성공")
	public void updateCategorySuccess() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.put("/api/categories/1")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"update title\", \"description\": \"update description\"}")
		).andExpect(status().isOk())
		.andExpect(handler().handlerType(CategoryResource.class))
		.andExpect(handler().methodName("updateCategory"))
		.andExpect(jsonPath("$.success", is(true)));

		mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories/1")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andExpect(handler().handlerType(CategoryResource.class))
				.andExpect(handler().methodName("getCategoryById"))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.title", is("update title")))
				.andExpect(jsonPath("$.description", is("update description")))
				.andExpect(jsonPath("$.totalExpense", is(0.0)));
	}

	@Test
	@Order(13)
	@DisplayName("트랜잭션 생성 성공")
	public void createTransactionSuccess() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/categories/1/transactions")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": 10000, \"note\":\"shopping for new year\", \"transactionDate\": 1577817000000}")
		).andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(TransactionsResource.class))
				.andExpect(handler().methodName("createTransaction"))
				.andExpect(jsonPath("$.transactionId", is(1000)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.amount", is(10000.0)))
				.andExpect(jsonPath("$.note", is("shopping for new year")))
				.andExpect(jsonPath("$.transactionDate", is(1577817000000L)));

	}
	@Test
	@Order(14)
	@DisplayName("트랜잭션 전체 조회 성공")
	public void getAllTransactionsSuccess() throws Exception {

		mockMvc.perform(
						MockMvcRequestBuilders
								.post("/api/categories/1/transactions")
								.header("Authorization", "Bearer "+token)
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"amount\": 10000, \"note\":\"shopping for new year\", \"transactionDate\": 1577817000000}")
				).andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(TransactionsResource.class))
				.andExpect(handler().methodName("createTransaction"))
				.andExpect(jsonPath("$.transactionId", is(1001)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.amount", is(10000.0)))
				.andExpect(jsonPath("$.note", is("shopping for new year")))
				.andExpect(jsonPath("$.transactionDate", is(1577817000000L)));

		mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories/1/transactions")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
		).andDo(print())
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(TransactionsResource.class))
			.andExpect(handler().methodName("getAllTransactions"))
			.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].transactionId",  is(1000)))
				.andExpect(jsonPath("$[1].transactionId",  is(1001)));

	}
	@Test
	@Order(15)
	@DisplayName("트랜잭션 단일 조회 성공")
	public void getTransactionSuccess() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/categories/1/transactions/1000")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
		).andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(TransactionsResource.class))
				.andExpect(handler().methodName("getTransactionById"))
				.andExpect(jsonPath("$.transactionId", is(1000)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.amount", is(10000.0)))
				.andExpect(jsonPath("$.note", is("shopping for new year")))
				.andExpect(jsonPath("$.transactionDate", is(1577817000000L)));
	}
	@Test
	@Order(16)
	@DisplayName("트랜잭션 수정 성공")
	public void updateTransactionSuccess() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.put("/api/categories/1/transactions/1000")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": 15000, \"note\":\"update note\", \"transactionDate\":\"1577817000000\"}")
		).andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(TransactionsResource.class))
				.andExpect(handler().methodName("updateTransaction"))
				.andExpect(jsonPath("$.success", is(true)));

		mockMvc.perform(
						MockMvcRequestBuilders
								.get("/api/categories/1/transactions/1000")
								.header("Authorization", "Bearer "+token)
								.accept(MediaType.APPLICATION_JSON)
				).andDo(print())
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(TransactionsResource.class))
				.andExpect(handler().methodName("getTransactionById"))
				.andExpect(jsonPath("$.transactionId", is(1000)))
				.andExpect(jsonPath("$.categoryId", is(1)))
				.andExpect(jsonPath("$.userId", is(1)))
				.andExpect(jsonPath("$.amount", is(15000.0)))
				.andExpect(jsonPath("$.note", is("update note")))
				.andExpect(jsonPath("$.transactionDate", is(1577817000000L)));
	}
//	@Test
	@Order(17)
	@DisplayName("트랜잭션 삭제 성공")
	public void deleteTransactionSuccess() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.delete("/api/categories/1/transactions/1000")
						.header("Authorization", "Bearer "+token)
						.accept(MediaType.APPLICATION_JSON)
		);
		//TODO
		fail();
	}
}
