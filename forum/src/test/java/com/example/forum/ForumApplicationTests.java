package com.example.forum;

import com.example.forum.dao.UserMapper;
import com.example.forum.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@SpringBootTest
class ForumApplicationTests {

	@Resource
	private UserMapper userMapper;

	@Resource
	private DataSource dataSource;


	@Test
	public void testConnection () throws SQLException {
		System.out.println("dataSource = " + dataSource.getClass());
		Connection connection = dataSource.getConnection();
		System.out.println("connection = " + connection);
		connection.close();
	}

	@Test
	void testMybatis() {
		User user = userMapper.selectByPrimaryKey(1L);
		System.out.println(user);
		System.out.println(user.getUsername());
	}

	@Test
	void contextLoads() {
		System.out.println("Hello Text");
	}


	@Test
	void testUUID() {
		System.out.println(UUID.randomUUID().toString());
	}

}
