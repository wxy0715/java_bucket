### 简介
@Validation是一套帮助我们继续对传输的参数进行数据校验的注解，通过配置Validation可以很轻松的完成对数据的约束。
@Validated作用在类、方法和参数上

![image-20230414162851411](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230414162851411.png)

### 第一步,分组
通常可以新建全局的新建,编辑,删除等分组接口,这里我使用第二种。在每个cmd/qry类里面加上分组接口(这样比较灵活)
![image-20230414162956715](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230414162956715.png)

### 第二步,对象上加验证
![image-20230414163011577](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230414163011577.png)

#### 想要对象里面的子对象也生效,做法如下
属性上加上`@Valid`
```java
@ApiModelProperty("拆分后项目集合")
@Valid
@NotEmpty(message = "拆分后项目信息不能为空",groups = {SplitItemPlanCmd.SaveCmdInterface.class})
private List<SplitItemCmd> splitItemCmdList;
```
![image-20230414163039851](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230414163039851.png)

### 第三步,接口加注解
分别在类上,方法上,实参前面加上注解

![image-20230414163057032](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230414163057032.png)

方法上的注解加分组,不要使用全局注解以免对别的接口造成影响比如不要写下面所示方法@Validated,要具体到分组体现出新增/修改/删除业务

```java
@Validated
Long saveSplitItemPlan(@Valid SplitItemPlanCmd splitItemPlanCmd);
```
### 自定义注解

#### 编写注解
```java
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MobileValidator.class})
public @interface MobileCheck {
    boolean required() default true;

    String message() default "手机号码格式有误!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

#### 写实现验证的类
```java
public class MobileValidator implements ConstraintValidator<MobileCheck, String> {

    private boolean require = false;

    @Override
    public void initialize(MobileCheck isMobile) {
        require = isMobile.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (require) {
            return ValidatorUtil.isMobile(value);
        } else {
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
```

```java
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }


}
```