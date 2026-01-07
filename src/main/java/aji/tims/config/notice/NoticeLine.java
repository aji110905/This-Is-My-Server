package aji.tims.config.notice;

import aji.tims.util.MutableStringUtil;
import aji.tims.mutable.MutableString;
import aji.tims.mutable.MutableStringType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;

public class NoticeLine {
    private final LinkedList<MutableString> elements;
    private final String string;

    public NoticeLine(String string) throws IllegalArgumentException{
        this.string = string;
        this.elements = MutableStringUtil.parseStringToLinked(string, MutableStringType.NOTICE);
    }

    public String parse(ServerPlayerEntity player){
        return MutableStringUtil.parseLinkedToString(elements, player);
    }

    public String getString() {
        return string;
    }
}
