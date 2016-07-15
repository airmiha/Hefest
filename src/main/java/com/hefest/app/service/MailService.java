package com.hefest.app.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import com.hefest.app.model.User;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    public static final String TEMPLATE_SUFFIX = "Email";
    public static final String EMAIL_ACTIVATION_PREFIX = "activation";
    public static final String EMAIL_RESETPASSWORD_PREFIX = "resetPassword";

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private Environment env;
    
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
    private ServletContext servletContext;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private MessageSource messageSource;
    
	@Autowired
	private SpringTemplateEngine templateEngine;
	
    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
    }
	
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'!", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendActivationEmail(final String email, String content, Locale locale) {
        log.debug("Sending activation e-mail to '{}'", email);
        String subject = messageSource.getMessage("email.activation.title", null, locale);        
    	sendEmail(email, subject, content, false, true);
    }
    
    public void sendResetPasswordEmail(final String email, String content, Locale locale) throws MailException, MessagingException {
        log.debug("Sending reset password e-mail to '{}'", email);
        final String subject = "email.resetpassword.title";
        sendEmail(email, subject, content, false, true);
    }
    
    public String createHtmlContentFromTemplate(final String emailPrefix, final User user, final Locale locale, final HttpServletRequest request,
            final HttpServletResponse response) {
 		Map<String, Object> variables = new HashMap<String, Object>();
 		variables.put("user", user);
 		variables.put("baseUrl", request.getScheme() + "://" + request.getServerName() +    
 		":" + request.getServerPort() + "/geoads");
 		IWebContext context = new SpringWebContext(request, response, servletContext, locale, variables, applicationContext);
 		return templateEngine.process(emailPrefix + MailService.TEMPLATE_SUFFIX, context);
    }
}
