package cjminecraft.core.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A set which will put all {@link ItemStack}'s with the same description
 * together rather than separately, the count will most likely exceed 64
 * 
 * @author CJMinecraft
 *
 */
public class ItemStackSet implements Set<ItemStack>, Iterable<ItemStack> {

	private static NBTTagCompound nbt = new NBTTagCompound();
	private HashMap<NBTTagCompound, Integer> stacks;

	/**
	 * A set which will put all {@link ItemStack}'s with the same description
	 * together rather than separately, the count will most likely exceed 64
	 */
	public ItemStackSet() {
		this.stacks = new HashMap<NBTTagCompound, Integer>();
	}

	/**
	 * A set which will put all {@link ItemStack}'s with the same description
	 * together rather than separately, the count will most likely exceed 64
	 * 
	 * @param c
	 *            A collection of {@link ItemStack}'s to put into a set
	 */
	public ItemStackSet(Collection<? extends ItemStack> c) {
		this.stacks = new HashMap<NBTTagCompound, Integer>();
		addAll(c);
	}

	@Override
	public boolean add(ItemStack e) {
		if(e.isEmpty())
			return false;
		nbt = e.serializeNBT();
		nbt.removeTag("Count");
		if (contains(nbt))
			this.stacks.replace(nbt, this.stacks.get(nbt) + e.getCount());
		else 
			this.stacks.put(nbt, e.getCount());
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends ItemStack> c) {
		for (ItemStack stack : c)
			add(stack);
		return true;
	}

	@Override
	public void clear() {
		this.stacks.clear();
	}

	@Override
	public boolean contains(Object o) {
		return o instanceof NBTTagCompound ? this.stacks.containsKey(o) : this.stacks.containsValue(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (ItemStack stack : (Set<ItemStack>) c) {
			nbt = stack.serializeNBT();
			nbt.removeTag("Count");
			if (!contains(nbt))
				return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.stacks.isEmpty();
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new ItemStackIterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.stacks.remove(o) == null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object e : c)
			remove(e);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		for (NBTTagCompound key : this.stacks.keySet())
			if (key != null && !(c.contains(key)))
				remove(key);
		return true;
	}

	@Override
	public int size() {
		return this.stacks.size();
	}

	@Override
	public Object[] toArray() {
		return getStacks().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getStacks().toArray(a);
	}

	/**
	 * @return the item stacks all with the count representative of all those in
	 *         the stacks of similar description
	 */
	public ImmutableList<ItemStack> getStacks() {
		List<ItemStack> stacksFinal = new ArrayList<ItemStack>();
		for (NBTTagCompound nbt : this.stacks.keySet()) {
			ItemStack stack = new ItemStack(nbt);
			stack.setCount(this.stacks.get(nbt));
			stacksFinal.add(stack);
		}
		return ImmutableList.<ItemStack>copyOf(stacksFinal);
	}

	private class ItemStackIterator implements Iterator<ItemStack> {

		private ItemStack[] stacks;
		private int index;

		public ItemStackIterator() {
			this.index = 0;
			this.stacks = (ItemStack[]) toArray();
		}

		@Override
		public boolean hasNext() {
			return this.index <= this.stacks.length;
		}

		@Override
		public ItemStack next() {
			ItemStack stack = this.stacks[this.index];
			this.index++;
			return stack;
		}

	}

}
