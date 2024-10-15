package baby.mumu.authentication.client.dto;

import baby.mumu.authentication.client.dto.co.AccountAddSystemSettingsCo;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * 添加账户系统设置指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
public class AccountAddSystemSettingsCmd {

  @Valid
  private AccountAddSystemSettingsCo accountAddSystemSettingsCo;
}
