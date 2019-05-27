package chap03_람다표현식;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main03 {
    public static void main(String[] args) throws Exception {
        System.out.println("Main03 start");

        String filePath = System.getProperty("user.dir") + "\\gokseong.java.8\\data\\" + "data.txt";

        // 람다 전달, 즉 함수형 인터페이스의 인스턴스를 전달
        // Buffered br 은 파라미터 형식을 나타내는 것이지, 실제 파라미터는 당연히 아니다.
        String oneLine = processFile(filePath, (BufferedReader br) -> br.readLine());
        String twoLines = processFile(filePath, (BufferedReader br) -> br.readLine() + br.readLine());

        System.out.println(oneLine);
        System.out.println(twoLines);
    }

    // 실행 어라운드 패턴 적용
    public static String processFile(String filePath, BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // 함수형 인터페이스의 메서드를 호출 시 실제 파라미터를 전달한다.
            return p.process(br);
        }
    }
}
