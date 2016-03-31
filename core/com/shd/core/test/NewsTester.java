package com.shd.core.test;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.shd.core.service.ShdNewsService;
import com.shd.core.utils.DeptNode;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-core.xml")
@TransactionConfiguration(defaultRollback = true)
public class NewsTester {
	
	@Autowired
	ShdNewsService shdNewsService;
	
	
	@Test
	public void testGetEmphMListPagination() throws Exception {
		
		List<DeptNode> list = shdNewsService.getALLDeptList("57");
		
		for(DeptNode node:list){
			System.out.println("===========");
			System.out.println(node.getTitle());
			for(DeptNode node2:node.getChildren()){
				
				System.out.println("======"+node2.getTitle());
				for(DeptNode node3:node2.getChildren()){
					
					System.out.println("============"+node3.getTitle());
				}
			}
			
		}
		
	}
	
	
	
	
}
