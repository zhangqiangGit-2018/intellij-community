import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nls;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@interface Anno {}

class Test {
    String test(@Anno @Language("HTML") @Nls String sample){
        sample = "<html>EOF</html>";
        return newMethod(sample);
    }

    @Language("HTML")
    @Nls
    private String newMethod(@Language("HTML") @Nls String sample) {
        return sample;
    }
}