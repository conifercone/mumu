package baby.mumu.authentication.client.dto.co;

import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 添加账户系统设置客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountAddSystemSettingsCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = -3265820245470464810L;

  /**
   * 唯一主键
   */
  private String id;

  /**
   * 系统设置标识
   */
  private String profile;

  /**
   * 系统设置名称
   */
  private String name;

  /**
   * 系统主题
   */
  private SystemThemeEnum systemTheme = SystemThemeEnum.DEFAULT;

  /**
   * 系统主题模式
   */
  private SystemThemeModeEnum systemThemeMode = SystemThemeModeEnum.SYNC_WITH_SYSTEM;

  /**
   * 已启用
   */
  private Boolean enabled = false;

  private Long version;
}
