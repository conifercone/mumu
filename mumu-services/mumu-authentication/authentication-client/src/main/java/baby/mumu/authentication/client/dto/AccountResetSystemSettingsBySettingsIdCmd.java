package baby.mumu.authentication.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 根据系统设置ID重置账户系统设置指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
public class AccountResetSystemSettingsBySettingsIdCmd {

  @NotBlank
  private String systemSettingsId;
}
