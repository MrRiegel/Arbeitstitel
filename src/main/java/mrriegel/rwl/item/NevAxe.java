package mrriegel.rwl.item;

import java.util.List;
import java.util.Vector;

import mrriegel.rwl.RWL;
import mrriegel.rwl.creative.CreativeTab;
import mrriegel.rwl.gui.GuiIDs;
import mrriegel.rwl.gui.InventoryNevTool;
import mrriegel.rwl.init.ModItems;
import mrriegel.rwl.reference.Reference;
import mrriegel.rwl.utility.BlockLocation;
import mrriegel.rwl.utility.MyUtils;
import mrriegel.rwl.utility.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;

public class NevAxe extends ItemAxe {
	public static ToolMaterial MATERIAL = EnumHelper.addToolMaterial(
			"MATERIAL", 2, 1999, 10.0F, 2.0F, 14);

	public NevAxe() {
		super(MATERIAL);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(Reference.MOD_ID + ":" + "nevaxe");
		this.setTextureName(Reference.MOD_ID + ":" + "nevaxe");
	}

	@Override
	public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
		if (p_82789_2_.getItem().equals(ModItems.nev)) {
			return true;
		}
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 1;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {
		player.openGui(RWL.instance, GuiIDs.NEVTOOL, world, 0, 0, 0);
		return stack;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean boo) {
		if (stack.getTagCompound() == null)
			return;
		if (stack
				.getTagCompound()
				.getTagList(InventoryNevTool.tagName,
						stack.getTagCompound().getId()).getStringTagAt(0)
				.equals("")) {
			return;
		}
		switch (stack
				.getTagCompound()
				.getTagList(InventoryNevTool.tagName,
						stack.getTagCompound().getId()).getCompoundTagAt(0)
				.getShort("Damage")) {
		case 0:
			list.add("radius 1");
			break;
		case 1:
			list.add("radius 2");
			break;
		case 2:
			list.add("radius 3");
			break;
		case 3:
			list.add("silk");
			break;
		case 4:
			list.add("fortune");
			break;
		case 8:
			list.add("whole tree");
			break;
		}

	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		if (stack.getTagCompound() == null)
			return super.getDigSpeed(stack, block, meta);
		if (stack
				.getTagCompound()
				.getTagList(InventoryNevTool.tagName,
						stack.getTagCompound().getId()).getCompoundTagAt(0)
				.getShort("Damage") == 5) {
			return super.getDigSpeed(stack, block, meta) * 3.0f;

		} else if (stack
				.getTagCompound()
				.getTagList(InventoryNevTool.tagName,
						stack.getTagCompound().getId()).getCompoundTagAt(0)
				.getShort("Damage") == 2) {
			return super.getDigSpeed(stack, block, meta) / 6.5f;

		}
		return super.getDigSpeed(stack, block, meta);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z,
			EntityPlayer player) {
		if (stack.getTagCompound() == null)
			return false;
		if (player.worldObj.isRemote) {
			return false;
		}
		switch (stack
				.getTagCompound()
				.getTagList(InventoryNevTool.tagName,
						stack.getTagCompound().getId()).getCompoundTagAt(0)
				.getShort("Damage")) {
		case 0:
			radius(stack, x, y, z, player, 1);
			return false;
		case 1:
			radius(stack, x, y, z, player, 2);
			return false;
		case 2:
			radius(stack, x, y, z, player, 3);
			player.getFoodStats().setFoodLevel(
					player.getFoodStats().getFoodLevel() - 1);
			return false;
		case 8:
			if (ForgeHooks.isToolEffective(stack,
					player.worldObj.getBlock(x, y, z),
					player.worldObj.getBlockMetadata(x, y, z))) {
				chop2(x, y, z, player.worldObj,
						player.worldObj.getBlock(x, y, z),
						player.worldObj.getBlockMetadata(x, y, z));
			}
			return false;
		default:
			break;

		}

		return super.onBlockStartBreak(stack, x, y, z, player);
	}

	private void chop2(int x, int y, int z, World world, Block block, int l) {
		for (BlockLocation bl : MyUtils.getNeighbors(world, x, y, z)) {

			if (world.getBlock(bl.x, bl.y, bl.z).getUnlocalizedName()
					.equals(block.getUnlocalizedName())
					&& world.getBlockMetadata(bl.x, bl.y, bl.z) == l) {
				MyUtils.breakWithFortune(world, bl.x, bl.y, bl.z, 0);
				chop2(bl.x, bl.y, bl.z, world, block, l);
			}

		}
	}

	protected void radius(ItemStack stack, int x, int y, int z,
			EntityPlayer player, int radius) {
		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int direction = -1;
		Vector<BlockLocation> v = new Vector<BlockLocation>();
		MovingObjectPosition mop = MyUtils.raytraceFromEntity(world, player,
				false, 4.5d);

		if (mop == null) {
			return;
		}
		if (!ForgeHooks.isToolEffective(stack, block, meta)) {
			return;
		}
		if (stack
				.getTagCompound()
				.getTagList(InventoryNevTool.tagName,
						stack.getTagCompound().getId()).getStringTagAt(0)
				.equals("")) {
			return;
		}

		direction = mop.sideHit;
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (direction == 0 || direction == 1)
					v.add(new BlockLocation(i + x, y, j + z));
				if (direction == 2 || direction == 3)
					v.add(new BlockLocation(i + x, j + y, z));
				if (direction == 4 || direction == 5)
					v.add(new BlockLocation(x, i + y, j + z));
			}
		}
		for (BlockLocation b : v) {
			Block bl = world.getBlock(b.x, b.y, b.z);
			if (ForgeHooks.isToolEffective(stack, bl, meta)) {
				MyUtils.breakWithFortune(world, b.x, b.y, b.z, 0);
				stack.setItemDamage(stack.getItemDamage() + 1);
			}
		}

	}
}
