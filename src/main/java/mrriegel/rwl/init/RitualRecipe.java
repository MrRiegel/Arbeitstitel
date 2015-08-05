package mrriegel.rwl.init;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

public class RitualRecipe {

	private ItemStack output;
	private ItemStack input1;
	private ItemStack input2;
	private ItemStack input3;
	private ItemStack input4;
	private ItemStack cat;

	public RitualRecipe(ItemStack output, ItemStack input1, ItemStack input2,
			ItemStack input3, ItemStack input4, ItemStack cat) {
		super();
		this.output = output;
		this.input1 = input1;
		this.input2 = input2;
		this.input3 = input3;
		this.input4 = input4;
		this.cat = cat;
	}

	private boolean contains(ItemStack stack, List<ItemStack> lis) {
		for (ItemStack s : lis)
			if (ItemStack.areItemStacksEqual(s, stack))
				return true;
		return false;
	}

	private boolean eq(List<ItemStack> a, List<ItemStack> b) {
		for (ItemStack s : a)
			if (!contains(s, b))
				return false;
		return true;
	}

	public boolean matches(ItemStack[] ar) {

		List<ItemStack> ist = Arrays.asList(ar);
		List<ItemStack> soll = Arrays.asList(new ItemStack[] { input1, input2,
				input3, input4 });

		if (eq(ist, soll)) {
			return true;
		}
		return 0 == 1;
	}

	public ItemStack getOutput() {
		return output;
	}

	public void setOutput(ItemStack output) {
		this.output = output;
	}

	public ItemStack getCat() {
		return cat;
	}

	public void setCat(ItemStack cat) {
		this.cat = cat;
	}

}