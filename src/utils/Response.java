package utils;

public class Response<T> {
    private final boolean isSuccess;
    private final String message;
    private final T data;

    private Response(String message, T data, boolean isSuccess) {
        this.message = message;
        this.data = data;
        this.isSuccess = isSuccess;
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(message, data, true);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(message, null, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
