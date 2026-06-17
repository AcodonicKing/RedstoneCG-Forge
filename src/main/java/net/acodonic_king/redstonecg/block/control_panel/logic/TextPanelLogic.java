package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TextPanelLogic extends DefaultPanelLogic implements ComposedTextInterface {
    public TextFormatProcedure.ComposedText TEXT = new TextFormatProcedure.ComposedText();
    public boolean CUSTOM_TEXT = false;

    public static float[] UVP_TL = new float[]{0f/32f, 0f/32f, 6f/32f, 6f/32f};
    public static float[] UVP_TM = new float[]{6f/32f, 0f/32f, 11f/32f, 6f/32f};
    public static float[] UVP_TR = new float[]{11f/32f, 0f/32f, 17f/32f, 6f/32f};

    public static float[] UVP_ML = new float[]{0f/32f, 6f/32f, 6f/32f, 16f/32f};
    public static float[] UVP_MM = new float[]{6f/32f, 6f/32f, 11f/32f, 16f/32f};
    public static float[] UVP_MR = new float[]{11f/32f, 6f/32f, 17f/32f, 16f/32f};

    public static float[] UVP_BL = new float[]{0f/32f, 16f/32f, 6f/32f, 22f/32f};
    public static float[] UVP_BM = new float[]{6f/32f, 16f/32f, 11f/32f, 22f/32f};
    public static float[] UVP_BR = new float[]{11f/32f, 16f/32f, 17f/32f, 22f/32f};

    public static BiFunction<Integer,Integer,Piece> P_TL = (x, y) -> new Piece(x, y, 6, 6, UVP_TL);
    public static BiFunction<Integer,Integer,Piece> P_TM = (x, y) -> new Piece(x, y, 5, 6, UVP_TM);
    public static BiFunction<Integer,Integer,Piece> P_TR = (x, y) -> new Piece(x, y, 6, 6, UVP_TR);

    public static BiFunction<Integer,Integer,Piece> P_ML = (x, y) -> new Piece(x, y, 6, 10, UVP_ML);
    public static BiFunction<Integer,Integer,Piece> P_MM = (x, y) -> new Piece(x, y, 5, 10, UVP_MM);
    public static BiFunction<Integer,Integer,Piece> P_MR = (x, y) -> new Piece(x, y, 6, 10, UVP_MR);

    public static BiFunction<Integer,Integer,Piece> P_BL = (x, y) -> new Piece(x, y, 6, 6, UVP_BL);
    public static BiFunction<Integer,Integer,Piece> P_BM = (x, y) -> new Piece(x, y, 5, 6, UVP_BM);
    public static BiFunction<Integer,Integer,Piece> P_BR = (x, y) -> new Piece(x, y, 6, 6, UVP_BR);

    public List<Piece> PIECES = new ArrayList<>();
    public int[] PAPER_SIZE = new int[2];

    public TextPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        loadStack(itemStack);
    }
    @Override
    public void loadStack(ItemStack stack){
        ITEM_STACK = stack;
        TEXT.clear();
        String text = TextFormatProcedure.getCustomItemName(stack);
        CUSTOM_TEXT = !text.isEmpty();
        if(!CUSTOM_TEXT)
            return;
        TEXT.load(text);
        PAPER_SIZE[0] = -1;
        PAPER_SIZE[1] = -1;
    }
    public void calcPaper(int w, int h){
        int pw = Math.max(2, (int) Math.ceil(((double) w) / 5.0));
        int ph = Math.max(1, (int) Math.ceil(((double) h) / 10.0));
        pw -= 2;
        ph -= 1;
        if(pw == PAPER_SIZE[0] && ph == PAPER_SIZE[1])
            return;
        //RedstonecgMod.LOGGER.debug(pw+" "+ph+" "+w+" "+h);
        PAPER_SIZE[0] = pw;
        PAPER_SIZE[1] = ph;
        PIECES.clear();
        int x = addPiece(0,0, P_TL).right();
        for(int ix = 0; ix < pw; ix++)
            x = addPiece(x,0, P_TM).right();
        int y = addPiece(x,0, P_TR).bottom();
        for(int iy = 0; iy < ph; iy++){
            x = addPiece(0,y, P_ML).right();
            for(int ix = 0; ix < pw; ix++)
                x = addPiece(x,y, P_MM).right();
            y = addPiece(x,y, P_MR).bottom();
        }
        x = addPiece(0,y, P_BL).right();
        for(int ix = 0; ix < pw; ix++)
            x = addPiece(x,y, P_BM).right();
        y = addPiece(x,y, P_BR).bottom();
    }
    public int getWidth(){
        return PIECES.get(PIECES.size()-1).right();
    }
    public int getHeight(){
        return PIECES.get(PIECES.size()-1).bottom();
    }
    public Piece addPiece(int x, int y, BiFunction<Integer,Integer,Piece> make){
        Piece piece = make.apply(x,y);
        PIECES.add(piece);
        return piece;
    }
    public ResourceLocation getPaper(){
        return new ResourceLocation("redstonecg","textures/panels/text_panel/paper.png");
    }
    public int getTextColor(){
        return 0x00000000;
    }

    @Override
    public TextFormatProcedure.ComposedText getComposedText() {
        return TEXT;
    }

    public static class Piece{
        public float[] UV;
        public int[] CS; //XS, YS, XE, YE
        public Piece(int x, int y, int w, int h, float[] uv){
            CS = new int[]{x, y, x+w, y+h};
            UV = uv;
        }
        public void buildVertex(Consumer<float[]> func){
            float[] param = new float[4];
            setTLVertex(param);
            func.accept(param);
            setBLVertex(param);
            func.accept(param);
            setBRVertex(param);
            func.accept(param);
            setTRVertex(param);
            func.accept(param);
        }
        public void setTLVertex(float[] param){
            param[0] = CS[0];
            param[1] = CS[1];
            param[2] = UV[0];
            param[3] = UV[1];
        }
        public void setBLVertex(float[] param){
            param[0] = CS[0];
            param[1] = CS[3];
            param[2] = UV[0];
            param[3] = UV[3];
        }
        public void setBRVertex(float[] param){
            param[0] = CS[2];
            param[1] = CS[3];
            param[2] = UV[2];
            param[3] = UV[3];
        }
        public void setTRVertex(float[] param){
            param[0] = CS[2];
            param[1] = CS[1];
            param[2] = UV[2];
            param[3] = UV[1];
        }
        public int left(){
            return CS[0];
        }
        public int top(){
            return CS[1];
        }
        public int right(){
            return CS[2];
        }
        public int bottom(){
            return CS[3];
        }
    }
}
