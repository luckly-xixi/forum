package com.example.forum.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.forum.dao") //配置扫描路径
public class MybatisConfig {
}
