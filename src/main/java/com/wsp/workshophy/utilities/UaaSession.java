package com.wsp.workshophy.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wsp.workshophy.constant.CoreMessageCode;
import com.wsp.workshophy.exception.AuthorizationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Getter
@Setter
@ToString
@Component
@RequestScope
public class UaaSession {
    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Autowired
    private HttpServletRequest request;

    private String headerAuthorization;
    private Long userId;
    private String username;
    private List<String> roles = new ArrayList<>();

    @PostConstruct
    public void initialize() throws JsonProcessingException {
        this.headerAuthorization = StringUtil.trim(this.request.getHeader(HEADER_AUTHORIZATION));
    }

    public void checkAuthorization() {
        if (StringUtils.isBlank(this.headerAuthorization)) {
            log.error("Authorization not found");
            throw new AuthorizationException(CoreMessageCode.SERVICE_UNAUTHORIZED);
        }
    }

    public void checkUserInfo() throws JsonProcessingException {
        this.username = SecurityUtil.getCurrentUserName();
        this.roles = SecurityUtil.getCurrentRoles();
        this.userId = SecurityUtil.getCurrentUserId();
    }

}
