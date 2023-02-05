import store.plugin.PluginType;
import suite.controller.ConfigEditor;
import utility.ConfigEditorInfo;

/**
 * @author ReverendDread on 5/17/2020
 * https://www.rune-server.ee/members/reverenddread/
 * @project ValkyrCacheSuite
 */
public class BitEditor extends ConfigEditor {

    @Override
    public ConfigEditorInfo getInfo() {
        return ConfigEditorInfo.builder().index(2).archive(14).type(PluginType.BIT).build();
    }

}