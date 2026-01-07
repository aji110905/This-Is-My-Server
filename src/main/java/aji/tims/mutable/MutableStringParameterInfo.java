package aji.tims.mutable;

/**
 * @param name 该参数的名字 在占位符中书写
 * @param must 该参数是否必须
 * @param defaultValue 如果不必须 该参数的值是什么 必须时可以为null
 * @param mutableStringParameterValidator 该参数的验证器
 */
public record MutableStringParameterInfo(
        String name,
        boolean must,
        String defaultValue,
        MutableStringParameterValidator mutableStringParameterValidator
) {
}
