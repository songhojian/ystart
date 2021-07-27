package tech.yiren.ystart.lowcode.exception.bizException;


/**
 * @Description : 异常消息的枚举类(此类属于业务异常枚举类)
 * @Param :
 * @Return :
 * @Author : SheldonPeng
 * @Date : 2019-10-11
 */
public enum BizExceptionCodeEnum implements BizExceptionCode {

    // 已指明的异常,在异常使用时message并不返回前端，返回前端的为throw新的异常时指定的message
    SPECIFIED("-1", "系统发生异常,请稍后重试"),


    // 300 开头，与授权相关
    NO_LOGIN("3000", "用户未进行登录"),
    USER_NAME_NULL("3001", "用户名不能为空，请重新输入!"),
    USER_PASSWORD_NULL("3001", "密码不能为空，请重新输入!"),
    USER_PASSWORD_WRONG("3001", "用户名或密码错误，请检查后重新输入!"),


    // 400 开头，与业务错误相关
    PARAM_ERROR("4000", "参数错误"),
    INSERT_ERROR("4001", "插入失败"),
    UPDATE_ERROR("4002", "更新失败"),
    DELETE_ERROR("4003", "删除失败"),
    QUERY_ERROR("4004", "查询失败"),
    PAGE_PARAM_NULL("4005", "分页参数不能为空"),
    LIMIT_PARAM_ERROR("4006", "limit参数不能大于200"),
    FILE_EMPTY_ERROR("4007", "上传文件不能为空"),
    FILE_UPLOAD_ERROR("4008", "上传文件不能为空");


    private final String code;

    private final String message;

    /**
     * @Description :
     * @Param : [code, message]
     * @Return :
     * @Author : SheldonPeng
     * @Date : 2019-10-11
     */
    BizExceptionCodeEnum(String code, String message) {

        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}