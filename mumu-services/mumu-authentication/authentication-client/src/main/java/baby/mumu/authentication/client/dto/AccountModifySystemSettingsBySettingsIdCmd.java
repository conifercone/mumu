package baby.mumu.authentication.client.dto;

import baby.mumu.authentication.client.dto.co.AccountModifySystemSettingsBySettingsIdCo;
import lombok.Data;

/**
 * 更新账户系统设置指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
public class AccountModifySystemSettingsBySettingsIdCmd {

  private AccountModifySystemSettingsBySettingsIdCo accountModifySystemSettingsBySettingsIdCo;
}
