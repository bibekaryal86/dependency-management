package dep.mgmt.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {
  @Override
  public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
    jg.writeRaw(": ");
  }

  @NotNull
  @Override
  public DefaultPrettyPrinter createInstance() {
    return new CustomPrettyPrinter();
  }
}
