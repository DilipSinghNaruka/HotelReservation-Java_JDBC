import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Scanner;

public class HotelReservation {
    private static final String url ="jdbc:mysql://localhost:3306/hotel_db";
    private static final String username ="root";
    private static final String password ="root";


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
                        reservationRoom(connection,scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        deleteReservation(connection,scanner);
                        break;
                    case 0:
                        exit();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private static void reservationRoom(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\nEnter Data :-");
        System.out.print("Enter Guest Name:-");
        String guestName = scanner.next();
        System.out.println("Enter Room Number:- ");
        int roomNumber = scanner.nextInt();
        System.out.println("Enter Contact Number");
        String contactNumber = scanner.next();
        String sql = "INSERT INTO reservation(guest_name,room_number,contact_number)"+
                "VALUES('" +guestName+ "', " + roomNumber +",'"+contactNumber +"')";

        try{
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(sql);
            if(affectedRows>0){
                System.out.println("Reservation successfull");

            }else{
                System.out.println("Reservation failed.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


private static void viewReservations(Connection connection) throws SQLException {
    String sql = "SELECT reservation_id, guest_name, room_number,contact_number,reservation_date FROM reservation";
    try {
        Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql);
        System.out.println("Current Reservations:");
        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
         while(resultSet.next()){
             int reservationId = resultSet.getInt("reservation_id");
             String guestName = resultSet.getString("guest_name");
             int roomNumber = resultSet.getInt("room_number");
             String contactNumber = resultSet.getString("contact_number");
             String reservationDate = resultSet.getTimestamp("reservation_date").toString();
             System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                     reservationId, guestName, roomNumber, contactNumber, reservationDate);

         }
    } catch (Exception e) {
        System.out.println(e);
    }

}

private static void getRoomNumber (Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();
            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";
            try(Statement statement  = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                    if(resultSet.next()){
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room number for reservation ID "+ reservationId+" and Guest "+ guestName+ " is:" + roomNumber);
                    }else{
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
            }



        }catch(SQLException e){
            e.printStackTrace();
        }
}

private static boolean reservationExists(Connection connection, int reservationId) {
        try{
            String sql = "SELECT reservation_id FROM reservation WHERE  reservation_id="+ reservationId;
            try(Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery(sql);
                return resultSet.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
}

private static void updateReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation ID to update");
            int reservationId = scanner.nextInt();

            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            System.out.println("Enter new name of guest");
            String newGuestName = scanner.next();
            System.out.println("Enter new room number");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;
            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation update is successfull");
                }else{
                    System.out.println("Reservation update failed. ");
                }
            }

        }catch (SQLException e){
            System.out.println(e);
        }
}
private static void deleteReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter your ID to delete: ");
            int reservationId = scanner.nextInt();
            if(!reservationExists(connection,reservationId)){
                System.out.println("registration id not found ");
                return;
            }
            String sql = "DELETE FROM reservation WHERE reservation_id ="+reservationId;


            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation delete succesfull !");
                }else{
                    System.out.println("Reservatio delete failed !");
                }

            }


        }catch(SQLException e){
            e.printStackTrace();
        }
}
    private static void exit() throws InterruptedException{
        System.out.print("Exiting System ");
        int i=3;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(350);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou for Using Hotel Reservation System!!");

    }


};



