package aji.tims.mutable;


@FunctionalInterface
public interface MutableStringParameterValidator {
    /**
     * 验证参数值
     * @param value 新参数值
     * @return 是否合法
     */
    boolean isValidValue(String value);

     /**
     * 当参数不合法时的错误信息
     * @param parameterInfo 参数信息
     * @param wrongValue 错误值
     * @return 错误信息
     */
    default String errorMessage(MutableStringParameterInfo parameterInfo, String wrongValue){
        return "Invalid value for parameter " + parameterInfo.name() + ": " + wrongValue;
    }
}
