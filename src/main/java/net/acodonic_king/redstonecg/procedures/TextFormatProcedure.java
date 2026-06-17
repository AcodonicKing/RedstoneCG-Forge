package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class TextFormatProcedure {
    public static Map<Integer, UnaryOperator<Style>> CUSTOM_FORMATS = new HashMap<>();
    static {
        CUSTOM_FORMATS.put((int) 'G', style -> style.withColor(0xDDD605));
        CUSTOM_FORMATS.put((int) 'H', style -> style.withColor(0xE3D4D1));
        CUSTOM_FORMATS.put((int) 'I', style -> style.withColor(0xCECACA));
        CUSTOM_FORMATS.put((int) 'J', style -> style.withColor(0x443A3B));
        CUSTOM_FORMATS.put((int) 'M', style -> style.withColor(0x971607));
        CUSTOM_FORMATS.put((int) 'N', style -> style.withColor(0xB4684D));
        CUSTOM_FORMATS.put((int) 'P', style -> style.withColor(0xDEB12D));
        CUSTOM_FORMATS.put((int) 'Q', style -> style.withColor(0x119F36));
        CUSTOM_FORMATS.put((int) 'S', style -> style.withColor(0x2CBAA8));
        CUSTOM_FORMATS.put((int) 'T', style -> style.withColor(0x21497B));
        CUSTOM_FORMATS.put((int) 'U', style -> style.withColor(0x9A5CC6));
        CUSTOM_FORMATS.put((int) 'V', style -> style.withColor(0xEB7114));
        CUSTOM_FORMATS.put((int) 'W', style -> style.withColor(0x8CB3FF));
    }
    public static List<Component> execute(String text){
        return execute(new ArrayList<>(), text);
    }
    public static List<Component> execute(List<Component> textLines, String text){
        textLines.clear();
        MutableComponent composedTextHolder = Component.literal("");
        List<Object> formats = new ArrayList<>();
        StringBuilder textBuilder = new StringBuilder();
        boolean formater = false;
        for(int value: text.codePoints().toArray()){
            if(value == '\\' || value == '§'){
                if (formater)
                    textBuilder.appendCodePoint(value);
                formater = !formater;
                continue;
            }
            if (formater) {
                addComponents(composedTextHolder, textBuilder, formats);
                textBuilder = new StringBuilder();
                switch ((char) value){
                    case 'r': {
                        formats.clear();
                        break;
                    }
                    case 'p': {
                        textLines.add(composedTextHolder);
                        composedTextHolder = Component.literal("");
                        break;
                    }
                    default: {
                        Object nf = getStyle(value);
                        if(nf == null)
                            textBuilder.appendCodePoint(value);
                        else {
                            if((nf instanceof ChatFormatting cf && cf.isColor()) || (nf instanceof UnaryOperator uo)){
                                int i = 0;
                                for(Object o: formats) {
                                    if (o instanceof ChatFormatting ocf)
                                        if (ocf.isColor())
                                            break;
                                    i++;
                                }
                                if(i < formats.size())
                                    formats.remove(i);
                            }
                            formats.add(nf);
                        }
                        break;
                    }
                }
                formater = false;
                continue;
            }
            textBuilder.appendCodePoint(value);
        }
        addComponents(composedTextHolder, textBuilder, formats);
        textLines.add(composedTextHolder);
        return textLines;
    }
    private static void addComponents(MutableComponent composedTextHolder, StringBuilder textBuilder, List<Object> formats){
        if(textBuilder.isEmpty())
            return;
        MutableComponent component = Component.literal(textBuilder.toString());
        for(Object format: formats){
            if(format instanceof ChatFormatting cf)
                component.withStyle(cf);
            if(format instanceof UnaryOperator uo)
                component.withStyle(uo);
        }
        composedTextHolder.append(component);
    }
    public static Object getStyle(int codepoint){
        ChatFormatting format = ChatFormatting.getByCode((char) codepoint);
        if(format == null)
            return CUSTOM_FORMATS.get(codepoint);
        return format;
    }
    public static String getCustomItemName(ItemStack stack){
        String org_text = stack.getItem().getDefaultInstance().getDisplayName().getString();
        String text = stack.getDisplayName().getString();
        if(org_text.equals(text))
            return "";
        if (text.startsWith("[") && text.endsWith("]"))
            text = text.substring(1, text.length() - 1);
        return text;
    }
    public static class ComposedText{
        public List<Component> LINES = new ArrayList<>();
        public ComposedText(){}
        public ComposedText(String text){
            execute(LINES, text);
        }
        public ComposedText load(String text){
            LINES.clear();
            execute(LINES, text);
            return this;
        }
        public ComposedText clear(){
            LINES.clear();
            return this;
        }
        public int lines(){
            return LINES.size();
        }
        public Component getLine(int i){
            return LINES.get(i);
        }
        public Component getFirst(){
            return LINES.get(0);
        }
        public Component getLast(){
            return LINES.get(LINES.size() - 1);
        }
        public boolean isEmpty(){
            return LINES.isEmpty();
        }
        public List<Component> getLines(){
            return LINES;
        }
    }
}
