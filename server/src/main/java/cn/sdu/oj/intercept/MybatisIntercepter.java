package cn.sdu.oj.intercept;

import cn.sdu.oj.util.Default;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Properties;

@Component
@Intercepts({
        @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})
})
public class MybatisIntercepter implements Interceptor {

    /**
     * 这个方法很好理解
     * 作用只有一个：我们不是拦截方法吗，拦截之后我们要做什么事情呢？
     * 这个方法里面就是我们要做的事情
     * <p>
     * 解释这个方法前，我们一定要理解方法参数 {@link Invocation} 是个什么鬼？
     * 1 我们知道，mybatis拦截器默认只能拦截四种类型 Executor、StatementHandler、ParameterHandler 和 ResultSetHandler
     * 2 不管是哪种代理，代理的目标对象就是我们要拦截对象，举例说明：
     * 比如我们要拦截 {@link Executor#update(MappedStatement ms, Object parameter)} 方法，
     * 那么 Invocation 就是这个对象，Invocation 里面有三个参数 target method args
     * target 就是 Executor
     * method 就是 update
     * args   就是 MappedStatement ms, Object parameter
     * <p>
     * 如果还是不能理解，我再举一个需求案例：看下面方法代码里面的需求
     * <p>
     * 该方法在运行时调用
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        /*
         * 需求：我们需要对所有更新操作前打印查询语句的 sql 日志
         * 那我就可以让我们的自定义拦截器 MyInterceptor 拦截 Executor 的 update 方法，在 update 执行前打印sql日志
         * 比如我们拦截点是 Executor 的 update 方法 ：  int update(MappedStatement ms, Object parameter)
         *
         * 那当我们日志打印成功之后，我们是不是还需要调用这个query方法呢，如何如调用呢？
         * 所以就出现了 Invocation 对象，它这个时候其实就是一个 Executor，而且 method 对应的就是 query 方法，我们
         * 想要调用这个方法，只需要执行 invocation.proceed()
         */

        /* 因为我拦截的就是Executor，所以我可以强转为 Executor，默认情况下，这个Executor 是个 SimpleExecutor */
        Executor executor = (Executor) invocation.getTarget();
        Object obj = invocation.getArgs()[1];
        setDefaultVal(obj);

        /*
         * Executor 的 update 方法里面有一个参数 MappedStatement，它是包含了 sql 语句的，所以我获取这个对象
         * 以下是伪代码，思路：
         * 1 通过反射从 Executor 对象中获取 MappedStatement 对象
         * 2 从 MappedStatement 对象中获取 SqlSource 对象
         * 3 然后从 SqlSource 对象中获取获取 BoundSql 对象
         * 4 最后通过 BoundSql#getSql 方法获取 sql
         */



        /*
         * 现在日志已经打印，需要调用目标对象的方法完成 update 操作
         * 我们直接调用 invocation.proceed() 方法
         * 进入源码其实就是一个常见的反射调用 method.invoke(target, args)
         * target 对应 Executor对象
         * method 对应 Executor的update方法
         * args   对应 Executor的update方法的参数
         */

        return invocation.proceed();
    }

    /**
     * 这个方法也很好理解
     * 作用就只有一个：那就是Mybatis在创建拦截器代理时候会判断一次，当前这个类 MyInterceptor 到底需不需要生成一个代理进行拦截，
     * 如果需要拦截，就生成一个代理对象，这个代理就是一个 {@link Plugin}，它实现了jdk的动态代理接口 {@link InvocationHandler}，
     * 如果不需要代理，则直接返回目标对象本身
     * <p>
     * Mybatis为什么会判断一次是否需要代理呢？
     * 默认情况下，Mybatis只能拦截四种类型的接口：Executor、StatementHandler、ParameterHandler 和 ResultSetHandler
     * 通过 {@link Intercepts} 和 {@link Signature} 两个注解共同完成
     * 试想一下，如果我们开发人员在自定义拦截器上没有指明类型，或者随便写一个拦截点，比如Object，那Mybatis疯了，难道所有对象都去拦截
     * 所以Mybatis会做一次判断，拦截点看看是不是这四个接口里面的方法，不是则不拦截，直接返回目标对象，如果是则需要生成一个代理
     * <p>
     * 该方法在 mybatis 加载核心配置文件时被调用
     */
    @Override
    public Object plugin(Object target) {
        /*
         * 看了这个方法注释，就应该理解，这里的逻辑只有一个，就是让mybatis判断，要不要进行拦截，然后做出决定是否生成一个代理
         *
         * 下面代码什么鬼，就这一句就搞定了？
         * Mybatis判断依据是利用反射，获取这个拦截器 MyInterceptor 的注解 Intercepts和Signature，然后解析里面的值，
         * 1 先是判断要拦截的对象是四个类型中 Executor、StatementHandler、ParameterHandler、 ResultSetHandler 的哪一个
         * 2 然后根据方法名称和参数(因为有重载)判断对哪一个方法进行拦截  Note：mybatis可以拦截这四个接口里面的任一一个方法
         * 3 做出决定，是返回一个对象呢还是返回目标对象本身(目标对象本身就是四个接口的实现类，我们拦截的就是这四个类型)
         *
         * 好了，理解逻辑我们写代码吧~~~  What !!! 要使用反射，然后解析注解，然后根据参数类型，最后还要生成一个代理对象
         * 我一个小白我怎么会这么高大上的代码嘛，怎么办？
         *
         * 那就是使用下面这句代码吧  哈哈
         * mybatis 早就考虑了这里的复杂度，所以提供这个静态方法来实现上面的逻辑
         */
        return Plugin.wrap(target, this);
    }

    /**
     * 这个方法最好理解，如果我们拦截器需要用到一些变量参数，而且这个参数是支持可配置的，
     * 类似Spring中的@Value("${}")从application.properties文件获取
     * 这个时候我们就可以使用这个方法
     * <p>
     * 如何使用？
     * 只需要在 mybatis 配置文件中加入类似如下配置，然后 {@link Interceptor#setProperties(Properties)} 就可以获取参数
     * <plugin interceptor="liu.york.mybatis.study.plugin.MyInterceptor">
     * <property name="username" value="LiuYork"/>
     * <property name="password" value="123456"/>
     * </plugin>
     * 方法中获取参数：properties.getProperty("username");
     * <p>
     * 问题：为什么要存在这个方法呢，比如直接使用 @Value("${}") 获取不就得了？
     * 原因是 mybatis 框架本身就是一个可以独立使用的框架，没有像 Spring 这种做了很多依赖注入的功能
     * <p>
     * 该方法在 mybatis 加载核心配置文件时被调用
     */
    @Override
    public void setProperties(Properties properties) {
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

    }

    public static void setDefaultVal(Object o) throws Exception {
        if (o.getClass().getAnnotation(Default.class) == null) {
            return;
        }
        Object defObj = o.getClass().newInstance();
        Field[] declaredFields = o.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), o.getClass());
            Method readMethod = descriptor.getReadMethod();
            if (readMethod.invoke(o) == null && readMethod.invoke(defObj) != null) {
                field.setAccessible(true);
                field.set(o, readMethod.invoke(defObj));
            }
        }

    }
}
