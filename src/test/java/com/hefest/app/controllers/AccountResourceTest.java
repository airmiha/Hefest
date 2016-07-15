package com.hefest.app.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hefest.app.dao.ProfessionalDAO;
import com.hefest.app.dao.UserDAO;
import com.hefest.app.model.User;
import com.hefest.app.resource.AccountResource;
import com.hefest.app.security.AuthoritiesConstants;
import com.hefest.app.service.MailService;
import com.hefest.app.service.SecurityService;
import com.hefest.app.utils.Utility;

public class AccountResourceTest {

	private static final String NAME = "User";
	private static final String EMAIL = "email@mail.com";
	private static final String PASSWORD = "password";
	private static final String ACTIVATIONKEY = "activationKey";
	private static final String RESETEMAILCONTENT = "content";
	private static final String TOKEN = "token";
	
	@Mock
    private UserDAO userDAO;
	
	@Mock
    private ProfessionalDAO professionalDAO;
	
	@Mock
    private MailService mailService;
	
	@Mock
    private SecurityService securityService;

	@InjectMocks
    private AccountResource accountResource;
	
    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AccountResource accountResource = new AccountResource();
        ReflectionTestUtils.setField(accountResource, "userDAO", userDAO);
        ReflectionTestUtils.setField(accountResource, "professionalDAO", professionalDAO);
        ReflectionTestUtils.setField(accountResource, "mailService", mailService);
        ReflectionTestUtils.setField(accountResource, "securityService", securityService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
    }
    
    @Test
    public void getAccount_NonExistingUser_ShouldReturnInternalServerErrorStatusCode() throws Exception {
    	when(userDAO.getUserByEmail(any(String.class))).thenReturn(null);
        mockMvc.perform(get("/resources/account/current")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());             
    }
    
    @Test
    public void getAccount_ExistingUser_ShouldReturnUserInformation() throws Exception {
    	User user = new User();
    	user.setName(NAME);
    	user.setEmail(EMAIL);
    	user.setRole(AuthoritiesConstants.PROFESSIONAL);
    	when(securityService.getCurrentLogin()).thenReturn(EMAIL);
    	when(userDAO.getUserByEmail(EMAIL)).thenReturn(user);
        mockMvc.perform(get("/resources/account/current")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value(AuthoritiesConstants.PROFESSIONAL));
    }
	
    @Test
    public void activateAccount_NonExistingUser_ShouldReturnInternalServerErrorStatusCode() throws Exception {
    	when(userDAO.getUserByEmail(any(String.class))).thenReturn(null);
        mockMvc.perform(get("/resources/account/current")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());             
    }
    
    @Test
    public void activateAccount_NoKey_ShouldReturnBadRequest() throws Exception {
    	when(userDAO.getUserByEmail(any(String.class))).thenThrow(new RuntimeException());    	
        mockMvc.perform(get("/resources/account/activate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void activateAccount_SuccessfulActivation_ShouldReturnOkStatus() throws Exception {
    	when(userDAO.activateRegistration(ACTIVATIONKEY)).thenReturn(new User());    	
        mockMvc.perform(get("/resources/account/activate?key=" + ACTIVATIONKEY)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    public void forgotPassword_EmptyEmail_ShouldReturnMethodNotAllowed() throws Exception {    	
        mockMvc.perform(post("/resources/account/forgotpassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(""))
                .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    public void forgotPassword_UserNotFound_ShouldReturnInternalServerError() throws Exception {    
    	when(userDAO.getUserByEmail(EMAIL)).thenReturn(null);   	
        mockMvc.perform(post("/resources/account/forgotpassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(EMAIL)))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    public void forgotPassword_ValidUser_ShouldGenerateResetPasswordTokenForUserAndSendEmail() throws Exception {    	
    	User user = new User();
    	user.setEmail(EMAIL);
    	when(userDAO.getUserByEmail(EMAIL)).thenReturn(user);    	
        mockMvc.perform(post("/resources/account/forgotpassword")
        		.content(EMAIL));
        verify(userDAO).generateResetPasswordTokenForUser(user);
    }
    
    @Test
    public void forgotPassword_ValidUser_ShouldSendResetEmail() throws Exception {    	
    	User user = new User();
    	user.setEmail(EMAIL);
    	when(mailService.createHtmlContentFromTemplate(eq(MailService.EMAIL_RESETPASSWORD_PREFIX), eq(user), any(Locale.class), any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(RESETEMAILCONTENT);
    	when(userDAO.getUserByEmail(EMAIL)).thenReturn(user);    	
        mockMvc.perform(post("/resources/account/forgotpassword")
        		.content(EMAIL))
                .andExpect(status().isOk());
        verify(mailService).sendResetPasswordEmail(eq(EMAIL), eq(RESETEMAILCONTENT), any(Locale.class));
    }
    
    @Test(expected=Exception.class)
    public void forgotPassword_EmailCouldNotBeSent_ReturnInternalServerError() throws Exception {    	
    	User user = new User();
    	user.setEmail(EMAIL);
    	when(userDAO.getUserByEmail(EMAIL)).thenReturn(user);   
    	when(mailService.createHtmlContentFromTemplate(eq(MailService.EMAIL_RESETPASSWORD_PREFIX), eq(user), any(Locale.class), any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(RESETEMAILCONTENT);
    	doThrow(new Exception()).when(mailService).sendResetPasswordEmail(eq(EMAIL), eq(RESETEMAILCONTENT), any(Locale.class));
    	mockMvc.perform(post("/resources/account/forgotpassword")
        		.content(EMAIL))
                .andExpect(status().isInternalServerError());        
    }
    
    @Test
    public void resetPassword_EmptyPassword_ShouldReturnForbidden() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("token", TOKEN);
        mockMvc.perform(post("/resources/account/resetpassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isForbidden());
        verify(userDAO, never()).resetPassword(any(String.class), any(String.class));
    }
    
    @Test
    public void resetPassword_EmptyToken_ShouldReturnForbidden() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("password", PASSWORD);
        mockMvc.perform(post("/resources/account/resetpassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isForbidden());
        verify(userDAO, never()).resetPassword(any(String.class), any(String.class));
    }
    
    @Test
    public void resetPassword_TokenExpired_ShouldReturnForbidden() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("password", PASSWORD);
    	parameters.put("token", TOKEN);
    	when(userDAO.resetPassword(TOKEN, PASSWORD)).thenReturn(null);
        mockMvc.perform(post("/resources/account/resetpassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isForbidden());
    }
    
    @Test
    public void resetPassword_OnSuccess_ShouldReturnStatusOk() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("password", PASSWORD);
    	parameters.put("token", TOKEN);
    	when(userDAO.resetPassword(TOKEN, PASSWORD)).thenReturn(new User());
        mockMvc.perform(post("/resources/account/resetpassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isOk());
    }
    
    @Test
    public void changePassword_EmptyOldPassword_ShouldReturnForbidden() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("newPassword", PASSWORD);
        mockMvc.perform(post("/resources/account/changepassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isForbidden());
        verify(userDAO, never()).changePassword(any(String.class));
    }
    
    @Test
    public void changePassword_EmptyNewPassword_ShouldReturnForbidden() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("oldPassword", PASSWORD);
        mockMvc.perform(post("/resources/account/changepassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isForbidden());
        verify(userDAO, never()).changePassword(any(String.class));
    }
    
    @Test
    public void changePassword_PasswordsDoNotMatch_ShouldReturnForbidden() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("newPassword", PASSWORD);
    	parameters.put("oldPassword", PASSWORD);
    	when(userDAO.isPasswordValid(PASSWORD)).thenReturn(false);
        mockMvc.perform(post("/resources/account/changepassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isForbidden());
        verify(userDAO, never()).changePassword(any(String.class));
    }
    
    @Test
    public void changePassword_PasswordsMatch_ShouldChangePasswordAndReturnOk() throws Exception { 
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("newPassword", PASSWORD);
    	parameters.put("oldPassword", PASSWORD);
    	when(userDAO.isPasswordValid(PASSWORD)).thenReturn(true);
        mockMvc.perform(post("/resources/account/changepassword")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(Utility.convertObjectToJSON(parameters)))
        		.andExpect(status().isOk());
        verify(userDAO).changePassword(PASSWORD);
    }
}
