package com.alexvait.accountingapi.security.springcontext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

// Using this class to retrieve Spring beans for classes that are not injected into application context

@Component
public class SpringApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext springApplicationContext;

    public static <T> T getBean(Class<T> beanClass) {
        return springApplicationContext.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springApplicationContext = applicationContext;
    }
}
