package net.oikmo.engine.inventory;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.world.blocks.Block;

public class Item {
	
	private static Map<String, Item> itemIndex = new HashMap<>();
	
	private static Image atlas;
	private static Image blockAtlas;
	
	public static Item[] blockItems = new Item[Block.blocks.length];
	public static final Item ironBlock = new Item(Block.Type.IRONBLOCK, "Iron Block", 64).setDescription("Block of iron").setBlockImageFromAtlas(15, 0);
	public static final Item goldBlock = new Item(Block.Type.GOLDBLOCK, "Gold Block", 64).setDescription("Block of gold").setBlockImageFromAtlas(15, 1);
	public static final Item diamondBlock = new Item(Block.Type.DIAMONDBLOCK, "Diamond Block", 64).setDescription("Block of diamonds").setBlockImageFromAtlas(15, 2);
	
	public static final Item grass = new Item(Block.Type.GRASS, "Grass", 64).setBlockImageFromAtlas(0, 0);
	public static final Item dirt = new Item(Block.Type.DIRT, "Dirt", 64).setBlockImageFromAtlas(1, 0);
	public static final Item stone = new Item(Block.Type.STONE, "Stone", 64).setBlockImageFromAtlas(2, 0);
	public static final Item bedrock = new Item(Block.Type.BEDROCK, "Bedrock", 64).setDescription("How did you get this?").setBlockImageFromAtlas(3, 0);
	public static final Item cobble = new Item(Block.Type.COBBLE, "Cobblestone", 64).setBlockImageFromAtlas(4, 0);
	public static final Item mossycobble = new Item(Block.Type.MOSSYCOBBLE, "Mossy Cobblestone", 64).setBlockImageFromAtlas(5, 0);
	public static final Item obsidian = new Item(Block.Type.OBSIDIAN, "Obsidian", 64).setBlockImageFromAtlas(6, 0);
	
	public static final Item oakleaf = new Item(Block.Type.LEAVES, "Oak leaves", 64).setBlockImageFromAtlas(0, 1);
	public static final Item oakLog = new Item(Block.Type.WOOD, "Oak Log", 64).setBlockImageFromAtlas(1, 1);
	public static final Item oakPlanks = new Item(Block.Type.PLANKS, "Oak Planks", 64).setBlockImageFromAtlas(2, 1);
	
	public static final Item glass = new Item(Block.Type.GLASS, "Glass", 64).setBlockImageFromAtlas(0, 2);
	public static final Item smoothStone = new Item(Block.Type.SMOOTHSTONE, "Smooth Stone", 64).setBlockImageFromAtlas(1, 2);
	public static final Item brick = new Item(Block.Type.BRICK, "Bricks", 64).setBlockImageFromAtlas(2, 2);
	
	public static final Item tnt = new Item(Block.Type.TNT, "TNT", 64).setDescription("Boom boom boom. I can't stop singing this bloody tune tune tune").setBlockImageFromAtlas(0, 3);
	
	private static void initImage() {
		if(atlas != null) { return; }
		atlas = new Image(ResourceLoader.loadUITexture("ui/items"));
		blockAtlas = new Image(ResourceLoader.loadUITexture("ui/blockitems"));
	}
	
	private Image image;
	private String name = "";
	private String desc = "";
	private int maxStackSize;
	private int blockIndex = -1;
	
	private int atlasX, atlasY;
	
	private Item(String itemName, int maxStackSize) {
		initImage();
		this.name = itemName;
		this.maxStackSize = maxStackSize;
		itemIndex.put(itemName.replaceAll("\\s+","").toLowerCase(), this);
	}
	
	private Item(Block.Type type, String itemName, int maxStackSize) {
		initImage();
		this.name = itemName;
		this.maxStackSize = maxStackSize;
		blockItems[type.ordinal()] = this;
		this.blockIndex = type.ordinal();
		itemIndex.put(itemName.replaceAll("\\s+","").toLowerCase(), this);
	}
	
	public static Item getItemFromID(String id) {
		return itemIndex.get(id);
	}
	
	public String getID() {
		return name.replaceAll("\\s+","").toLowerCase();
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return desc;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public Image getImage() {
		return image;
	}
	
	public Item setDescription(String desc) {
		this.desc = desc;
		return this;
	}
	
	public Item setImageFromAtlas(int x, int y) {
		this.atlasX = x*16;
		this.atlasY = y*16;
		this.image = atlas.getSubImage(atlasX, atlasY, 16, 16);
		this.image.setFilter(Image.FILTER_NEAREST);
		
		return this;
	}
	
	public Item setBlockImageFromAtlas(int x, int y) {
		this.atlasX = x*16;
		this.atlasY = y*16;
		this.image = blockAtlas.getSubImage(atlasX, atlasY, 16, 16);
		this.image.setFilter(Image.FILTER_NEAREST);
		
		return this;
	}
	
	public static Item blockToItem(Block block) {
		return blockItems[block.getByteType()];
	}
	
	public static Block itemToBlock(Item item) {
		if(item != null) {
			int i = item.blockIndex;
			if(i != -1) {
				return Block.blocks[i];
			}
		}
		
		return null;
	}
}
