package baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.dataobject;

import baby.mumu.basis.dataobject.jpa.JpaRedisBasisArchivableDataObject;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import java.io.Serial;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 角色基本信息缓存
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "role")
public class RoleRedisDo extends JpaRedisBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = 2814267592168109003L;

  /**
   * 角色ID
   */
  @Id
  @Indexed
  private Long id;

  /**
   * 角色编码
   */
  @Indexed
  private String code;

  /**
   * 角色名称
   */
  private String name;

  /**
   * 存活时间
   * <p>低等级别变化数据：默认缓存时间为6小时</p>
   */
  @TimeToLive(unit = TimeUnit.HOURS)
  private Long ttl = 6L;
}
