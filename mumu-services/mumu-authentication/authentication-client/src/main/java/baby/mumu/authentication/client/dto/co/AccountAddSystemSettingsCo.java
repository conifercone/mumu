package baby.mumu.authentication.client.dto.co;

import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
  @Nullable
  @Size(min = 1)
  private String id;

  /**
   * 系统设置标识
   */
  @NotBlank
  private String profile;

  /**
   * 系统设置名称
   */
  @NotBlank
  private String name;

  /**
   * 系统主题
   */
  @NotNull
  private SystemThemeEnum systemTheme = SystemThemeEnum.DEFAULT;

  /**
   * 系统主题模式
   */
  @NotNull
  private SystemThemeModeEnum systemThemeMode = SystemThemeModeEnum.SYNC_WITH_SYSTEM;

  /**
   * 已启用
   */
  @NotNull
  private Boolean enabled = false;
}
