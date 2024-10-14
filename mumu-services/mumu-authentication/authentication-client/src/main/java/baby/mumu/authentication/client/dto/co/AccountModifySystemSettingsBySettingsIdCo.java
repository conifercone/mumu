package baby.mumu.authentication.client.dto.co;

import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新账户系统设置客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountModifySystemSettingsBySettingsIdCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = -2434107327544242748L;

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
  private SystemThemeEnum systemTheme;

  /**
   * 系统主题模式
   */
  private SystemThemeModeEnum systemThemeMode;

  /**
   * 已启用
   */
  private Boolean enabled;

  private Long version;
}
