package com.shd.core.cmd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.shd.core.service.ShdNewsService;

@Component
public class NewsUpdateCmd {

	@Autowired
	ShdNewsService shdNewsService;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext context = 
            new ClassPathXmlApplicationContext("classpath:spring-core.xml");
		ShdNewsService shdNewsService = context.getBean(ShdNewsService.class);
		shdNewsService.updateNewsByBatch();
	}
	
}
