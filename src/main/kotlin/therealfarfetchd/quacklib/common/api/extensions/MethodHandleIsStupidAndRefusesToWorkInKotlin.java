package therealfarfetchd.quacklib.common.api.extensions;

import java.lang.invoke.MethodHandle;

public class MethodHandleIsStupidAndRefusesToWorkInKotlin {
    @SuppressWarnings("unchecked")
    public static <T, R> R call(MethodHandle mh, T t) throws Throwable {
        return (R) mh.invoke(t);
    }
}
