import store.plugin.Plugin;
import store.plugin.PluginType;
import suite.annotation.PluginDescriptor;

@PluginDescriptor(author = "CrazzMC", type = PluginType.BIT, version = "184")
public class BitPlugin extends Plugin {

    @Override
    public boolean load() {
        setConfig(new BitConfig());
        setLoader(new BitLoader());
        setController(new BitEditor());
        return true;
    }

    @Override
    public String getFXML() {
        return "ConfigEditor.fxml";
    }
}
