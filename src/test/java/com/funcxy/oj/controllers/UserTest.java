package com.funcxy.oj.controllers;

import com.funcxy.oj.Application;
import com.funcxy.oj.models.User;
import com.funcxy.oj.services.UserService;
import com.funcxy.oj.utils.InvalidException;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by aak12 on 2017/3/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest{
    @Autowired
    UserService userService;

    private String rightUsername = new String("aak1247");
    private String rightPassword = new String("abc123456");
    private String rightEmail = new String("aak1247@126.com");

    private String rightUsername2 = new String("zccz14");
    private String rightEmail2 = new String("123456@123.com");

    //Username test case
    private String simpleUsername = new String("123456");
    private String shortUsername = new String("A");

    //password test case
    private String shortPassword = new String("a1");
    private String simplePassword = new String("123456");

    //email test case
    private String invalidEmail1 = new String("ABCDEFG");
    private String invalidEmail2 = new String("abc@d");
    private String invalidEmail3 = new String("abc.d");
    private String invalidEmail4 = new String("abc@123.4");

    @Test//right input
    public void createUser() throws InvalidException{
        User user = new User();
        user.setUsername(rightUsername);
        user.setEmail(rightEmail);
        user.setPassword(rightPassword);
        userService.save(user);
    }
    @Test(expected = InvalidException.class)//no username and password
    public void signUpTest1()throws InvalidException{
        User user = new User();
        user.setId(ObjectId.get());
        userService.save(user);
    }
    @Test(expected = InvalidException.class)//no password
    public void signUpTest2()throws InvalidException{
        User user = new User();
        user.setId(ObjectId.get());
        user.setUsername(rightUsername);
        user.setEmail(rightEmail);
        userService.save(user);
    }
    @Test(expected = InvalidException.class)//no email
    public void signUpTest3() throws InvalidException{
        User user = new User();
        user.setId(ObjectId.get());
        user.setUsername(rightUsername);
        user.setPassword(rightPassword);
        userService.save(user);
    }
    //duplicate username

    //duplicate email

    //short username

    //short password

    //simple username

    //simple password





}
