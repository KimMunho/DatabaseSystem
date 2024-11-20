import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Test {
    // MySQL 연결 정보
    private static final String URL = "jdbc:mysql://192.168.56.101:4567/madang";
    private static final String USER = "munho";
    private static final String PASSWORD = "ansgh12895*";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("MySQL에 연결되었습니다.");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n옵션을 선택하세요:");
                System.out.println("1. 데이터 검색");
                System.out.println("2. 데이터 삽입");
                System.out.println("3. 데이터 삭제");
                System.out.println("4. 종료");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        searchData(con);
                        break;
                    case 2:
                        insertData(con, scanner);
                        break;
                    case 3:
                        deleteData(con, scanner);
                        break;
                    case 4:
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 데이터 검색
    private static void searchData(Connection con) {
        String query = "SELECT * FROM Book";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Book 테이블 데이터:");
            while (rs.next()) {
                System.out.printf("ID: %d, Title: %s, Author: %s%n",
                        rs.getInt("bookid"), rs.getString("bookname"), rs.getString("publisher"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 데이터 삽입
    private static void insertData(Connection con, Scanner scanner) {
        System.out.println("새 데이터를 입력하세요.");
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // 개행 문자 처리
        System.out.print("책 이름: ");
        String name = scanner.nextLine();
        System.out.print("출판사: ");
        String publisher = scanner.nextLine();

        String query = "INSERT INTO Book (bookid, bookname, publisher) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, publisher);
            pstmt.executeUpdate();
            System.out.println("데이터가 삽입되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 데이터 삭제
    private static void deleteData(Connection con, Scanner scanner) {
        System.out.print("삭제할 책의 ID를 입력하세요: ");
        int id = scanner.nextInt();

        String query = "DELETE FROM Book WHERE bookid = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("데이터가 삭제되었습니다.");
            } else {
                System.out.println("해당 ID를 가진 데이터가 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
