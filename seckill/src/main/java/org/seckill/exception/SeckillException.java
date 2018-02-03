package org.seckill.exception;

/**
 * Create By ${User} on 2018/1/28
 *
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
