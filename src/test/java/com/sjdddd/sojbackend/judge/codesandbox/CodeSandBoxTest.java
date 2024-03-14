package com.sjdddd.sojbackend.judge.codesandbox;

import com.sjdddd.sojbackend.judge.codesandbox.Impl.ExampleSandBoxImpl;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojbackend.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/3/7 19:05
 **/
@SpringBootTest
class CodeSandBoxTest {

    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCode() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String type = scanner.next();
            CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
            String code = "int main() { }";
            String language = QuestionSubmitLanguageEnum.JAVA.getValue();
            List<String> inputList = Arrays.asList("1 2", "3 4");
            ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                    .code(code)
                    .language(language)
                    .inputList(inputList)
                    .build();
            ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        }

    }

    @Test
    void executeCodeByType() {
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        String code = "int main() { }";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);

    }

    @Test
    void executeCodeByProxy() {
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果：\" + (a + b));\n" +
                "    }\n" +
                "}\n";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }

    @Test
    void executeCodeByProxyCpp() {
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String code = "#include <iostream>\n" +
                "using namespace std;\n" +
                "int main() {\n" +
                "    int a, b;\n" +
                "    cin >> a >> b;\n" +
                "    cout << \"结果：\" << a + b << endl;\n" +
                "    return 0;\n" +
                "}\n";
        String language = QuestionSubmitLanguageEnum.CPLUSPLUS.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }

    @Test
    void executeCodeByProxyPython() {
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String code = "# calculate.py\n" +
                "import sys\n" +
                "def main():\n" +
                "    if len(sys.argv) != 3:\n" +
                "        print(\"Usage: python calculate.py <a> <b>\")\n" +
                "        return\n" +
                "    a = int(sys.argv[1])\n" +
                "    b = int(sys.argv[2])\n" +
                "    result = calculate_sum(a, b)\n" +
                "    print(result)\n" +
                "def calculate_sum(a, b):\n" +
                "    return a + b\n" +
                "if __name__ == \"__main__\":\n" +
                "    main()\n";
        String language = QuestionSubmitLanguageEnum.PYTHON.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String type = scanner.next();
            CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
            String code = "int main() { }";
            String language = QuestionSubmitLanguageEnum.JAVA.getValue();
            List<String> inputList = Arrays.asList("1 2", "3 4");
            ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                    .code(code)
                    .language(language)
                    .inputList(inputList)
                    .build();
            ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        }
    }
}
