package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;

public class RedCuWireCanConnectRedstoneProcedure {
	public static int wireConnectionFilter(int connection){
		if(connection == 10) {
			return 0b00001111;
		} else if(connection >= 6){
			connection -= 6;
			connection = 0b00111011 >> connection;
		} else if(connection >= 2){
			connection -= 2;
			connection = 0b00011001 >> connection;
		} else {
			connection = 0b00001010 >> connection;
		}
		return connection & 0b00001111;
	}

	public static int wireIntersectionConnectionMask(int connection){
		if(connection < 2){
			connection = 0b00001010 >> connection;
		} else {
			connection -= 2;
			connection = 0b00001100 >> connection;
		}
		return connection & 0b00001111;
	}

	public static boolean wireIntersectionDirectionIsB(int connection, Direction direction){
		int shift = BlockFrameTransformUtils.encodeDirectionToInt(direction);
		if(connection < 2){
			shift += connection - 1;
			connection = 0b00001010;
		} else {
			shift += connection - 3;
			connection = 0b00001100;
		}
		connection >>= shift;
		connection &= 1;
		return connection > 0;
	}

	public static int redstoneToRedCu_RedCuFilter(int connection){
		int n = (0b1001111111111 >> connection) & 1;
		int e = (0b11101000000000 >> connection) & 2;
		int w = (0b1111010000000000 >> connection) & 8;
		return (n | e | w);
	}

	public static int redstoneToRedCu_RedstoneFilter(int connection){
		int n = (0b0100000000000 >> connection) & 1;
		int e = (0b00010011011000 >> connection) & 2;
		int s = (0b111111111000100 >> connection) & 4;
		int w = (0b0000001011010000 >> connection) & 8;
		return (n | e | s | w);
	}

	public static int redstoneToRedCu_AllFilter(int connection){
		int n = (0b1101111111111 >> connection) & 1;
		int e = (0b11111011011000 >> connection) & 2;
		int s = (0b111111111000100 >> connection) & 4;
		int w = (0b1111011011010000 >> connection) & 8;
		return (n | e | s | w);
	}
}
