package com.hefest.app.security;

import java.sql.Date;

import javax.validation.constraints.Size;

import org.joda.time.LocalDate;


/**
 * Persistent tokens are used by Spring Security to automatically log in users.
 *
 * @see com.geo.ads.security.CustomPersistentRememberMeServices
 */

public class PersistentToken {
    
    private static final int MAX_USER_AGENT_LEN = 255;

    private String series;

    private String value;

    private LocalDate localDate;
    
    private Date date;

    @Size(min = 0, max = 39)
    private String ipAddress;

    private String userAgent;

	private String email;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.localDate = new LocalDate(date);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        if (userAgent.length() >= MAX_USER_AGENT_LEN) {
            this.userAgent = userAgent.substring(0, MAX_USER_AGENT_LEN - 1);
        } else {
            this.userAgent = userAgent;
        }
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
		this.date = new java.sql.Date(localDate.toDate().getTime());
	}
}
