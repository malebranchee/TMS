package com.example.tms;

import com.example.tms.controllers.AdminController;
import com.example.tms.controllers.UserController;
import com.example.tms.utils.JwtUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = {AdminController.class, UserController.class})
public class TaskControllersTests {



}
