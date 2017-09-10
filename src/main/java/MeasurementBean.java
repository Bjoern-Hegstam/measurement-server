import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;

@Data
class MeasurementBean {
    private String source;
    private long timestampMillis;
    private String type;
    private String value;
    private String unit;

    static MeasurementBean fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, MeasurementBean.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
