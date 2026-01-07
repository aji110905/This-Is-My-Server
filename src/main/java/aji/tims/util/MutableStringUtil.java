package aji.tims.util;

import aji.tims.mutable.MutableString;
import aji.tims.mutable.MutableStringParameter;
import aji.tims.mutable.MutableStringType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Set;

public class MutableStringUtil {
    /**
     * 从一个参数集合中获得你想要的参数
     * @param name 参数名
     * @param mutableStringParameters 参数集合
     * @return 你想要的参数
     */
    public static MutableStringParameter getParameter(String name, Set<MutableStringParameter> mutableStringParameters){
        for (MutableStringParameter mutableStringParameter : mutableStringParameters) {
            if (mutableStringParameter.name().equals(name)) {
                return mutableStringParameter;
            }
        }
        throw new IllegalArgumentException("Unknown parameter: " + name);
    }

    public static LinkedList<MutableString> parseStringToLinked(String string, MutableStringType type){
        LinkedList<MutableString> elements = new LinkedList<>();
        char[] charArray = string.toCharArray();
        StringBuilder elementTemp = new StringBuilder();
        StringBuilder stringTemp = new StringBuilder();
        boolean bl = true;
        for (int i = 0; i < charArray.length; i++) {
            if (bl) {
                if (charArray[i] == '{') {
                    bl = false;
                    if (!stringTemp.isEmpty()) {
                        elements.add(MutableString.of(stringTemp.toString(), type));
                        stringTemp = new StringBuilder();
                    }
                    elementTemp.append('{');
                } else if (charArray[i] == '}') {
                    throw new IllegalArgumentException("Unexpected '}' at position " + i);
                } else {
                    stringTemp.append(charArray[i]);
                }
            } else {
                if (charArray[i] == '}') {
                    bl = true;
                    elementTemp.append('}');
                    elements.add(MutableString.of(elementTemp.toString(), type));
                    elementTemp = new StringBuilder();
                } else {
                    elementTemp.append(charArray[i]);
                }
            }
        }
        if (!stringTemp.isEmpty()) {
            elements.add(MutableString.of(stringTemp.toString(), type));
        }
        if (!bl) {
            throw new IllegalArgumentException("Unclosed '{' at end ofNotice string");
        }
        return elements;
    }

    public static String parseLinkedToString(LinkedList<MutableString> elements, @Nullable ServerPlayerEntity player){
        StringBuilder stringBuilder = new StringBuilder();
        for (MutableString element : elements) {
            stringBuilder.append(element.parse(player));
        }
        return stringBuilder.toString();
    }
}
