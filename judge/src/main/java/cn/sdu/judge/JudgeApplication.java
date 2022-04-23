package cn.sdu.judge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * 不需要使用SpringBoot的Application自动配置数据库
 */
@SpringBootApplication
public class JudgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JudgeApplication.class, args);
	}

}
