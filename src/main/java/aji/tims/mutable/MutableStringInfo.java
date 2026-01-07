package aji.tims.mutable;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * @param name MutableString的名称 占位符的名字
 * @param mutableStringParameters MutableString的参数
 * @param parser 解析器 根据当前情况把该文本变为实际文本
 */
public record MutableStringInfo(
        String name,
        Set<MutableStringParameterInfo> mutableStringParameters,
        BiFunction<ServerPlayerEntity, MutableString, String> parser
) {
}
