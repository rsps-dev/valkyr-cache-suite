package utility;

import lombok.Builder;
import lombok.Getter;
import store.plugin.PluginType;

/**
 * @author ReverendDread on 4/4/2020
 * https://www.rune-server.ee/members/reverenddread/
 * @project ValkyrCacheSuite
 */
@Builder @Getter
public class ConfigEditorInfo {

    private int index;
    private int archive = 0;
    private int file = 0;
    private boolean is317;
    private String fileName;

    private PluginType type;

}
