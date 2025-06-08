package org.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.booking.Util.UserServiceUtil;
import org.booking.entities.Train;
import org.booking.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    private User user;

    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/main/java/org/booking/localDb/users.json";


    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        loadUsers();
    }
    public UserBookingService() throws IOException {
        loadUsers();
    }
    public List<User> loadUsers() throws IOException{
        File users = new File(USERS_PATH);
        return objectMapper.readValue(users, new TypeReference<List<User>>() { });
    }

    public Boolean loginUser(){
        // search user then find the user who has entered the name
        // equalsignore case ignores the case of first letter be small or capital entered by the user
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            // add the user data to the userlist database and to the file
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch(IOException ex){
            return Boolean.FALSE;
        }
    }
    private void saveUserListToFile() throws IOException{
        //add the user data to the file ie the json file ie serialisation (user Object(user) -> json file ),(json file -> object(user))
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile,userList);
    }

    public void fetchBooking(){
        user.printTickets();
    }

    public Boolean cancelBookings(String ticketId){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel:");
        ticketId = sc.next();

        if(ticketId == null || ticketId.isEmpty()){
            System.out.println("Ticket id cannot be empty or be null");
            return Boolean.FALSE;
        }
        String finalTicketId1 = ticketId;
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        if(removed){
            System.out.println("Ticket with ID "+ ticketId + "has been cancelled");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID "+ticketId);
            return Boolean.FALSE;
        }
    }
    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainservice = new TrainService();
            return trainservice.searchTrains(source,destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }
    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }
    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }
}
