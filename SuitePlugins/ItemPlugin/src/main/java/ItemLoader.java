import javafx.scene.control.Alert;
import store.CacheLibrary;
import store.cache.index.Index;
import store.cache.index.archive.Archive;
import store.cache.index.archive.file.File;
import store.io.impl.InputStream;
import store.plugin.extension.LoaderExtensionBase;
import suite.controller.Selection;
import suite.dialogue.Dialogues;

/**
 * 
 */

/**
 * @author ReverendDread
 * Oct 6, 2019
 */
public class ItemLoader extends LoaderExtensionBase {

	public static int streamIndices[];
	@Override
	public boolean load() {
		try {
			CacheLibrary cache = CacheLibrary.get();

			if (cache.is317()) {
				InputStream stream = new InputStream(getConfigFile317("obj.dat"));
				InputStream streamIdx = new InputStream(getConfigFile317("obj.idx"));

				int totalItems = streamIdx.readUnsignedShort();

				streamIndices = new int[totalItems];

				int i = 2;

				for (int j = 0; j < totalItems; j++) {
					streamIndices[j] = i;

					i += streamIdx.readUnsignedShort();
				}

				for (int id = 0; id < totalItems; id++) {
					stream.position = streamIndices[id];
					ItemConfig definition = new ItemConfig();
					definition.id = id;
					readConfig(stream, definition);
					definitions.put(id, definition);
					Selection.progressListener.pluginNotify("(" + id + "/" + streamIndices.length + ")");
				}

				return true;
			}

			Index index = cache.getIndex(getIndex());
			Archive archive = index.getArchive(getArchive());

			int[] fileIds = index.getArchive(getArchive()).getFileIds();

			for (int id : fileIds) {
				File file = index.getArchive(getArchive()).getFile(id);
				if (file == null || file.getData() == null)
					continue;
				ItemConfig definition = new ItemConfig();
				definition.id = id;
				InputStream buffer = new InputStream(file.getData());
				readConfig(buffer, definition);
				definitions.put(id, toNote(definition));
				Selection.progressListener.pluginNotify("(" + id + "/" + fileIds.length + ")");
			}
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public int getFile() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int getArchive() {
		return CacheLibrary.get().is317() ? 2 : 10;
	}

	@Override
	public int getIndex() {
		return CacheLibrary.get().is317() ? 0 : 2;
	}

	private ItemConfig toNote(ItemConfig config) {
		if (config.notedID != -1 && config.notedTemplate != -1) {
			ItemConfig original = (ItemConfig) getDefinitions().get(config.notedID);
			if (original != null) {
				config.name = original.name;
				config.cost = original.cost;
				config.members = original.members;
				config.stackable = original.stackable;
			} else {
				Dialogues.alert(Alert.AlertType.ERROR, "Error", "You can't import an item with a non-existing note item.", null, false);
				return null;
			}
		}
		return config;
	}

}