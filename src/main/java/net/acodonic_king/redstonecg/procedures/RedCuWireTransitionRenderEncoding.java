package net.acodonic_king.redstonecg.procedures;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class RedCuWireTransitionRenderEncoding {
    public static int encodeRotation(String rotation) {
        int out = 0;
        for(String sub: rotation.split(">")){
            out <<= 4;
            out |= encodeRotation(sub.charAt(0),sub.charAt(1));
        }
        return out;
    }
    public static int encodeRotation(char axis, char dir) {
        int axisBits = switch (axis) {
            case 'X' -> 1;
            case 'Y' -> 2;
            case 'Z' -> 3;
            default -> 0;
        };
        int dirBits = switch (dir) {
            case 'N' -> 0;
            case 'E' -> 1;
            case 'S' -> 2;
            case 'W' -> 3;
            default -> throw new IllegalArgumentException("Invalid dir: " + dir);
        };
        return (axisBits << 2) | dirBits;
    }
    public static int packRenderEntry(int modelId, String rotation) {
        int packed = 0;
        packed |= (modelId & 0xF);
        packed |= (encodeRotation(rotation) << 4);
        return packed;
    }
    public static void applyRotationFromCode(PoseStack poseStack, int code) {
        code &= 0xF;
        int axisBits = (code >> 2) & 0x3;
        int dirBits = code & 0x3;
        if(axisBits == 0 || dirBits == 0){return;}
        double angle = switch (dirBits) {
            case 0 -> 0;
            case 1 -> Math.PI * 0.5;
            case 2 -> Math.PI;
            case 3 -> Math.PI * 1.5;
            default -> 0;
        };
        RCGQuaternion quat = switch (axisBits) {
            case 1 -> RCGQuaternion.Vector3F.rotateXP((float) angle);
            case 2 -> RCGQuaternion.Vector3F.rotateYP((float) angle);
            case 3 -> RCGQuaternion.Vector3F.rotateZP((float) angle);
            default -> RCGQuaternion.Vector3F.rotateYP((float) angle);
        };
        poseStack.mulPose(quat.quaternion);
    }
    public static int getModelId(int packed) {
        return packed & 0xF;
    }
    public static int getRotationA(int packed) {
        return (packed >> 4) & 0xF;
    }
    public static int getRotationB(int packed) {
        return (packed >> 8) & 0xF;
    }
    public static BakedModel getModel(ModelManager modelManager, int packed){
        packed &= 0xF;
        if(packed == 15)
            return modelManager.getModel(new ModelResourceLocation(new ResourceLocation("redstonecg:smooth_stone_plate"), "facing=down,rotation=north,waterlogged=false"));
        String model_variant = "model="+packed+",waterlogged=false";
        //String model_variant = "model="+packed;
        return modelManager.getModel(new ModelResourceLocation(new ResourceLocation("redstonecg:redcu_wire_transition"), model_variant));
    }

}
