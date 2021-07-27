package tech.yiren.ystart.monitor.support;

import com.alibaba.druid.support.http.ResourceServlet;
import tech.yiren.ystart.common.core.util.SpringContextHolder;
import tech.yiren.ystart.monitor.service.impl.MonitorStatService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;

/**
 * @author linchtech
 * @date 2020-09-16 11:10
 **/
@Slf4j
public class MonitorViewServlet extends ResourceServlet {

	private MonitorStatService monitorStatService;

	public MonitorViewServlet() {
		super("druid");
	}

	@Override
	public void init() throws ServletException {
		log.info("init MonitorViewServlet");
		super.init();
		monitorStatService = SpringContextHolder.getBean(MonitorStatService.class);
	}

	@Override
	protected String process(String url) {
		return monitorStatService.service(url);
	}

}
