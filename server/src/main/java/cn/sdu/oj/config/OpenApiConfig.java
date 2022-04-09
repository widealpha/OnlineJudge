package cn.sdu.oj.config;


import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
@EnableOpenApi
public class OpenApiConfig {
    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Autowired
    public OpenApiConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    @Bean
    public Docket createRestApi() {
        String groupName = "1.0.0";
        return new Docket(DocumentationType.OAS_30)
                // 指定构建api文档的详细信息的方法
                .apiInfo(apiInfo())
                .groupName(groupName)
                .select()
                // 指定要生成api接口的包路径，这里把controller作为包路径，生成controller中的所有接口
                .apis(RequestHandlerSelectors.basePackage("cn.sdu.oj.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(securityScheme()))
                .extensions(openApiExtensionResolver.buildExtensions(groupName));
    }

    /**
     * 构建api文档的详细信息
     *
     * @return 返回详细的API信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("OnlineJudge API")
                // 设置接口描述
                .description("Online Judge服务器接口文档")
                // 设置联系方式
                .contact(new Contact("SDU", "http://localhost:8080/", ""))
                // 设置版本
                .version("1.0")
                // 构建
                .build();
    }


    /***
     * 权限模式
     *
     * @return 权限模式
     */
    @Bean
    SecurityScheme securityScheme() {
        return new ApiKey("jwt", "Authorization", "header");
    }

}
