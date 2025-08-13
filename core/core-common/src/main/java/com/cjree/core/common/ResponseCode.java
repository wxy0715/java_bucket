package com.cjree.core.common;

/**
 * 系统应用响应状态码
 */
public enum ResponseCode {

    /**
     * 成功
     */
    SUCCESS("00000", "成功"),
    /**
     * 失败
     */
    FAILURE("99999", "失败"),

    /**
     * 未登录
     */
    LOGIN_NOT_ONLINE("10000", "未登录"),
    /**
     * 登录异常
     */
    LOGIN_EXCEPTION("10001", "登录异常"),
    /**
     * 用户名错误
     */
    LOGIN_USERNAME_ERROR("10002", "用户名错误"),
    /**
     * 密码错误
     */
    LOGIN_PASSWORD_ERROR("10003", "密码错误"),
    /**
     * 验证码错误
     */
    LOGIN_VERIFYCODE_ERROR("10004", "验证码错误"),
    /**
     * 账号不存在
     */
    LOGIN_UNKNOW_ACCOUNT("10005", "账号不存在"),
    /**
     * 账号已存在
     */
    ACCOUNT_ALREADY_EXISTS("10006", "账号已存在"),
    /**
     * 手机号已被注册
     */
    MOBILE_ALREADY_EXISTS("10007", "手机号已被注册"),
    /**
     * 账号被冻结
     */
    ACCOUNT_LOCKED("10008", "账号被冻结"),
    /**
     * 密码错误次数过多
     */
    LOGIN_PASSWORD_ERROR_COUNT("10009", "密码输入错误次数过多，请于30分钟后重试"),
    /**
     * 应用无效或不存在
     */
    LOGIN_APPLICATION_ERROR("10010", "应用无效或不存在"),
    /**
     * 接口访问权限异常
     */
    URL_PERMISSION_ERROR("10011", "接口访问权限异常"),
    /**
     * OAUTH2登入异常
     */
    LOGIN_OAUTH2_ERROR("100012", "OAUTH2登入异常"),
    /**
     * 数据验证异常
     */
    DATA_VERIFY_EXCEPTION("20000", "数据验证异常"),
    /**
     * 当前对象id为空
     */
    DATA_VERIFY_EMPTY_ID("20001", "当前对象id为空"),
    /**
     * 参数为空（List)
     */
    DATA_VERIFY_EMPTY_LIST("20002", "参数为空"),
    /**
     * SQL 注入 嫌疑
     */
    DATA_VERIFY_SQL_INJECT("20003", "SQL注入嫌疑"),
    /**
     * 数据验证空指针
     */
    DATA_VERIFY_NULL_POINTER("20003", "数据验证空指针"),

    /**
     * 数据解析异常
     */
    DATA_PARSE_EXCEPTION("30000", "数据解析异常"),

    /**
     * 数据权限异常
     */
    DATA_PERMISSION_EXCEPTION("40000", "数据权限异常"),

    /**
     * 数据访问存取异常
     */
    DATA_ACCESS_EXCEPTION("50000", "数据访问存取异常"),
    /**
     * 存在相同的记录
     */
    DATA_ACCESS_CONFLICT_RECORD("50001", "存在相同的记录"),
    /**
     * 配置文件中不存在此key
     */
    DATA_ACCESS_UNEXIST_KEY("50002", "配置文件中不存在此key"),
    /**
     * 数据库不存在该条记录
     */
    DATA_ACCESS_NOT_FOUND("50003", "未查询到记录"),
    /**
     * 从session获取用户出错
     */
    DATA_ACCESS_INVALID_SESSION("50004", "无效的会话"),
    /**
     * 数据更新失败
     */
    DATA_ACCESS_UPDATE_FAILED("50005", "数据更新失败"),
    /**
     * 数据版本不一致
     */
    DATA_ACCESS_DIFFERENT_VERSION("50006", "数据版本不一致"),
    /**
     * 数据物理删除失败
     */
    DATA_ACCESS_PHYSICAL_DELETE_FAILED("50007", "物理删除失败"),
    /**
     * 数据逻辑删除失败
     */
    DATA_ACCESS_LOGIC_DELETE_FAILED("50008", "逻辑删除失败"),
    /**
     * 文件处理异常
     */
    DATA_ACCESS_FILE_HANDLE_ERROR("50009", "文件处理异常"),

    /**
     * 业务异常
     */
    BUSINESS_EXCEPTION("60000", "业务异常"),
    /**
     * 业务异常
     */
    BUNISESS_EXCEPTION("600000", "业务异常"),
    /**
     * 该条项目申报已被通过
     */
    BUNISESS_DECLARE_PASSED("600001", "该条项目申报已被通过"),
    /**
     * 当前节点不支持此操作
     */
    BUNISESS_UNSUPPORTED_TASK_ACTION("600002", "当前节点不支持此操作"),
    /**
     * 当前记录已经启动流程
     */
    BUNISESS_RECORD_STARTED("600003", "当前记录已经启动流程"),
    /**
     * 当前委托函下子项目未全部通过
     */
    BUNISESS_DECLARE_UNPASS("600004", "子项目未全部通过"),

    /**
     * 服务器出错
     */
    SERVER_INTERNAL_EXCEPTION("70000", "服务器出错"),
    /**
     * 保存token出错
     */
    SERVER_INTERNAL_TOKEN_SAVE_FAILURE("70001", "保存token出错"),
    /**
     * 删除token出错
     */
    SERVER_INTERNAL_TOKEN_DEL_FAILURE("70002", "删除token出错"),
    /**
     * 获取token出错
     */
    SERVER_INTERNAL_TOKEN_GET_FAILURE("70003", "获取token出错"),
    /**
     * 请求异常
     */
    REQUEST_EXCEPTION("80000", "请求异常"),
    /**
     * 恶意请求
     */
    REQUEST_MALICIOUS("80001", "恶意请求"),

    /**
     * 流程中心访问异常
     */
    WORKFLOW_RPC_EXCEPTION("90000", "流程中心访问异常"),
    /**
     * 签名/验签异常
     */
    SIGN_MESSAGE_EXCEPTION("90001", "签名/验签异常"),

    /**
     * 生成密钥异常
     */
    KEY_GENERATE_EXCEPTION("91000", "生成密钥异常"),
    /**
     * 签名异常
     */
    SIGN_EXCEPTION("91001", "签名异常"),
    /**
     * 签名验证异常
     */
    SIGN_VERIFY_EXCEPTION("91002", "签名验证异常"),
    /**
     * 加密异常
     */
    ENCRYPT_EXCEPTION("91003", "加密异常"),
    /**
     * 解密异常
     */
    DECRYPT_EXCEPTION("91004", "解密异常"),
    /**
     * 请求验证异常
     */
    SIGN_REQUEST_EXCEPTION("91005", "请求签名异常"),
    /**
     * 请求验证异常
     */
    VALID_REQUEST_EXCEPTION("91006", "请求验证异常"),
    ;

    private final String value;
    private final String message;

    ResponseCode(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String value() {
        return this.value;
    }

    public String message() {
        return message;
        //return Resources.getMessage("RESPONSECODE_" + this.value);
    }

    @Override
    public String toString() {
        return this.name() + "" + this.value.toString();
    }


    /**
     * 根据提供的字符串值查找并返回相应的ResponseCode枚举实例。
     * 如果找不到匹配的枚举实例，则抛出IllegalArgumentException异常。
     *
     * @param value 要查找的字符串值，应与某个ResponseCode枚举实例的value()方法返回值相匹配。
     * @return 匹配的ResponseCode枚举实例。
     * @throws IllegalArgumentException 如果没有与提供的值相匹配的枚举常量。
     */
    public static ResponseCode fromValue(String value) {
        for (ResponseCode rcode : ResponseCode.values()) {
            if (rcode.value().equals(value)) {
                return rcode;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }

}
