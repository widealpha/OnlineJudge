package cn.sdu.judge.config;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@EnableIntegration
public class SftpConfig {
    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.user}")
    private String user;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.local-cache-dir}")
    private String localCacheDir;
    @Value("${sftp.remote-root-dir}")
    private String remoteRootDir;

    /**
     * 配置连接缓存池
     */
    @Bean
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(host);
        factory.setPort(port);
        factory.setUser(user);
        factory.setPassword(password);
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }

    /**
     * 注入sftp远程文件访问模板
     * 用来创建 SftpOutboundGateway（见 SftpOutboundGateway 构造方法源码）
     */
    @Bean
    public SftpRemoteFileTemplate sftpRemoteFileTemplate() {
        return new SftpRemoteFileTemplate(sftpSessionFactory());
    }

    /**
     * 发送执行 ls 命令
     * payload 为远程目录
     */
    @Bean
    @ServiceActivator(inputChannel = "lsChannel")
    public MessageHandler lsHandler() {
        SftpOutboundGateway gateway = new SftpOutboundGateway(sftpSessionFactory(), "ls", "payload");
        gateway.setOptions("-dirs");   // 显示目录记录
        return gateway;
    }


    /**
     * 发送执行 get 命令
     * payload 为远程文件路径
     */
    @Bean
    @ServiceActivator(inputChannel = "getChannel")
    public MessageHandler getHandler() {
        SftpOutboundGateway gateway = new SftpOutboundGateway(sftpSessionFactory(), "get", "payload");
        gateway.setOptions("-P");
        gateway.setAutoCreateDirectory(true);
        gateway.setAutoCreateLocalDirectory(true);
        File dir = new File(localCacheDir);
        gateway.setLocalDirectory(dir);
        return gateway;
    }

    /**
     * 为具体命令配置channel
     */
    @Bean
    public DirectChannel getChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel lsChannel() {
        return new DirectChannel();
    }
}
