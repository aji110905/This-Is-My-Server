package aji.tims.mutable;

import aji.tims.ThisIsMyServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiFunction;

import static aji.tims.util.ExceptionUtil.willThrowException;
import static aji.tims.util.MutableStringUtil.getParameter;

public class MutableString {
    private static final Set<MutableStringInfo> NOTICE_MUTABLE_STRING_INFOS = new HashSet<>();
    private static final Set<MutableStringInfo> MOTD_MUTABLE_STRING_INFOS = new HashSet<>();

    private final Set<MutableStringParameter> mutableStringParameters;
    private final BiFunction<@Nullable ServerPlayerEntity, MutableString, String> parser;

    private MutableString(String[] parameters, MutableStringInfo MutableStringInfo) throws IllegalArgumentException{
        Set<MutableStringParameter> inputMutableStringParameters = new HashSet<>();
        for (String parameter : parameters) {
            String[] split = parameter.split("=");
            for (MutableStringParameterInfo parameterInfo : MutableStringInfo.mutableStringParameters()) {
                if (split[0].equals(parameterInfo.name())) {
                    LinkedList<StringBuilder> valueTemp = new LinkedList<>();
                    StringBuilder stringTemp = new StringBuilder();
                    StringBuilder elementTemp = new StringBuilder();
                    boolean bl = true;
                    char[] valueCharArray = split[1].toCharArray();
                    for (int i = 0; i < valueCharArray.length; i++) {
                        if (bl){
                            if (valueCharArray[i] == '('){
                                bl = false;
                                if (!stringTemp.isEmpty()){
                                    valueTemp.add(stringTemp);
                                    stringTemp = new StringBuilder();
                                }
                                elementTemp.append('(');
                            }else if(valueCharArray[i] == ')'){
                                throw new IllegalArgumentException("Unexpected ')' at position " + i);
                            }else {
                                stringTemp.append(valueCharArray[i]);
                            }
                        }else {
                            if (valueCharArray[i] == ')'){
                                bl = true;
                                elementTemp.append(')');
                                valueTemp.add(elementTemp);
                                elementTemp = new StringBuilder();
                            }else {
                                elementTemp.append(valueCharArray[i]);
                            }
                        }
                    }

                    if (!stringTemp.isEmpty()) {
                        valueTemp.add(stringTemp);
                    }

                    if (!bl) {
                        throw new IllegalArgumentException("Unclosed '(' at end ofNotice string");
                    }

                    StringBuilder value = new StringBuilder();
                    for (StringBuilder stringBuilder : valueTemp) {
                        String string = stringBuilder.toString();
                        switch (string){
                            case "(left_brace)" -> value.append("(");
                            case "(right_brace)" -> value.append(")");
                            case "(space)" -> value.append(" ");
                            case "(equal_sign)" -> value.append("=");
                            default -> value.append(string);
                        }
                    }

                    inputMutableStringParameters.add(new MutableStringParameter(parameterInfo.name(), value.toString()));
                    break;
                }
            }
        }

        MutableStringInfo.mutableStringParameters().stream()
                .filter(MutableStringParameterInfo::must)
                .forEach(paramInfo -> {
                    if (inputMutableStringParameters.stream().noneMatch(p -> p.name().equals(paramInfo.name()))) {
                        throw new IllegalArgumentException("Must parameter " + paramInfo.name() + " is not provided");
                    }
                });

        MutableStringInfo.mutableStringParameters().stream()
                .filter(paramInfo -> !paramInfo.must())
                .filter(paramInfo -> inputMutableStringParameters.stream().noneMatch(p -> p.name().equals(paramInfo.name())))
                .forEach(paramInfo -> inputMutableStringParameters.add(new MutableStringParameter(paramInfo.name(), paramInfo.defaultValue())));


        MutableStringInfo.mutableStringParameters().stream()
                .filter(mutableStringParameterInfo -> inputMutableStringParameters.stream()
                        .anyMatch(mutableStringParameter -> mutableStringParameter.name().equals(mutableStringParameterInfo.name()) &&
                                !mutableStringParameterInfo.mutableStringParameterValidator().isValidValue(mutableStringParameter.value())))
                .findFirst()
                .ifPresent(parameterInfo -> {
                    MutableStringParameter mutableStringParameter = inputMutableStringParameters.stream()
                            .filter(p -> p.name().equals(parameterInfo.name()))
                            .findFirst()
                            .orElseThrow();
                    throw new IllegalArgumentException(parameterInfo.mutableStringParameterValidator().errorMessage(parameterInfo, mutableStringParameter.value()));
                });

        this.mutableStringParameters = Set.copyOf(inputMutableStringParameters);
        this.parser = MutableStringInfo.parser();
    }

    public String parse(@Nullable ServerPlayerEntity player) {
        return parser.apply(player, this);
    }

    public static MutableString of(String string, MutableStringType type){
        if (type == MutableStringType.NOTICE) return MutableString.of(string, NOTICE_MUTABLE_STRING_INFOS);
        return MutableString.of(string, MOTD_MUTABLE_STRING_INFOS);
    }

    private static MutableString of(String string, Set<MutableStringInfo> motdMutableStringInfos) {
        if (!(string.startsWith("{") && string.endsWith("}"))) {
            return new MutableString(new String[0], new MutableStringInfo("", Set.of(), (context, mutableString) -> string));
        }
        String[] split = string.substring(1, string.length() - 1).split(" ");
        for (MutableStringInfo MutableStringInfo : motdMutableStringInfos) {
            if (MutableStringInfo.name().equals(split[0])) {
                String[] parameters = new String[split.length - 1];
                System.arraycopy(split, 1, parameters, 0, parameters.length);
                return new MutableString(parameters, MutableStringInfo);
            }
        }
        throw new IllegalArgumentException("Unknown notice element: " + string);
    }

    /**
     * 注册MutableString到Notice
     * @param newMutableStringInfo 新的MutableString
     * @throws IllegalArgumentException 如果MutableString已存在
     */
    public static void registerMutableStringToNotice(MutableStringInfo newMutableStringInfo) throws IllegalArgumentException{
        registerMutableString(newMutableStringInfo, NOTICE_MUTABLE_STRING_INFOS);
    }

    /**
     * 注册MutableString到Motd
     * @param newMutableStringInfo 新的MutableString
     * @throws IllegalArgumentException 如果MutableString已存在
     */
    public static void registerMutableStringToMotd(MutableStringInfo newMutableStringInfo) throws IllegalArgumentException{
        registerMutableString(newMutableStringInfo, MOTD_MUTABLE_STRING_INFOS);
    }

    private static void registerMutableString(MutableStringInfo newMutableStringInfo, Set<MutableStringInfo> targetSet) throws IllegalArgumentException {
        if (
                !(targetSet.stream()
                .filter(mutableStringInfo -> mutableStringInfo.name().equals(newMutableStringInfo.name()))
                .toArray()
                .length == 0)
        ) {
            throw new IllegalArgumentException("MutableString with name " + newMutableStringInfo.name() + " already exists");
        } else if (ThisIsMyServer.initialized){
            throw new IllegalArgumentException("MutableString with name " + newMutableStringInfo.name() + " register too late, This is not a problem with the mod, please contact the extension developer");
        } else {
            targetSet.add(newMutableStringInfo);
        }
    }

    /**
     * 注册MutableString到Notice和Motd
     * @param newMutableStringInfo 新的MutableString
     * @throws IllegalArgumentException 如果MutableString已存在
     */
    public static void registerMutableString(MutableStringInfo newMutableStringInfo) throws IllegalArgumentException{
        registerMutableStringToNotice(newMutableStringInfo);
        registerMutableStringToMotd(newMutableStringInfo);
    }

    /**
     * 获取MutableString的参数 供扩展开发者调用 该集合为不可变集合
     * @return MutableString的参数
     */
    public Set<MutableStringParameter> getMutableStringParameters() {
        return mutableStringParameters;
    }

    static {
        registerMutableString(
                new MutableStringInfo(
                        "now_time",
                        Set.of(
                                new MutableStringParameterInfo("format", false, "YYYY-MM-dd HH:mm:ss", string -> !willThrowException(() -> DateTimeFormatter.ofPattern(string))),
                                new MutableStringParameterInfo("zone", false, ZoneId.systemDefault().getId(), string -> !willThrowException(() -> ZoneId.of(string)))
                        ),
                        (player, mutableString) -> {
                            MutableStringParameter format = getParameter("format", mutableString.mutableStringParameters);
                            MutableStringParameter zone = getParameter("zone", mutableString.mutableStringParameters);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format.value());
                            ZoneId zoneId = ZoneId.of(zone.value());
                            return LocalDateTime.now(zoneId).format(dateTimeFormatter);
                        }
                )
        );
        registerMutableString(
                new MutableStringInfo(
                        "day_to_today",
                        Set.of(
                                new MutableStringParameterInfo("day", true, null, string -> !willThrowException(() -> LocalDate.parse(string))),
                                new MutableStringParameterInfo("zone", false, ZoneId.systemDefault().getId(), string -> !willThrowException(() -> ZoneId.of(string)))
                        ),
                        (player, mutableString) -> {
                            MutableStringParameter day = getParameter("day", mutableString.mutableStringParameters);
                            MutableStringParameter zone = getParameter("zone", mutableString.mutableStringParameters);
                            ZoneId zoneId = ZoneId.of(zone.value());
                            LocalDate now = LocalDate.now(zoneId);
                            LocalDate parse = LocalDate.parse(day.value());
                            return Long.toString(ChronoUnit.DAYS.between(parse, now));
                        }
                )
        );
        registerMutableString(
                new MutableStringInfo(
                        "left_curly_brace",
                        Set.of(),
                        (player, mutableString) -> "{"
                )
        );
        registerMutableString(
                new MutableStringInfo(
                        "right_curly_brace",
                        Set.of(),
                        (player, mutableString) -> "}"
                )
        );
        registerMutableString(
                new MutableStringInfo(
                        "seed",
                        Set.of(),
                        (player, mutableString) -> Long.toString(ThisIsMyServer.server.getWorld(World.OVERWORLD).getSeed())
                )
        );
        registerMutableString(
                new MutableStringInfo(
                        "minecraft_version",
                        Set.of(),
                        (player, mutableString) -> ThisIsMyServer.server.getVersion()
                )
        );
        registerMutableStringToNotice(
                new MutableStringInfo(
                        "player_name",
                        Set.of(),
                        (player, mutableString) -> player == null ? "" : player.getName().getString()
                )
        );
        registerMutableStringToNotice(
                new MutableStringInfo(
                        "player_uuid",
                        Set.of(),
                        (player, mutableString) -> player == null ? "" : player.getUuid().toString()
                )
        );
        registerMutableStringToNotice(
                new MutableStringInfo(
                        "online_users",
                        Set.of(
                                new MutableStringParameterInfo("include", false, "true", string -> (string.equals("true") || string.equals("false")))
                        ),
                        (player, mutableString) -> {
                            MutableStringParameter include = getParameter("include", mutableString.mutableStringParameters);
                            int size = ThisIsMyServer.server.getCurrentPlayerCount();
                            if (include.value().equals("true")) return Integer.toString(size);
                            else return Integer.toString(size - 1);
                        }
                )
        );
        registerMutableStringToMotd(
                new MutableStringInfo(
                        "online_users",
                        Set.of(),
                        (player, mutableString) -> Integer.toString(ThisIsMyServer.server.getPlayerManager().getPlayerList().size())
                )
        );
    }
}
