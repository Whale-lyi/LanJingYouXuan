package top.whalefall.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RedisData {
    private Object data;
    private LocalDateTime expireTime;
}
