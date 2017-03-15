package com.funcxy.oj.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funcxy.oj.Application;
import com.funcxy.oj.contents.Passport;
import com.funcxy.oj.models.User;
import com.funcxy.oj.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by DDHEE on 2017/3/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@SpringBootTest(classes = Application.class)
public class UserSignTest {
    String usernameValid = "zccz13";
    String usernameValid1 = "zzcz13";
    String usernameDuplicated = "  z ccz 1  3 ";
    String usernameEmpty = "      ";
    String emailValid = "hell@yeahfuncxy.net";
    String emailValid1 = "hello@yeahfuncxy.com";
    String emailDuplicated = "  h ell@yeah    func   xy.net";
    String emailEmpty = "     ";
    String emailInvalid = "asfdjklas@.com";
    String passwordValid = "abc6789067890";
    String passwordEmpty = "";
    String passwordInvalid = "243";

    @Autowired
    private UserRepository userRepository;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private HashMap<String, Object> sessionAttr;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    private Passport testUserPassport = new Passport();

    @Before
    public void validUser () throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();//加载上下文
        testUserPassport.username = usernameValid;
        testUserPassport.email = emailValid;
        testUserPassport.password = passwordValid;
    }

    @Test
    public void signUpTestNormal() throws Exception {
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void signUpTestEmptyUsername() throws Exception {
        testUserPassport.username = usernameEmpty;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpTestDuplicatedUsername() throws Exception {
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        testUserPassport.username = usernameDuplicated;
        testUserPassport.email = emailValid1;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpTestEmptyEmail() throws Exception {
        testUserPassport.email = emailEmpty;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpTestInvalidEmail() throws Exception {
        testUserPassport.email = emailInvalid;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpTestDuplicatedEmail() throws Exception {
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        testUserPassport.username = usernameValid1;
        testUserPassport.email = emailDuplicated;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpTestEmptyPassword() throws Exception {
        testUserPassport.password = passwordEmpty;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpTestInvalidPassword() throws Exception {
        testUserPassport.password = passwordInvalid;
        mockMvc.perform(post("/users/sign-up")
                .content(this.json(testUserPassport))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @After
    public void clear(){//去除加入的数据
        userRepository.deleteAll();
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}