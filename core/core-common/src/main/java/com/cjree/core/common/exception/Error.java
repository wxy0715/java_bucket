package com.cjree.core.common.exception;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.utils.ExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
public class Error {

    private ResponseCode responseCode;
    private List<Arg> args;
    private String message;
    private String detail;

    public static ErrorBuilder builder() {
        return new ErrorBuilder();
    }

    @AllArgsConstructor
    public static class Arg {
        Object key;
        Object value;

        @Override
        public String toString() {
            return "[arg: key=" + key.toString() + ",value=" + value.toString() + "]";
        }
    }

    @Slf4j
    public static class ErrorBuilder {

        private ResponseCode responseCode;
        private List<Arg> args = new LinkedList<>();
        private String message;
        private Exception e;

        public ErrorBuilder responseCode(ResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public ErrorBuilder arg(Object key, Object value) {
            args.add(new Arg(key, value));
            return this;
        }

        public ErrorBuilder message(String msg) {
            this.message = msg;
            return this;
        }

        public ErrorBuilder exception(Exception e) {
            this.e = e;
            return this;
        }

        public Error build() {
            String detail = detail();
            log.error(detail);
            if (StringUtils.isEmpty(message) && Objects.nonNull(e)) {
                message = e.getMessage();
            }
            if (StringUtils.isEmpty(message)) {
                message = detail;
            }
            return new Error(this.responseCode, this.args, this.message, detail);
        }

        /**
         * 错误的详细信息
         */
        public String detail() {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotEmpty(message)) {
                sb.append("-->");
                sb.append(message);
                sb.append(System.lineSeparator());
            }
            if (!(args == null || args.isEmpty())) {
                sb.append("-->");
                sb.append(responseCode.message());
                sb.append(", args: {");
                sb.append(args.stream().map(Arg::toString).collect(Collectors.joining(",")));
                sb.append("}");
                sb.append(System.lineSeparator());
            } else {
                sb.append("-->");
                sb.append(responseCode.message());
                sb.append(System.lineSeparator());
            }
            if (Objects.nonNull(e)) {
                sb.append(ExceptionUtil.getStackTraceAsString(e));
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
    }

}
