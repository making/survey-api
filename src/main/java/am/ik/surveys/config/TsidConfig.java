package am.ik.surveys.config;

import am.ik.surveys.tsid.TsidGenerator;
import com.github.f4b6a3.tsid.TsidFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TsidConfig {

	@Bean
	public TsidGenerator tsidFactory() {
		final TsidFactory tsidFactory = TsidFactory.newInstance256();
		return tsidFactory::create;
	}

}
