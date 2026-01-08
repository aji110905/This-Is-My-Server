package aji.tims.config.notice;

import aji.tims.util.MutableStringUtil;
import aji.tims.mutable.MutableString;
import aji.tims.mutable.MutableStringType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;

public class NoticeLine {
    private LinkedList<MutableString> elements;
    private final String string;
    private boolean isParsed;

    public NoticeLine(String string){
        this.string = string;
        try {
            this.elements = MutableStringUtil.parseStringToLinked(string, MutableStringType.NOTICE);
            isParsed = true;
        } catch (Exception e) {
            isParsed = false;
        }
    }

    public String parse(ServerPlayerEntity player){
        return isParsed ? MutableStringUtil.parseLinkedToString(elements, player) : "";
    }

    public String getString() {
        return string;
    }
}
