package me.zeroseven.island.minions;

public enum MinionType {
	
	BLOCKS, MOBS, CROPS;
	
	public static MinionType fromString(String str) {
		
		for (MinionType m : values()) {
			if (m.toString().equals(str)) return m;
		}
		return null;
	}

}
