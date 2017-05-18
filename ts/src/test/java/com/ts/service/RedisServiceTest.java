/**
 * 
 */
package com.ts.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ts.BaseTest;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.service.RedisService;

/**
 * @author hasee
 *
 */
public class RedisServiceTest extends BaseTest {
	
	@Autowired
	private RedisService redisService;
	
	@Test
	public void testSet(){
		BookVo bo = new BookVo();
		bo.setId(111l);
		bo.setText("一不小心深陷在小乔的魅力里了，对妹子的魅力大意了。少女心炸裂❤️❤️");
		redisService.set("test1", bo);
		System.out.println(((BookVo)redisService.get("test1")).getText());
	}
	
	@Test
	public void testGet(){
		System.out.println(((BookVo)redisService.get("test1")).getId());
	}
}
