import lombok.extern.slf4j.Slf4j;
import store.CacheLibrary;
import store.plugin.PluginType;
import suite.controller.ConfigEditor;
import utility.ConfigEditorInfo;

/**
 * 
 * @author ReverendDread Jul 10, 2018
 */
public class ItemEditor extends ConfigEditor {

	@Override
	public ConfigEditorInfo getInfo() {
		if (CacheLibrary.get().is317()) {
			return ConfigEditorInfo.builder().is317(true).fileName("obj").index(0).archive(2).type(PluginType.ITEM).build();
		}
		return ConfigEditorInfo.builder().index(2).archive(10).type(PluginType.ITEM).build();
	}

}
