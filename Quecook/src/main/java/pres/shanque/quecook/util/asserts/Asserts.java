/*
 *    Copyright 2023 ketikai
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pres.shanque.quecook.util.asserts;

import pres.shanque.quecook.util.StringUtils;


import java.util.Collection;

/**
 * <p>断言相关工具</p>
 *
 * <p>Created on 2023/2/16 19:50</p>
 *
 * @author ketikai
 * @version 0.0.1
 * @since 0.0.1
 */
public abstract class Asserts {

    /**
     * 断言表达式的结果为 true
     *
     * @param expr 表达式
     * @param msg  异常信息
     */
    public static void expr(Boolean expr, String... msg) {
        if (!Boolean.TRUE.equals(expr)) {
            throwEx(msg);
        }
    }

    /**
     * 断言对象为空
     *
     * @param obj 对象
     * @param msg 异常信息
     */
    public static void isNull(Object obj, String... msg) {
        expr(obj == null, msg);
    }

    /**
     * 断言对象非空
     *
     * @param obj 对象
     * @param msg 异常信息
     */
    public static void notNull(Object obj, String... msg) {
        expr(obj != null, msg);
    }

    /**
     * 断言数组为空
     *
     * @param arr 数组
     * @param msg 异常信息
     */
    public static void isEmpty(Object[] arr, String... msg) {
        expr(arr == null || arr.length == 0, msg);
    }

    /**
     * 断言数组非空
     *
     * @param arr 数组
     * @param msg 异常信息
     */
    public static void notEmpty(Object[] arr, String... msg) {
        expr(arr != null && arr.length != 0, msg);
    }

    /**
     * 断言集合为空
     *
     * @param coll 集合
     * @param msg  异常信息
     */
    public static void isEmpty(Collection<Object> coll, String... msg) {
        expr(coll == null || coll.isEmpty(), msg);
    }

    /**
     * 断言集合非空
     *
     * @param coll 集合
     * @param msg  异常信息
     */
    public static void notEmpty(Collection<Object> coll, String... msg) {
        expr(coll != null && !coll.isEmpty(), msg);
    }

    /**
     * 断言字符串有文本内容
     *
     * @param str 字符
     * @param msg 异常信息
     */
    public static void hasText(String str, String... msg) {
        expr(!StringUtils.isNullOrEmpty(str), msg);
    }

    private static void throwEx(String... msg) {
        final int msgLength = msg.length;
        if (msgLength == 0) {
            throw new IllegalArgumentException();
        }
        if (msgLength == 1) {
            throw new IllegalArgumentException(String.valueOf(msg[0]));
        }

        final StringBuilder sb = new StringBuilder(64);
        for (final String message : msg) {
            sb.append(message);
        }
        throw new IllegalArgumentException(sb.toString());
    }
}
