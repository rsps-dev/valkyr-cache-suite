import store.plugin.Plugin;
import store.plugin.PluginType;
import suite.annotation.PluginDescriptor;

@PluginDescriptor(author = "Rocky", type = PluginType.INV, version = "184")
public class InvPlugin extends Plugin {

    @Override
    public boolean load() {
        setConfig(new InvConfig());
        setLoader(new InvLoader());
        setController(new InvEditor());
        return true;
    }

    @Override
    public String getFXML() {
        return "ConfigEditor.fxml";
    }
}
