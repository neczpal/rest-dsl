import java.util.Objects;

public class ApiResponse {
    private Integer code;
    private String type;
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(Integer code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse that = (ApiResponse) o;
        return Objects.equals(code, that.code) && Objects.equals(type, that.type) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, type, message);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", type=" + type +
                ", message=" + message +
                '}';
    }
}
