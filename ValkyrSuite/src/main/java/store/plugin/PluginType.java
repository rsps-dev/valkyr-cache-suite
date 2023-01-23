/**
 * 
 */
package store.plugin;

import utility.StringUtilities;

/**
 * @author ReverendDread
 * Oct 6, 2019
 */
public enum PluginType {

	INV,
	FLO,
	FLU,
	HUFFMAN,
	ITEM,
	OBJECT,
	NPC,
	SEQUENCE,
	FRAME,
	BASE,
	SPOT_ANIM,
	TEXTURE,
	MESH,
	SPRITE,
	PARTICLE,
	BAS,
	COLOUR,
	MAP,
	MATERIAL,
	HITMARK,
	ENUM,
	WIDGET,
	CS2,
    WORLD_MAP;

    public String toString() {
		return StringUtilities.getFormattedEnumName(name());
	}
	
}
