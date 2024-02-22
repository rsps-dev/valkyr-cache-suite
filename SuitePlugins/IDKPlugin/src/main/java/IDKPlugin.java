import store.plugin.Plugin;
import store.plugin.PluginType;
import suite.annotation.PluginDescriptor;

@PluginDescriptor(author = "CrazzMC", type = PluginType.IDK, version = "218")
public class IDKPlugin extends Plugin {

    @Override
    public boolean load() {
        setConfig(new IDKConfig());
        setLoader(new IDKLoader());
        setController(new IDKEditor());
        return true;
    }

    @Override
    public String getFXML() {
        return "ConfigEditor.fxml";
    }
}
