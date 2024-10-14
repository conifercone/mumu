package baby.mumu.extension.nosql;

import static baby.mumu.basis.constants.BeanNameConstants.MUMU_JPA_MONGODB_AUDITOR_AWARE;

import baby.mumu.basis.dataobject.jpa.MuMuJpaMongodbAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mongo配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Configuration
public class MongodbConfiguration {

  @Bean(name = MUMU_JPA_MONGODB_AUDITOR_AWARE)
  public MuMuJpaMongodbAuditorAware mumuJpaMongodbAuditorAware() {
    return new MuMuJpaMongodbAuditorAware();
  }
}
