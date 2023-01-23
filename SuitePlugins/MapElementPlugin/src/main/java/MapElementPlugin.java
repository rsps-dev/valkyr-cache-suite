import store.plugin.Plugin;
import store.plugin.PluginType;
import suite.annotation.PluginDescriptor;

@PluginDescriptor(author = "Rocky", type = PluginType.INV, version = "184")
public class MapElementPlugin extends Plugin {

    @Override
    public boolean load() {
        setConfig(new MapElementConfig());
        setLoader(new MapElementLoader());
        setController(new MapElementEditor());
        return true;
    }

    @Override
    public String getFXML() {
        return "ConfigEditor.fxml";
    }
}
