package de.comramo.engagiert.word2pdf;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConversionResponse {
  
  private String pdfAsBase64;
}
