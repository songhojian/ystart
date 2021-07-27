package tech.yiren.ystart.daemon.quartz;

import tech.yiren.ystart.common.feign.annotation.EnableYstartFeignClients;
import tech.yiren.ystart.common.security.annotation.EnableYstartResourceServer;
import tech.yiren.ystart.common.swagger.annotation.EnableYstartSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author frwcloud
 * @date 2019/01/23 定时任务模块
 */
@EnableYstartSwagger2
@EnableYstartFeignClients
@EnableYstartResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class YstartDaemonQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(YstartDaemonQuartzApplication.class, args);
	}

}
