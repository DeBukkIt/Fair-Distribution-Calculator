package eu.ajg.fairdistribution;

public class Student {

	protected String name;
	protected int[] wishes;

	public Student(String name, int optionId0, int optionId1, int optionId2) {
		this.name = name;
		
		wishes = new int[3];
		setWishes(optionId0, optionId1, optionId2);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOptionId(int wishRank) {
		return wishes[wishRank];
	}

	public void setWishes(int optionId0, int optionId1, int optionId2) {
		if (optionId0 == optionId1 || optionId0 == optionId2 || optionId1 == optionId2) {
			System.err.println("[Error] " + name + " chose the same wish twice.\n");
			throw new IllegalArgumentException(
					"The same value may not appear twice under first, second, and third preferences.");
		}
		
		wishes[0] = optionId0;
		wishes[1] = optionId1;
		wishes[2] = optionId2;
	}
	
	public int getWishRank(int optionId) {
		for(int i = 0; i < wishes.length; i++) {
			if(wishes[i] == optionId) {
				return i;
			}
		}
		return -1;
	}

}