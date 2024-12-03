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
                System.out.println("4. User 엔티티 조회");
                System.out.println("5. Club 엔티티 조회");
                System.out.println("6. Club_Members 엔티티 조회");
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
                } else if (choice == 4) {
                    viewUserEntity(conn);
                } else if (choice == 5) {
                    viewClubEntity(conn);
                } else if (choice == 6) {
                    viewClubMembersEntity(conn);
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
            System.out.println("5. 클럽 참여");
            System.out.println("6. 종료");
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
                if ("Member".equals(role)) {
                    participateClub(conn, ssn);
                } else {
                    System.out.println("권한 부족: 클럽 참여는 Member 역할만 가능합니다.");
                }
            } else if (choice == 6) {
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
            pstmt.setInt(4, ssn);
            pstmt.setInt(5, ssn); // Club_Uid는 President의 Ssn 사용
            pstmt.executeUpdate();

            // 사용자의 Role을 President로 업데이트
            query = "UPDATE user SET Role = 'President' WHERE Ssn = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                updateStmt.setInt(1, ssn);
                updateStmt.executeUpdate();
            }

            // club_members 테이블에 생성된 클럽 추가 (President 추가)
            query = "INSERT INTO club_members (C_Name, UserID, UserName, Role) VALUES (?, ?, ?, 'President')";
            try (PreparedStatement insertStmt = conn.prepareStatement(query)) {
                insertStmt.setString(1, clubName);
                insertStmt.setString(2, getUserId(conn, ssn)); // President의 UserID
                insertStmt.setString(3, getUserName(conn, ssn)); // President의 Name
                insertStmt.executeUpdate();
            }

            System.out.println("클럽 생성 완료. 클럽 이름: " + clubName);
        } catch (SQLException e) {
            System.out.println("클럽 생성 실패: " + e.getMessage());
        }
    }


    private static void joinClub(Connection conn, Scanner scanner, int ssn) throws SQLException {
        System.out.println("현재 가입 가능한 클럽 이름 목록:");

        // 클럽 이름만 출력
        String query = "SELECT Club_Name FROM club";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("- " + rs.getString("Club_Name"));
            }
        }

        System.out.print("가입할 클럽 이름을 입력하세요: ");
        String clubName = scanner.nextLine();

        query = "SELECT Club_Name FROM club WHERE Club_Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clubName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 삽입 전 UserID 확인
                String userId = getUserId(conn, ssn);
                if (userId == null) {
                    System.out.println("가입 실패: UserID를 찾을 수 없습니다.");
                    return;
                }

                System.out.println("삽입하려는 UserID: " + userId);

                // club_members 테이블에 사용자 추가
                query = "INSERT INTO club_members (C_Name, UserID, UserName, Role) VALUES (?, ?, ?, 'Member')";
                try (PreparedStatement insertStmt = conn.prepareStatement(query)) {
                    insertStmt.setString(1, clubName);
                    insertStmt.setString(2, userId); // UserID 삽입
                    insertStmt.setString(3, getUserName(conn, ssn));
                    insertStmt.executeUpdate();
                }

                // 사용자 Role을 Member로 업데이트
                query = "UPDATE user SET Role = 'Member' WHERE Ssn = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                    updateStmt.setInt(1, ssn);
                    updateStmt.executeUpdate();
                }

                // 해당 클럽의 number_of_members를 1 증가
                query = "UPDATE club SET Number_of_members = Number_of_members + 1 WHERE Club_Name = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                    updateStmt.setString(1, clubName);
                    updateStmt.executeUpdate();
                }

                System.out.println("클럽 가입 성공. 가입한 클럽: " + clubName);
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
                System.out.println("웹페이지: " + rs.getString("Webpage")); // 웹페이지 출력 추가
                System.out.println("회원 수: " + rs.getInt("Number_of_members"));
                System.out.println("클럽 방문 완료.");
            } else {
                System.out.println("클럽 방문 실패: 클럽이 존재하지 않습니다.");
            }
        }
    }

    // 클럽 참여
    private static void participateClub(Connection conn, int ssn) throws SQLException {
        String query = "SELECT c.* FROM club c JOIN club_members cm ON c.Club_Name = cm.C_Name WHERE cm.UserID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, ssn);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("클럽 참여 정보:");
            System.out.println("--------------------------------------------------");
            while (rs.next()) {
                System.out.println("클럽 이름: " + rs.getString("Club_Name"));
                System.out.println("위치: " + rs.getString("Location"));
                System.out.println("유형: " + rs.getString("Type"));
                System.out.println("회원 수: " + rs.getInt("Number_of_members"));
                System.out.println("--------------------------------------------------");
            }
        }
    }

    // 사용자 이름 가져오기 (유틸 메서드)
    private static String getUserName(Connection conn, int ssn) throws SQLException {
        String query = "SELECT Name FROM user WHERE Ssn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, ssn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        }
        return null;
    }

    private static String getUserId(Connection conn, int ssn) throws SQLException {
        String query = "SELECT ID FROM user WHERE Ssn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, ssn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("ID");
            }
        }
        return null;
    }


    // User 엔티티 조회
    private static void viewUserEntity(Connection conn) throws SQLException {
        String query = "SELECT * FROM user";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("User 엔티티 조회:");
            System.out.println("--------------------------------------------------");
            while (rs.next()) {
                System.out.println("Ssn: " + rs.getInt("Ssn"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("ID: " + rs.getString("ID"));
                System.out.println("Gender: " + rs.getString("Gender"));
                System.out.println("Address: " + rs.getString("Address"));
                System.out.println("Pnum: " + rs.getString("Pnum"));
                System.out.println("Role: " + rs.getString("Role"));
                System.out.println("--------------------------------------------------");
            }
        }
    }

    // 클럽 엔티티 조회
    private static void viewClubEntity(Connection conn) throws SQLException {
        String query = "SELECT * FROM club";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Club 엔티티 조회:");
            System.out.println("--------------------------------------------------");
            while (rs.next()) {
                System.out.println("Club_Name: " + rs.getString("Club_Name"));
                System.out.println("Location: " + rs.getString("Location"));
                System.out.println("Type: " + rs.getString("Type"));
                System.out.println("Number_of_members: " + rs.getInt("Number_of_members"));
                System.out.println("Webpage: " + rs.getString("Webpage"));
                System.out.println("--------------------------------------------------");
            }
        }
    }

    // Club_Members 엔티티 조회
    private static void viewClubMembersEntity(Connection conn) throws SQLException {
        String query = "SELECT * FROM club_members";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Club_Members 엔티티 조회:");
            System.out.println("--------------------------------------------------");
            while (rs.next()) {
                System.out.println("C_Name: " + rs.getString("C_Name"));
                System.out.println("UserID: " + rs.getString("UserID"));
                System.out.println("UserName: " + rs.getString("UserName"));
                System.out.println("Role: " + rs.getString("Role"));
                System.out.println("--------------------------------------------------");
            }
        }
    }

}
