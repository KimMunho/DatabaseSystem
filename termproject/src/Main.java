import java.sql.*;
import java.util.Scanner;

public class Main {

    // MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://192.168.56.101:4567/termproject";
    private static final String DB_USER = "munho";
    private static final String DB_PASSWORD = "ansgh12895*";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. 회원가입");
                System.out.println("2. 로그인");
                System.out.println("3. 종료");
                System.out.print("선택: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // 버퍼 클리어

                if (choice == 1) {
                    registerUser(conn, scanner);
                } else if (choice == 2) {
                    loginUser(conn, scanner);
                } else if (choice == 3) {
                    System.out.println("프로그램을 종료합니다.");
                    break;
                } else {
                    System.out.println("잘못된 입력입니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 회원가입
    private static void registerUser(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("이름: ");
        String name = scanner.nextLine();
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("성별(Male/Female): ");
        String gender = scanner.nextLine();
        System.out.print("주소: ");
        String address = scanner.nextLine();
        System.out.print("전화번호: ");
        String pnum = scanner.nextLine();

        String query = "INSERT INTO user (Name, ID, Gender, Address, Pnum) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            pstmt.setString(3, gender);
            pstmt.setString(4, address);
            pstmt.setString(5, pnum);
            pstmt.executeUpdate();
            System.out.println("회원가입이 완료되었습니다. 초기화면으로 이동합니다.");
        } catch (SQLException e) {
            System.out.println("회원가입 실패: " + e.getMessage());
        }
    }

    // 로그인
    private static void loginUser(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("이름: ");
        String name = scanner.nextLine();
        System.out.print("ID: ");
        String id = scanner.nextLine();

        String query = "SELECT Ssn, Role FROM user WHERE Name = ? AND ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int ssn = rs.getInt("Ssn");
                String role = rs.getString("Role");
                System.out.println("로그인 성공. 역할: " + role);
                userActions(conn, scanner, ssn, role);
            } else {
                System.out.println("로그인 실패: 이름 또는 ID가 일치하지 않습니다.");
            }
        }
    }

    // 사용자 행동
    private static void userActions(Connection conn, Scanner scanner, int ssn, String role) throws SQLException {
        while (true) {
            System.out.println("1. 클럽 생성");
            System.out.println("2. 클럽 가입");
            System.out.println("3. 클럽 관리");
            System.out.println("4. 클럽 방문");
            System.out.println("5. 종료");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 클리어

            if (choice == 1) {
                if ("General".equals(role)) {
                    createClub(conn, scanner, ssn);
                } else {
                    System.out.println("권한 부족: 클럽 생성은 General 역할만 가능합니다.");
                }
            } else if (choice == 2) {
                if ("General".equals(role)) {
                    joinClub(conn, scanner, ssn);
                } else {
                    System.out.println("권한 부족: 클럽 가입은 General 역할만 가능합니다.");
                }
            } else if (choice == 3) {
                if ("President".equals(role)) {
                    manageClub(conn, scanner, ssn);
                } else {
                    System.out.println("권한 부족: 클럽 관리는 President 역할만 가능합니다.");
                }
            } else if (choice == 4) {
                visitClub(conn, scanner);
            } else if (choice == 5) {
                System.out.println("초기화면으로 돌아갑니다.");
                break;
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 클럽 생성
    private static void createClub(Connection conn, Scanner scanner, int ssn) throws SQLException {
        System.out.print("클럽 이름: ");
        String clubName = scanner.nextLine();
        System.out.print("클럽 위치: ");
        String location = scanner.nextLine();
        System.out.print("클럽 유형: ");
        String type = scanner.nextLine();

        String query = "INSERT INTO club (Club_Name, Location, Type, PresidentID, Club_Uid) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clubName);
            pstmt.setString(2, location);
            pstmt.setString(3, type);
            pstmt.setInt(4, ssn); // 현재 사용자 ID
            pstmt.setInt(5, ssn);
            pstmt.executeUpdate();

            // 사용자의 Role을 President로 변경
            query = "UPDATE user SET Role = 'President' WHERE Ssn = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                updateStmt.setInt(1, ssn);
                updateStmt.executeUpdate();
            }
            System.out.println("클럽 생성 완료. 당신은 이제 클럽의 President입니다.");
        } catch (SQLException e) {
            System.out.println("클럽 생성 실패: " + e.getMessage());
        }
    }

    // 클럽 가입
    private static void joinClub(Connection conn, Scanner scanner, int ssn) throws SQLException {
        System.out.print("가입할 클럽 이름: ");
        String clubName = scanner.nextLine();

        String query = "SELECT Club_Name FROM club WHERE Club_Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clubName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                query = "UPDATE user SET Role = 'Member' WHERE Ssn = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                    updateStmt.setInt(1, ssn);
                    updateStmt.executeUpdate();
                }
                System.out.println("클럽 가입 성공. 당신은 이제 Member입니다.");
            } else {
                System.out.println("클럽 가입 실패: 클럽이 존재하지 않습니다.");
            }
        }
    }

    // 클럽 관리
    private static void manageClub(Connection conn, Scanner scanner, int ssn) throws SQLException {
        System.out.print("수정 또는 삭제할 클럽 이름: ");
        String clubName = scanner.nextLine();

        String query = "SELECT Club_Name FROM club WHERE Club_Name = ? AND PresidentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clubName);
            pstmt.setInt(2, ssn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("1. 클럽 속성 수정");
                System.out.println("2. 클럽 삭제");
                System.out.print("선택: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.print("새로운 클럽 위치: ");
                    String location = scanner.nextLine();
                    query = "UPDATE club SET Location = ? WHERE Club_Name = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                        updateStmt.setString(1, location);
                        updateStmt.setString(2, clubName);
                        updateStmt.executeUpdate();
                    }
                    System.out.println("클럽 정보가 수정되었습니다.");
                } else if (choice == 2) {
                    query = "DELETE FROM club WHERE Club_Name = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(query)) {
                        deleteStmt.setString(1, clubName);
                        deleteStmt.executeUpdate();
                    }
                    query = "UPDATE user SET Role = 'General' WHERE Ssn = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                        updateStmt.setInt(1, ssn);
                        updateStmt.executeUpdate();
                    }
                    System.out.println("클럽이 삭제되었습니다.");
                } else {
                    System.out.println("잘못된 선택입니다.");
                }
            } else {
                System.out.println("권한이 없거나 클럽이 존재하지 않습니다.");
            }
        }
    }

    // 클럽 방문
    private static void visitClub(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("방문할 클럽 이름: ");
        String clubName = scanner.nextLine();

        String query = "SELECT * FROM club WHERE Club_Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clubName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("클럽 이름: " + rs.getString("Club_Name"));
                System.out.println("위치: " + rs.getString("Location"));
                System.out.println("유형: " + rs.getString("Type"));
                System.out.println("회원 수: " + rs.getInt("Number_of_members"));
                System.out.println("클럽 방문 완료.");
            } else {
                System.out.println("클럽 방문 실패: 클럽이 존재하지 않습니다.");
            }
        }
    }
}
