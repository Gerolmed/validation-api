package net.endrealm.validate.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.endrealm.validate.api.DownStreamContext;

/**
 *
 */
@Data
@AllArgsConstructor
public class ExecutionData<T> {
    private T value;
    private DownStreamContext downStreamContext;
}
