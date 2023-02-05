/**
 * 
 */
package store.plugin.extension;

import misc.CustomTab;
import store.plugin.PluginType;
import utility.ConfigEditorInfo;

import javax.annotation.Nullable;

/**
 * @author ReverendDread
 * Oct 15, 2019
 */
public abstract class FXController{

	public abstract void initialize(CustomTab tab, boolean refresh, int lastId);
	
	public abstract void save();

	public void update() {

	}
	@Nullable
	public abstract ConfigEditorInfo getInfo();

}
