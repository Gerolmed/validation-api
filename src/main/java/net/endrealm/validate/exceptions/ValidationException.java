package net.endrealm.validate.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Base class for validation related exceptions
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class ValidationException extends RuntimeException {
    protected String reason;

    public ValidationException(String reason) {
        super(reason);
        this.reason = reason;
    }
    public String getReason() {
        return reason;
    }
}
