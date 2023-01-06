package de.comramo.engagiert.word2pdf;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@Data
@RequiredArgsConstructor
@Getter
public class ConversionRequest {
    private final String templateAsBase64;
    private String backgroundAsBase64;
    private final HashMap<String, String> valueMapping;


}
