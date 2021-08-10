package tattoo.go.templates;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class IndexHelper {

  public String concatTitleAndStyle(String title, String style) {
    if (StringUtils.isBlank(title)) {
      return style;
    } else if (StringUtils.isBlank(style)) {
      return title;
    } else return title + " # " + style;
  }

  public boolean isAllBlank(CharSequence... charSequences){
    return StringUtils.isAllBlank(charSequences);
  }

}
