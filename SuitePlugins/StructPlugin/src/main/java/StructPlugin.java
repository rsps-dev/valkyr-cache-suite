import store.plugin.Plugin;
import store.plugin.PluginType;
import suite.annotation.PluginDescriptor;

@PluginDescriptor(author = "Rocky", type = PluginType.STRUCT, version = "184")
public class StructPlugin extends Plugin {

    @Override
    public boolean load() {
        setConfig(new StructConfig());
        setLoader(new StructLoader());
        setController(new StructEditor());
        return true;
    }

    @Override
    public String getFXML() {
        return "ConfigEditor.fxml";
    }
}
